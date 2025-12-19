/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.timesheet;

import dal.TimesheetDAO;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 *
 * @author Admin
 */
public class AddTimesheet extends HttpServlet {

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
            out.println("<title>Servlet AddTimesheet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddTimesheet at " + request.getContextPath() + "</h1>");
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

        if (user == null) {
            response.sendRedirect("login.jsp");
            // tuy không cần return (hoặc là có) nhưng mà để đây để đánh dấu kết thúc luồng 
            return;
        }
        try {
            if (user.getRoleID() == 1) {
                //validate 
                // 1. kiểu Date
                // 2. start sau end 
                // 3. start là thứ 2 và end là chủ nhật cùng tuần
                // 4. ko có 2 timesheet cùng 1 thời điểm. - hết
                // 5. ko cho add trước tương lai - qk cũng ko 
                String dayStartString = request.getParameter("startDate");
                String dayEndString = request.getParameter("endDate");
                String errorMsg = null;

                // --- BẮT ĐẦU VALIDATE ---
                try {
                    // 1. Kiểm tra kiểu Date (Parse lỗi sẽ nhảy xuống catch)
                    if (dayStartString == null || dayEndString == null || dayStartString.isEmpty() || dayEndString.isEmpty()) {
                        errorMsg = "Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc.";
                    } else {
                        LocalDate start = LocalDate.parse(dayStartString);
                        LocalDate end = LocalDate.parse(dayEndString);

                        // 2. Kiểm tra start sau end (Vô lý)
                        if (start.isAfter(end)) {
                            errorMsg = "Ngày bắt đầu không được sau ngày kết thúc.";
                        } // 3. Kiểm tra start là Thứ 2 và end là Chủ Nhật cùng tuần
                        else if (start.getDayOfWeek() != DayOfWeek.MONDAY) {
                            errorMsg = "Ngày bắt đầu của Timesheet phải là Thứ Hai.";
                        } else if (end.getDayOfWeek() != DayOfWeek.SUNDAY) {
                            errorMsg = "Ngày kết thúc của Timesheet phải là Chủ Nhật.";
                        } else if (ChronoUnit.DAYS.between(start, end) != 6) {
                            errorMsg = "Một Timesheet phải gói gọn trong 1 tuần (từ Thứ 2 đến Chủ Nhật).";
                        } // 5. Không cho add tương lai - quá khứ (Chỉ cho phép tuần hiện tại)
                        else {
                            LocalDate today = LocalDate.now();
                            // Tìm ngày thứ 2 của tuần hiện tại
                            LocalDate currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                            if (!start.equals(currentMonday)) {
                                errorMsg = "Hệ thống chỉ cho phép tạo Timesheet cho tuần hiện tại (bắt đầu từ ngày " + currentMonday + ").";
                            } else {
                                // 4. Kiểm tra trùng lặp (Ko có 2 timesheet cùng thời điểm)
                                // Gọi xuống database check xem AccountID và start date này đã tồn tại chưa
                                if (timesheetDao.isTimesheetExist(user.getUserID(), java.sql.Date.valueOf(start))) {
                                    errorMsg = "Tuần này anh đã khai Timesheet rồi, không chơi tạo trùng nhé!";
                                    session.setAttribute("sessionError", errorMsg);
                                    response.sendRedirect(request.getContextPath() + "/ViewAndSearchTimesheet");
                                    return;
                                } else {
                                    // nếu tạo thành công
                                    boolean isCreated = timesheetDao.createTimesheet(user.getUserID(),
                                            java.sql.Date.valueOf(start),
                                            java.sql.Date.valueOf(end));
                                    if (isCreated) {
                                        session.setAttribute("sessionMessage", "Tạo Timesheet mới thành công rồi nhé!");
                                        session.setAttribute("messageType", "success");
                                    } else {
                                        session.setAttribute("sessionMessage", "Ối, tạo thất bại rồi. Có thể tuần này đã tồn tại!");
                                        session.setAttribute("messageType", "danger");
                                    }
                                    response.sendRedirect(request.getContextPath() + "/ViewAndSearchTimesheet");
                                    return;
                                }
                            }
                        }
                    }
                } catch (DateTimeParseException e) {
                    errorMsg = "Định dạng ngày tháng không đúng (yyyy-MM-dd).";
                }

                // --- XỬ LÝ KẾT QUẢ VALIDATE ---
                if (errorMsg != null) {
                    session.setAttribute("sessionError", errorMsg);
                    response.sendRedirect(request.getContextPath() + "/ViewAndSearchTimesheet");
                    return; // Dừng luồng ở đây
                }

            } else {
                //redirect về homepage tương ứng với 2 và 3, còn lại thì session.invalidate rồi tống về login
                if (user.getRoleID() == 2 || user.getRoleID() == 3) {
                    // redirect về trang tương ứng
                    response.sendRedirect(request.getContextPath() + "/supervisor/dashboard");
                    return;
                } else {
                    session.invalidate();
                    response.sendRedirect("login.jsp");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
            String errorMsg = "Đã có exception xảy ra: " + e.getMessage();
            session.setAttribute("sessionError", errorMsg);
            response.sendRedirect("/nghiapages/errorPage.jsp");
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
