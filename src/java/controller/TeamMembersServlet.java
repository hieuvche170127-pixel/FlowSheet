/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ProjectDAO;
import dao.RoleDAO;
import dao.TeamDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import entity.TeamMember;
import dao.TeamMemberDAO;
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
            response.sendRedirect("team");
            return;
        }

        int teamId = Integer.parseInt(teamIdParam);
        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "members";
        }

        if (teamIdParam == null) {
            // no teamId → you can redirect back or show error; for now go back
            response.sendRedirect("team");
            return;
        }

        try {
            Team team = teamDAO.findById(teamId);
            List<User> members = userAccountDAO.findMembersByTeam(teamId);       // students + supervisors
            List<Project> projects = projectDAO.findProjectsByTeam(teamId);

            request.setAttribute("team", team);
            request.setAttribute("userAccounts", members);
            request.setAttribute("projects", projects);
            request.setAttribute("activeTab", tab);

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
