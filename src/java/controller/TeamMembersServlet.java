/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.ProjectDAO;
import dao.RoleDAO;
import dal.TeamDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import entity.TeamMember;
import dal.TeamMemberDAO;
import dal.TeamProjectDAO;
import dao.UserDAO;
import entity.Project;
import java.util.HashMap;
import java.util.Map;
import entity.Role;
import entity.Team;
import entity.User;
import entity.UserAccount;

/**
 *
 * @author Admin
 */
public class TeamMembersServlet extends HttpServlet {

    private TeamMemberDAO teamMemberDAO = new TeamMemberDAO();

    private UserDAO userAccountDAO = new UserDAO();
    private RoleDAO roleDAO = new RoleDAO();

    private TeamDAO teamDAO = new TeamDAO();

    private ProjectDAO projectDAO = new ProjectDAO();

    private TeamProjectDAO teamProjectDAO = new TeamProjectDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TeamMemberServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TeamMemberServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        try {
            // Get data from DB
            List<TeamMember> teamMembers = teamMemberDAO.findAll();

            // Put list into request scope
            request.setAttribute("teamMembers", teamMembers);

            // Forward to JSP for display
            request.getRequestDispatcher("/teamMember-list.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
         */

        //UPDATE
        String teamIdParam = request.getParameter("teamId");
        if (teamIdParam == null) {
            // no teamId → you can redirect back or show error; for now go back
            response.sendRedirect("team");
            return;
        }

        int teamId = Integer.parseInt(teamIdParam);
        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "members";
        }

        String memberKeyword = request.getParameter("q"); // từ ô search

        try {
            Team team = teamDAO.findById(teamId);

            List<User> members;
            if (memberKeyword != null && !memberKeyword.trim().isEmpty()) {
                members = userAccountDAO.findMembersByTeamAndKeyword(teamId, memberKeyword.trim());
            } else {
                members = userAccountDAO.findMembersByTeam(teamId);
            }       // students + supervisors

            List<Project> projects = projectDAO.findProjectsByTeam(teamId);

            String message = request.getParameter("msg");
            if (message != null) {
                request.setAttribute("msg", message);
            }

            request.setAttribute("team", team);
            request.setAttribute("userAccounts", members);
            request.setAttribute("teamRoles", roleDAO.findTeamRoles());
            request.setAttribute("projects", projects);
            request.setAttribute("activeTab", tab);
            request.setAttribute("q", memberKeyword);

            request.getRequestDispatcher("/teamMember-list.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("team");
        } catch (SQLException e) {
            throw new ServletException(e);
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
        String action = request.getParameter("action");
        String teamIdParam = request.getParameter("teamId");

        if (teamIdParam == null) {
            response.sendRedirect("team");
            return;
        }
        int teamId;
        try {
            teamId = Integer.parseInt(teamIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("team");
            return;
        }

        try {
            if ("deleteTeam".equals(action)) {
                // Xử lý delete team (phần 4 – bên dưới, dùng TeamDAO)
                deleteTeamAndRelations(teamId);
                response.sendRedirect(request.getContextPath() + "/team");
                return;
                
            } else if ("kick".equals(action)) {

                // 1) lấy userId từ form
                String userIdParam = request.getParameter("userId");
                int userId = Integer.parseInt(userIdParam);

                // 2) gọi DAO kick
                boolean ok = teamMemberDAO.kickMember(teamId, userId);

                // 3) quay lại tab members và báo msg
                response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg="
                        + (ok ? "Kicked+successfully" : "Kick+failed"));
                return;

            } else if ("changeRole".equals(action)) {

                String userIdParam = request.getParameter("userId");
                String roleIdParam = request.getParameter("roleId");

                int userId = Integer.parseInt(userIdParam);
                int newRoleId = Integer.parseInt(roleIdParam);

                boolean ok = teamMemberDAO.changeRole(teamId, userId, newRoleId);

                response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg="
                        + (ok ? "Role+updated" : "Role+update+failed"));
                return;

            } else if ("updateTeam".equals(action)) {

                String teamName = request.getParameter("teamName");
                String description = request.getParameter("description");

                boolean ok = teamDAO.updateTeamInfo(teamId, teamName, description);

                response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg="
                        + (ok ? "Team+updated" : "Team+update+failed"));
                return;
            }
            
            // action không hợp lệ
            response.sendRedirect("teamMember?teamId=" + teamId);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void deleteTeamAndRelations(int teamId) throws SQLException {
        // xóa TeamProject, TeamMember, rồi Team
        teamProjectDAO.deleteByTeam(teamId);
        teamMemberDAO.deleteByTeam(teamId);
        teamDAO.deleteTeam(teamId);
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
