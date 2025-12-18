package controller;

import dal.ProjectDAO;
import entity.Project;
import entity.UserAccount;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@WebServlet(name="SupervisorProjectListController", urlPatterns = {"/supervisor/projects"})
public class SupervisorProjectListController extends HttpServlet {
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserAccount currentUser = (UserAccount) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Check if user is supervisor (roleId == 2) or admin (roleId == 3)
        if (currentUser.getRoleID() != 2 && currentUser.getRoleID() != 3) {
            response.sendRedirect(request.getContextPath() + "/projects");
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        String searchKeyword = request.getParameter("search");
        String statusTab = request.getParameter("status");
        
        if (searchKeyword == null)
            searchKeyword = "";
        if (statusTab == null || statusTab.isEmpty())
            statusTab = "All"; // Default to "All" for supervisors to see all projects
        
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
        
        // For supervisors/admins, use roleId 3 (admin) to get all projects
        // This ensures they see ALL projects in the database
        int totalRecords = projectDAO.countProjects(
                currentUser.getUserID(), 
                3, // Use admin roleId to bypass filtering
                searchKeyword, 
                statusTab 
        );
        
        int endPage = totalRecords / pageSize;
        if (totalRecords % pageSize != 0) {
            endPage++;
        }
        
        if(pageIndex < 1) pageIndex = 1;
        if(pageIndex > endPage && endPage > 0) pageIndex = endPage;
        
        // Use roleId 3 (admin) to get all projects without user filtering
        List<Project> projectList = projectDAO.searchProjectsWithPaging(
                currentUser.getUserID(),
                3, // Use admin roleId to bypass filtering
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Supervisor Project List Controller - Shows all projects";
    }
}

