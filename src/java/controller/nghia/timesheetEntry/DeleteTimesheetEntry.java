/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.timesheetEntry;

import dal.TimesheetDAO;
import dal.TimesheetEntryDAO;
import entity.TimeSheet;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import java.time.DayOfWeek; // Thằng này để chỉ định Thứ 2 (DayOfWeek.MONDAY)
import java.time.temporal.TemporalAdjusters; // Thằng này là "phù thủy" để tìm Thứ 2 của tuần

/**
 *
 * @author Admin
 */
public class DeleteTimesheetEntry extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DeleteTimesheetEntry</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteTimesheetEntry at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8"); // Đặt Content Type và Encoding

        HttpSession session = request.getSession(true);
        UserAccount user = (UserAccount) session.getAttribute("user");
        TimesheetDAO timesheetDao = new TimesheetDAO();
        TimesheetEntryDAO tsEntryDao = new TimesheetEntryDAO();

        ArrayList<String> errorList = new ArrayList<>();
        if (user == null) {
            response.sendRedirect("login.jsp");
            // tuy không cần return (hoặc là có) nhưng mà để đây để đánh dấu kết thúc luồng 
            return;
        }
        int roleId = user.getRoleID();

        if (roleId == 2 || roleId == 3) {
            response.sendRedirect(request.getContextPath() + "/supervisor/dashboard");
            return;
        }
        if (roleId == 1) {
            // bắt exception chung nếu có lỗi thì chuyển về timesheetdetail, nếu ko lấy được timesheet detail thì chuyển về chỗ
            // error page
            try {
                String timesheetidString = request.getParameter("timesheetId");
                int timesheetId = Integer.parseInt(timesheetidString);
                try {
                    String deletedTimesheetEntryIdString = request.getParameter("DeletedTimesheetEntryId");
                    int deletedTimesheetEntryIdInt = Integer.parseInt(deletedTimesheetEntryIdString);

                    // 1. check xem thằng xóa có đúng ng tạo ko :))) ôi má ơi thôi dùm tôi cái
                    TimeSheet getTimesheetById = timesheetDao.getTimesheetByTimesheetId(timesheetId);
                    if (getTimesheetById.getUserId() != user.getUserID()) {
                        errorList.add("người tạo và người add timesheet ko trùng khớp");
                    }
                    // 4. check xem thằng timesheet có chứa timesheet id ko ?, xem có đúng ko :))
                    // cắm thẳng vào database mà mò thử xem, chứ xác nhận qua client data thì hơi ngu
                    // nếu ko tồn tồn tại - tức là nhầm thằng hoặc như nào đấy -> 
                    if (!tsEntryDao.isTimesheetEntryBelongToTimesheet(timesheetId, deletedTimesheetEntryIdInt)) {
                        errorList.add("timesheet entry bị xóa không thuộc timesheet hiện tại!, ko có quyền xóa.");
                    }

                    // 3. check xem thằng timesheet kia có cho edit ko? -ý là có đúng tuần ko, nếu
                    // mà là của tuần trước thì ko cho thay đổi nữa, tuần trước rồi thay đổi làm gì :)).
                    
                    // 3.1: Lấy mốc Thứ 2 tuần này (Dùng LocalDate cho lành anh ạ)
                    LocalDate mondayOfThisWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    // 3.2: lấy ngày end của timesheet rồi chuyển về dạng localdate.
                    LocalDate tsDayEnd = getTimesheetById.getDayEnd().toLocalDate();
                    // 3.3: So sánh: Nếu ngày kết thúc của Timesheet trước Thứ 2 tuần này
                    if (tsDayEnd.isBefore(mondayOfThisWeek)) {
                        errorList.add("Timesheet này đã là quá khứ (trước tuần hiện tại), không thể thay đổi/ cập nhật.");
                    }
                    
                    // 2. check xem cái timesheetentry này có xóa được ko ? (dùng timesheetDao- vieeste hàm mới thôi)
                    //  2.1 ko có reportTask nào tham chiếu đến nó
                    if (tsEntryDao.isEntryContainAnyTaskReport(deletedTimesheetEntryIdInt)) {
                        errorList.add("timesheet entry này đã có nội dung task report, không thể xóa."
                                + "nếu muốn xóa, hãy xóa các task report liên quan (trong phần view detail) trước. ");
                    }

                    if (!errorList.isEmpty()) {
                        session.setAttribute("errorList", errorList);
                        response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                        return;
                    }
                    // ko cần else do tự ngắt luồng.

                    boolean deleteStatus = false;
                    deleteStatus = tsEntryDao.deleteTimesheetEntry(deletedTimesheetEntryIdInt);
                    if (deleteStatus) {
                        session.setAttribute("info", "delete timesheet entry thành cônggg");
                    } else {
                        session.setAttribute("info", "delete timesheet entry thất bại - có thể do DAO");
                    }
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                    return;

                } catch (Exception e) {
                    errorList.add("đã có lỗi khi parse dữ liệu hoặc lúc xóa");
                    session.setAttribute("errorList", errorList);
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                    return;
                }
            } catch (Exception e) {
                errorList.add("đã có excpetion xảy ra :)), vui lòng liên hệ team dev " + e.getMessage());
                session.setAttribute("errorList", errorList);
                response.sendRedirect("/nghiapages/errorPage.jsp");
                return;
            }
        } else {
            session.invalidate();
            response.sendRedirect("login.jsp");
            return;
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
