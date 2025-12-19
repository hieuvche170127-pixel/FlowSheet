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
import java.util.List;

@WebServlet(name = "AbsenceListController", urlPatterns = {"/request"})
public class AbsenceListController extends HttpServlet {

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
            out.println("<title>Servlet AbsenceListController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AbsenceListController at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
//        if (user == null) {
//            // Tạo ra một user giả
//            user = new UserAccount();
//            user.setUserId(2); // QUAN TRỌNG: ID này phải tồn tại trong bảng UserAccount
//            user.setUsername("sup_hoa");
//            user.setFullName("Nguyen Thi Hoa (Test)");
//            user.setRoleId(2); // Role Supervisor
//
//            session.setAttribute("user", user);
//            System.out.println("--- ĐÃ KÍCH HOẠT CHẾ ĐỘ TEST USER ---");
//        }
        
        if (user == null) {
        
        user = new UserAccount();
        user.setUserID(3); 
        user.setUsername("stu_anh");
        user.setFullName("Nguyen Hoang Anh (Test)");
        user.setRoleID(1);

        session.setAttribute("user", user);
        System.out.println("--- ĐÃ KÍCH HOẠT CHẾ ĐỘ TEST USER ---");
        }
        
//        if (user == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }

        String statusFilter = request.getParameter("statusFilter");
        if (statusFilter == null) {
            statusFilter = "PENDING";
        }

        RequestDAO dao = new RequestDAO();

        List<LeaveRequest> list = dao.getLeaveRequests(user.getUserID(), user.getRoleID(), statusFilter);

        request.setAttribute("leaveList", list);
        request.setAttribute("currentFilter", statusFilter);
        request.getRequestDispatcher("AbsenceRequestList.jsp").forward(request, response);
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
