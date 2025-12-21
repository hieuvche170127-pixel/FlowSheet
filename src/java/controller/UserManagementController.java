package controller;

import dal.UserAccountDAO;
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

@WebServlet(name = "UserManagementController", urlPatterns = {"/admin/users"})
public class UserManagementController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        return user != null && user.getRoleID() == 3;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserManagementController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserManagementController at " + request.getContextPath() + "</h1>");
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
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You are not Admin.");
            return;
        }

        String search = request.getParameter("search");
        if (search == null) search = "";

        String roleFilter = request.getParameter("roleFilter");
        if ("".equals(roleFilter)) roleFilter = null;

        int page = 1;
        int pageSize = 20; 
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        UserAccountDAO dao = new UserAccountDAO();
        int totalUsers = dao.countUsers(search, roleFilter);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        List<UserAccount> list = dao.getUsers(search, roleFilter, page, pageSize);

        request.setAttribute("listUsers", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("search", search);
        request.setAttribute("roleFilter", roleFilter); 
        request.getRequestDispatcher("/UserManagement.jsp").forward(request, response);
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
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        UserAccountDAO dao = new UserAccountDAO();
        String action = request.getParameter("action");
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));

            if ("deactivate".equals(action)) {
                dao.updateUserStatus(userId, false);
            } else if ("activate".equals(action)) {
                dao.updateUserStatus(userId, true); 
            } else if ("delete".equals(action)) {
                dao.deleteUserEmail(userId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect("users");
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
