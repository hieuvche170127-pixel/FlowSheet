package controller;

import dal.ProjectDAO;
import dal.TeamDAO;
import dal.TeamMemberDAO;
import dal.TeamProjectDAO;

import dao.RoleDAO;
import dao.UserDAO;

import entity.Project;
import entity.Role;
import entity.Team;
import entity.UserAccount;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamMembersServlet extends HttpServlet {

    private final TeamMemberDAO teamMemberDAO = new TeamMemberDAO();
    private final UserDAO userAccountDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final TeamDAO teamDAO = new TeamDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final TeamProjectDAO teamProjectDAO = new TeamProjectDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) tab = "members";

        String memberKeyword = request.getParameter("q");

        UserAccount current = (UserAccount) request.getSession().getAttribute("user");
        if (current == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            Team team = teamDAO.findById(teamId);

            // roles for dropdown/labels (only 4 & 5)
            List<Role> teamRoles = roleDAO.findTeamRoles();
            Map<Integer, String> roleMap = new HashMap<>();
            for (Role r : teamRoles) {
                // NOTE: if your Role entity uses getRoleID() instead of getRoleId(), rename here.
                roleMap.put(r.getRoleId(), r.getRoleName());
            }

            // members list
            List<UserAccount> members;
            if (memberKeyword != null && !memberKeyword.trim().isEmpty()) {
                members = userAccountDAO.findMembersByTeamAndKeyword(teamId, memberKeyword.trim());
            } else {
                members = userAccountDAO.findMembersByTeam(teamId);
            }

            // projects tab
            List<Project> projects = projectDAO.findProjectsByTeam(teamId);

            // permission flag for JSP
            boolean canManageTeam = canManageTeam(current, teamId);

            String message = request.getParameter("msg");
            if (message != null) request.setAttribute("msg", message);

            request.setAttribute("team", team);
            request.setAttribute("userAccounts", members);
            request.setAttribute("teamRoles", teamRoles);
            request.setAttribute("roleMap", roleMap);
            request.setAttribute("projects", projects);
            request.setAttribute("activeTab", tab);
            request.setAttribute("q", memberKeyword);

            // for JSP show/hide
            request.setAttribute("canManageTeam", canManageTeam);

            request.getRequestDispatcher("/teamMember-list.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserAccount current = (UserAccount) request.getSession().getAttribute("user");
        if (current == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

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

        boolean isManageAction =
                "kick".equals(action) ||
                "changeRole".equals(action) ||
                "deleteTeam".equals(action) ||
                "updateTeam".equals(action);

        try {
            // If it's a management action: enforce permission
            if (isManageAction && !canManageTeam(current, teamId)) {
                response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=No+permission");
                return;
            }

            // For kick/changeRole: enforce target rules
            if ("kick".equals(action) || "changeRole".equals(action)) {
                int targetUserId = Integer.parseInt(request.getParameter("userId"));

                // prevent self-modify
                if (targetUserId == current.getUserID()) {
                    response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=Cannot+modify+yourself");
                    return;
                }

                // student leader restrictions (cannot manage leaders; cannot manage non-students)
                if (isStudentLeader(current, teamId)) {
                    Integer targetTeamRole = teamMemberDAO.getTeamRoleId(teamId, targetUserId);
                    if (targetTeamRole != null && targetTeamRole == 5) {
                        response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=Leader+cannot+modify+leader");
                        return;
                    }

                    int targetAccountRole = userAccountDAO.getAccountRoleId(targetUserId);
                    if (targetAccountRole != 1) { // not a student
                        response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=Cannot+edit+non-student");
                        return;
                    }
                }

                // supervisor/admin restrictions: can manage leader too, BUT only if target is student
                if (isSupervisor(current) || isAdmin(current)) {
                    int targetAccountRole = userAccountDAO.getAccountRoleId(targetUserId);
                    if (targetAccountRole != 1) {
                        response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=Cannot+edit+non-student");
                        return;
                    }
                }
            }

            // Execute actions
            if ("deleteTeam".equals(action)) {
                deleteTeamAndRelations(teamId);
                response.sendRedirect(request.getContextPath() + "/team");
                return;

            } else if ("kick".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                boolean ok = teamMemberDAO.kickMember(teamId, userId);
                response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=" + (ok ? "Kicked+successfully" : "Kick+failed"));
                return;

            } else if ("changeRole".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int newRoleId = Integer.parseInt(request.getParameter("roleId"));

                // optional: enforce only 4/5
                if (newRoleId != 4 && newRoleId != 5) {
                    response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=Invalid+role");
                    return;
                }

                boolean ok = teamMemberDAO.changeRole(teamId, userId, newRoleId);
                response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=" + (ok ? "Role+updated" : "Role+update+failed"));
                return;

            } else if ("updateTeam".equals(action)) {
                String teamName = request.getParameter("teamName");
                String description = request.getParameter("description");

                boolean ok = teamDAO.updateTeamInfo(teamId, teamName, description);
                response.sendRedirect("teamMember?teamId=" + teamId + "&tab=members&msg=" + (ok ? "Team+updated" : "Team+update+failed"));
                return;
            }

            // unknown action
            response.sendRedirect("teamMember?teamId=" + teamId);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private boolean isAdmin(UserAccount u) {
        return u.getRoleID() == 3;
    }

    private boolean isSupervisor(UserAccount u) {
        return u.getRoleID() == 2;
    }

    private boolean isStudentLeader(UserAccount u, int teamId) throws SQLException {
        if (u.getRoleID() != 1) return false;
        Integer myTeamRole = teamMemberDAO.getTeamRoleId(teamId, u.getUserID());
        return myTeamRole != null && myTeamRole == 5;
    }

    private boolean canManageTeam(UserAccount u, int teamId) throws SQLException {
        return isAdmin(u) || isSupervisor(u) || isStudentLeader(u, teamId);
    }

    private void deleteTeamAndRelations(int teamId) throws SQLException {
        teamProjectDAO.deleteByTeam(teamId);
        teamMemberDAO.deleteByTeam(teamId);
        teamDAO.deleteTeam(teamId);
    }
}
