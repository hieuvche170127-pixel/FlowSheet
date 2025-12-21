package controller;

import dal.RequestDAO;
import entity.LeaveRequest;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@WebServlet(name = "CreateAbsenceRequestController", urlPatterns = {"/request/create"})
public class CreateAbsenceRequestController extends HttpServlet {

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
            out.println("<title>Servlet CreateAbsenceRequestController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreateAbsenceRequestController at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        request.setCharacterEncoding("UTF-8");
        
        try {
            HttpSession session = request.getSession();
            UserAccount user = (UserAccount) session.getAttribute("user");

            String fromDateStr = request.getParameter("fromDate");
            String toDateStr = request.getParameter("toDate");
            String reason = request.getParameter("reason");

            LocalDate fromDate = LocalDate.parse(fromDateStr);
            LocalDate toDate = LocalDate.parse(toDateStr);
            LocalDate today = LocalDate.now();

            if (fromDate.isBefore(today)) {
                response.sendRedirect(request.getContextPath() + "/request?error=past_date");
                return;
            }

            if (fromDate.isAfter(toDate)) {
                response.sendRedirect(request.getContextPath() + "/request?error=invalid_range");
                return;
            }
            
            long days = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
            if (days <= 0) {
                response.sendRedirect(request.getContextPath() + "/request?error=invalidDate");
                return;
            }

            LeaveRequest dto = new LeaveRequest();
            dto.setUserId(user.getUserID());
            dto.setFromDate(fromDate);
            dto.setToDate(toDate);
            dto.setReason(reason);
            dto.setDurationDays((int) days);

            RequestDAO dao = new RequestDAO();
            dao.createLeaveRequest(dto);

            response.sendRedirect(request.getContextPath() + "/request?msg=success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/request?error=system");
        }
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
