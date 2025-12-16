package controller;

import dal.ProjectDAO;
import entity.Project;
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


@WebServlet(name="ProjectListController", urlPatterns = {"/projects"})
public class ProjectListController extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Servlet ProjectListController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProjectListController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserAccount currentUser = (UserAccount) session.getAttribute("LOGIN_USER");
        
        if (currentUser == null) {
        
        currentUser = new UserAccount();
        currentUser.setUserID(3); 
        currentUser.setUsername("stu_anh");
        currentUser.setFullName("Nguyen Hoang Anh (Test)");
        currentUser.setRoleID(1);

        session.setAttribute("user", currentUser);
        System.out.println("--- ĐÃ KÍCH HOẠT CHẾ ĐỘ TEST USER ---");
    }
        
//        if (currentUser == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
        
        request.setCharacterEncoding("UTF-8");
        String searchKeyword = request.getParameter("search");
        String statusTab = request.getParameter("status");
        
        if (searchKeyword == null)
            searchKeyword = "";
        if (statusTab == null || statusTab.isEmpty())
            statusTab = "ACTIVE";
        
        ProjectDAO projectDAO = new ProjectDAO();
        String pageStr = request.getParameter("page");
        int pageIndex = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                pageIndex = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                pageIndex = 1;
            }
        }
        int pageSize = 10;
        
        int totalRecords = projectDAO.countProjects(
                currentUser.getUserID(),
                currentUser.getRoleID(),
                searchKeyword, 
                statusTab 
        );
        
        int endPage = totalRecords / pageSize;
        if (totalRecords % pageSize != 0) {
            endPage++;
        }
        
        if(pageIndex < 1) pageIndex = 1;
        if(pageIndex > endPage && endPage > 0) pageIndex = endPage;
        
        List<Project> projectList = projectDAO.searchProjectsWithPaging(
                currentUser.getUserID(),
                currentUser.getRoleID(),
                searchKeyword,
                statusTab,
                pageIndex,
                pageSize
        );
        
        request.setAttribute("projectList", projectList);
        request.setAttribute("currentSearch", searchKeyword);
        request.setAttribute("currentStatus", statusTab);
        request.setAttribute("currentPage", pageIndex);
        request.setAttribute("endPage", endPage);
        
        request.getRequestDispatcher("/ProjectList.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
