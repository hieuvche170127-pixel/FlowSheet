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
public class DeleteTimesheet extends HttpServlet {

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
            out.println("<title>Servlet DeleteTimesheet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteTimesheet at " + request.getContextPath() + "</h1>");
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
        // nhận cái timesheetid về, sau đó check xóa được hay ko (ko chứa timesheetentry nào)
//      // -> xóa hoặc có lỗi thì đều đưa về controller lấy all timesheet
        ArrayList<String> errorList = new ArrayList<>();
        try {
            if (user.getRoleID() == 1) {
                String timesheetIdString = request.getParameter("timesheetId");
                int timesheetIdInt = Integer.parseInt(timesheetIdString);
                boolean isAbleToDelte = timesheetDao.isTimesheetAbleToDelete(timesheetIdInt, user.getUserID());
                if (!isAbleToDelte) {
                    errorList.add("this timesheet is not able to delete");
                }
                //sau validate
                if (errorList.isEmpty()) {
                    // xóa và gửi về status ch obene mytimesheetlist.
                    if (timesheetDao.deleteTimesheetById(timesheetIdInt)) {
                        session.setAttribute("info", "Xóa Timesheet thành công!");
                    } else {
                        session.setAttribute("info", "Lỗi hệ thống, không thể xóa!");
                    }
                    // neu thanh cong hoac that bai thi vao all timesheet chu nhi, tai day la xoa timesheet ma
                    request.getRequestDispatcher("/ViewAndSearchTimesheet").forward(request, response);
                    return;
                } else {
                    session.setAttribute("errorList", errorList);
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
