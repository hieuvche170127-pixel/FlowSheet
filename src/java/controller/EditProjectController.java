package controller;

import dal.ProjectDAO;
import dal.UserAccountDAO;
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
import java.sql.Date;
import java.util.List;

@WebServlet(name = "EditProjectController", urlPatterns = {"/project/edit"})
public class EditProjectController extends HttpServlet {

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
            out.println("<title>Servlet EditProjectController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditProjectController at " + request.getContextPath() + "</h1>");
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
        
//        if (user == null || user.getRoleID() == 1) {
//            response.sendRedirect("projects?error=access_denied");
//            return;
//        }
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("projects");
            return;
        }

        try {
            int projectId = Integer.parseInt(idStr);
            ProjectDAO projectDAO = new ProjectDAO();
            UserAccountDAO userDAO = new UserAccountDAO();

            Project project = projectDAO.getProjectById(projectId);
            if (project == null) {
                response.sendRedirect("projects");
                return;
            }

            List<UserAccount> currentMembers = projectDAO.getMembersInProject(projectId);

            List<UserAccount> allUsers = userDAO.getAllUsersForTeam();

            request.setAttribute("project", project);
            request.setAttribute("currentMembers", currentMembers);
            request.setAttribute("allUsers", allUsers);

            request.getRequestDispatcher("/EditProject.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("projects");
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
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null || user.getRoleID() == 1) {
            response.sendRedirect("projects");
            return;
        }

        try {
            ProjectDAO projectDAO = new ProjectDAO();
            UserAccountDAO userDAO = new UserAccountDAO();

            int projectId = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String status = request.getParameter("status");
            String description = request.getParameter("description");

            String startStr = request.getParameter("startDate");
            String endStr = request.getParameter("deadline");
            Date startDate = (startStr != null && !startStr.isEmpty()) ? Date.valueOf(startStr) : null;
            Date deadline = (endStr != null && !endStr.isEmpty()) ? Date.valueOf(endStr) : null;

            Project p = new Project();
            p.setProjectID(projectId);
            p.setProjectName(name);
            p.setStatus(status);
            p.setDescription(description);
            p.setStartDate(startDate);
            p.setDeadline(deadline);

            if (startDate != null && deadline != null && deadline.before(startDate)) {
                request.setAttribute("errorMessage", "Invalid Date: Deadline cannot be before Start Date.");
                request.setAttribute("project", p);

                List<UserAccount> currentMembers = projectDAO.getMembersInProject(projectId);
                List<UserAccount> allUsers = userDAO.getAllUsersForTeam();
                request.setAttribute("currentMembers", currentMembers);
                request.setAttribute("allUsers", allUsers);
                request.getRequestDispatcher("/EditProject.jsp").forward(request, response);
                return;
            }

            projectDAO.updateProjectInfo(p);

            String[] newMembers = request.getParameterValues("new_members[]");
            String[] newRoles = request.getParameterValues("new_roles[]");

            if (newMembers != null) {
                for (int i = 0; i < newMembers.length; i++) {
                    String username = newMembers[i];
                    String role = newRoles[i];
                    int userId = userDAO.getUserIdByUsername(username);
                    if (userId != -1) {
                        projectDAO.addMemberToProject(projectId, userId, role);
                    }
                }
            }
            String[] memberIds = request.getParameterValues("exist_member_ids[]");
            String[] memberRoles = request.getParameterValues("exist_member_roles[]");

            if (memberIds != null) {
                for (int i = 0; i < memberIds.length; i++) {
                    int uid = Integer.parseInt(memberIds[i]);
                    String newRole = memberRoles[i];

                    // Gọi hàm Update Role
                    projectDAO.updateMemberRole(projectId, uid, newRole);
                }
            }
            String deletedIdsStr = request.getParameter("deleted_members");
            if (deletedIdsStr != null && !deletedIdsStr.trim().isEmpty()) {
                String[] ids = deletedIdsStr.split(",");
                for (String id : ids) {
                    try {
                        int uidToDelete = Integer.parseInt(id.trim());
                        projectDAO.removeMemberFromProject(projectId, uidToDelete);
                    } catch (NumberFormatException e) {
                    }
                }
            }

            response.sendRedirect(request.getContextPath() + "/project/details?id=" + projectId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating project: " + e.getMessage());
            request.getRequestDispatcher("/EditProject.jsp").forward(request, response);
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
