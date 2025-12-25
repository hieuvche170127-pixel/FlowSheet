/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.timesheet;

import dal.TaskReportDAO;
import dal.TimesheetDAO;
import dal.TimesheetEntryDAO;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class UpdateTimesheet extends HttpServlet {

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
            out.println("<title>Servlet UpdateTimesheet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateTimesheet at " + request.getContextPath() + "</h1>");
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
        TimesheetEntryDAO timesheetEntryDao = new TimesheetEntryDAO();
        TaskReportDAO taskreportDao = new TaskReportDAO();

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        ArrayList<String> errorList = new ArrayList<>();
        try {
            if (user.getRoleID() == 1) {
                // nếu ko tìm thấy/ nhận được xuống exception -> ném về view all timesheet
                String timesheetIdString = request.getParameter("timesheetId");
                String rawSummary = request.getParameter("summary").trim();
                String cleanSummary = "";
                if (rawSummary != null) {
                    // 1. trim() để bỏ 2 đầu
                    // 2. replaceAll("\\s+", " ") để biến mọi cụm khoảng trắng (2, 3, 4 dấu cách) thành 1 dấu duy nhất
                    cleanSummary = rawSummary.trim().replaceAll("\\s+", " ");
                }
                int timesheetIdInt = Integer.parseInt(timesheetIdString);
                // validate người dùng là chủ sở hữu timesheet
                // timesheet chưa được review && là của tuần này - cái gì ở qk thì thôi, đừng cập nhật nữa. 
                // count timesheet where timesheet id = And status!= reviewed timesheet. start là thứ 2 tuần này - hay tùy. 
                // sumarry <2000 kí tự. -sau trim.
                if (cleanSummary.length() > 2000) {
                    errorList.add("sumaary String quá dài, ko thể update");
                }
                boolean isAbleToUpdate = timesheetDao.isAbleToUpdateTimesheet(timesheetIdInt, user.getUserID());
                if (!isAbleToUpdate) {
                    errorList.add("timesheet này đã ko còn có thể thay đổi do đã qua tuần.");
                }

                if (errorList.isEmpty()) {
                    boolean updateResult = timesheetDao.updateTimesheetSummary(timesheetIdInt, cleanSummary);
                    // nếu update được thì gửi về là update thành công
                    // không fthif gửi về thất bại
                    // đều gửi về cái servlet detail hết. xong để thk jsp lấy session rồi teminate cái ses atribute đi.
                    if (updateResult) {
                        session.setAttribute("info", "cập nhật nội dung timesheet thành công!");
                    } else {
                        session.setAttribute("info", "cập nhật nội dung timesheet thất bại!");
                    }
                    // chỉnh url theo cái timesheetdetail servlet
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetIdInt);
                    return;
                } else {
                    session.setAttribute("errorList", errorList);
                }
                response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetIdInt);
                return;

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
        } catch (NumberFormatException numberFormatException) {
            errorList.add("ko lấy được id của timesheet");
            session.setAttribute("errorList", errorList);
            request.getRequestDispatcher("/ViewAndSearchTimesheet").forward(request, response);
        } catch (Exception e) {
            errorList.add("Đã có exception xảy ra");
            session.setAttribute("errorList", errorList);
            request.getRequestDispatcher("/ViewAndSearchTimesheet").forward(request, response);
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
