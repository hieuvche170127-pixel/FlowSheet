/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.timesheet;

import dal.TaskReportDAO;
import dal.TimesheetDAO;
import dal.TimesheetEntryDAO;

import entity.TimeSheet;
import entity.TimesheetEntry;
import entity.UserAccount;
import entity.TaskReport;

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
public class ViewDetailTimesheet extends HttpServlet {

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
            out.println("<title>Servlet ViewDetailTimesheet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ViewDetailTimesheet at " + request.getContextPath() + "</h1>");
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

        //setup
        // lười kiểm tra 1 hay nhiều lỗi lắm nên để cái này cho chung chung. 
        ArrayList<String> errorList = new ArrayList<>();
        // kiểm tra xem thk người dùng có phải student hay ko ? 
        if (user.getRoleID() == 1) {
            try {
                // view timesheet BR: 
                // chỉ thk tạo ra mới được xem
                // nhận từ client:  id của student (user session), id của timesheet.

                // luồng trong đầu: 
                // validate trước
                // check xem có parse được ko? thì xuống nghiệp vụ :(ko) thì lại gửi về lỗi 
                // nghiệp vụ
                // 1. lấy timesheet ra -> check có null ko ? (nếu null) gửi lỗi và gửi về mytimesheetlist : (ko null - có timesheet) xuống dòng 2.
                // 2. check xem có đúng người tạo ko ? gửi timesheet này và các timesheet entry/ tasktrong timesheet entry/ taskreport trong tuần về  : (ko) thì cho về trang mytimesheetlist
                // thực hiện hóa luồng: 
                // validate trước
                // check xem có parse được ko? thì xuống nghiệp vụ :(ko) thì lại gửi về lỗi // có try catch rồi nên ko cần if else
                String timesheetIdString = request.getParameter("timesheetId");
                int timesheetIdInt = Integer.parseInt(timesheetIdString);

                TimeSheet foundById = timesheetDao.getTimesheetByTimesheetId(timesheetIdInt);
                if (foundById == null) {
                    errorList.add("ko tìm thấy timesheet");
                    request.setAttribute("errorList", errorList);
                    request.getRequestDispatcher("/ViewAndSearchTimesheet").forward(request, response);
                    return;
                }

                if (foundById.getUserId() != user.getUserID()) {
                    errorList.add("timesheet này ko phải của cậuuuuuu");
                    request.setAttribute("errorList", errorList);
                    request.getRequestDispatcher("/").forward(request, response);
                    return;
                }

                // lấy được timesheet của nó r
                // giờ lấy thêm các timesheet entry ứng với timesheet 
                // ban than cai timesheetentry ko can timesheetid(FK) no tu gan theo ngay roi, nhung thoi ke, ko ai biet 
                // lấy thêm reporttask trong tuần (ứng với timesheetentry)
                // giờ nghĩ lại thấy nên chuyển từ mqh từ timesheetentry đến report chuyển thành đến task, còn timesheet report thì lấy theo ngày là được
                ArrayList<TimesheetEntry> timesheetEntryInTimesheet = timesheetEntryDao.getEntriesByTimesheetId(timesheetIdInt);

                request.setAttribute("timesheetEntry", timesheetEntryInTimesheet);
                request.setAttribute("timesheet", foundById);
                request.getRequestDispatcher("/nghiapages/timesheetjsp/timesheetdetail.jsp").forward(request, response);
                return;

            } catch (NumberFormatException numberFormatException) {
                errorList.add("ko lấy được id của timesheet");
                request.setAttribute("errorList", errorList);
                request.getRequestDispatcher("/ViewAndSearchTimesheet").forward(request, response);
            } catch (Exception e) {
                errorList.add("Đã có exception xảy ra");
                request.setAttribute("errorList", errorList);
                request.getRequestDispatcher("/ViewAndSearchTimesheet").forward(request, response);
            }
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
