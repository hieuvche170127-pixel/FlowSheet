package controller;

import dal.ProjectDAO;
import dal.TeamDAO;
import dal.UserAccountDAO;
import entity.Project;
import entity.Team;
import entity.TeamMember;
import entity.TeamProject;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "CreateTeamController", urlPatterns = {"/team/create"})
public class CreateTeamController extends HttpServlet {

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
            out.println("<title>Servlet CreateTeamController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreateTeamController at " + request.getContextPath() + "</h1>");
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
        UserAccount currentUser = (UserAccount) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        ProjectDAO dao = new ProjectDAO();

        List<Team> teams = dao.getAllTeamsForProject();
        List<UserAccount> users = dao.getAllActiveMembers();

        request.setAttribute("teamList", teams);
        request.setAttribute("userList", users);

        request.getRequestDispatcher("/CreateProject.jsp").forward(request, response);
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
            String name = request.getParameter("projectName");
            String code = request.getParameter("projectCode");
            String startStr = request.getParameter("startDate");
            String endStr = request.getParameter("deadline");
            String desc = request.getParameter("description");

            Date startDate = (startStr != null && !startStr.isEmpty()) ? Date.valueOf(startStr) : null;
            Date deadline = (endStr != null && !endStr.isEmpty()) ? Date.valueOf(endStr) : null;

            Project p = new Project();
            p.setProjectName(name);
            p.setProjectCode(code);
            p.setStartDate(startDate);
            p.setDeadline(deadline);
            p.setDescription(desc);
            p.setStatus("OPEN");

            ProjectDAO dao = new ProjectDAO();
            int newProjectId = dao.createProject(p);
            
            if (startDate != null && deadline != null && startDate.after(deadline)) {
                request.setAttribute("error", "Start Date cannot be later than Deadline!");
                request.setAttribute("p", p);
                doGet(request, response);
                return;
            }
            
            if (newProjectId > 0) {
                String assignType = request.getParameter("assignType");

                if ("team".equals(assignType)) {
                    String teamIdStr = request.getParameter("teamId");
                    if (teamIdStr != null && !teamIdStr.isEmpty()) {
                        try {
                            int teamId = Integer.parseInt(teamIdStr);
                            dao.addTeamToProject(newProjectId, teamId);
                            dao.importMembersFromTeam(newProjectId, teamId);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                } else if ("member".equals(assignType)) {
                    String[] memberIdsRaw = request.getParameterValues("memberIds");
                    String[] memberRoles = request.getParameterValues("memberRoles");

                    if (memberIdsRaw != null) {
                        for (int i = 0; i < memberIdsRaw.length; i++) {
                            String uidStr = memberIdsRaw[i];
                            if (uidStr != null && !uidStr.isEmpty()) {
                                try {
                                    int uid = Integer.parseInt(uidStr);
                                    String role = "Member";
                                    if (memberRoles != null && i < memberRoles.length) {
                                        role = memberRoles[i];
                                    }
                                    dao.addMemberToProject(newProjectId, uid, role);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/projects");
            } else {
                request.setAttribute("error", "Create project failed! Project Code existed.");
                request.setAttribute("p", p);
                doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/views/error.jsp");
        }
    }

private void handleError(HttpServletRequest request, HttpServletResponse response, String msg)
        throws ServletException, IOException {
    request.setAttribute("error", msg);
    // Load lại dropdown data để người dùng không thấy dropdown trống trơn
    doGet(request, response);
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
