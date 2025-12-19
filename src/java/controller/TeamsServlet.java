/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.ProjectDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import entity.Team;
import dal.TeamDAO;
import dao.UserDAO;
import entity.UserAccount;
import java.util.HashMap;
import java.util.Map;
import entity.UserAccount;

/**
 *
 * @author Admin
 */
public class TeamsServlet extends HttpServlet {

    private TeamDAO teamDAO = new TeamDAO();

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
            out.println("<title>Servlet TeamServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TeamServlet at " + request.getContextPath() + "</h1>");
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

        UserAccount current = (UserAccount) request.getSession().getAttribute("user");
        if (current == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        TeamDAO teamDAO = new TeamDAO();
        ProjectDAO projectDAO = new ProjectDAO();
        UserDAO userAccountDAO = new UserDAO();

        String q = request.getParameter("q");
        List<Team> teams;
        // Get data from DB
        try {

            boolean isSupervisorOrAdmin = (current.getRoleID() == 2 || current.getRoleID() == 3);

            if (q != null && !q.trim().isEmpty()) {
                if (isSupervisorOrAdmin) {
                    teams = teamDAO.searchByTeamOrMember(q.trim());
                } else {
                    // NEW: restricted search (only teams the student belongs to)
                    teams = teamDAO.searchByTeamOrMemberForUser(current.getUserID(), q.trim());
                }
                request.setAttribute("q", q.trim());
            } else {
                if (isSupervisorOrAdmin) {
                    teams = teamDAO.findAll();
                } else {
                    teams = teamDAO.getAllTeamByUserId(current.getUserID());
                }
            }

            Map<Integer, List<String>> projectCodesByTeam = new HashMap<>();
            Map<Integer, List<UserAccount>> membersByTeam = new HashMap<>();

            for (Team t : teams) {
                int teamId = t.getTeamID();
                projectCodesByTeam.put(teamId, projectDAO.findProjectCodesByTeam(teamId));
                membersByTeam.put(teamId, userAccountDAO.findMembersByTeam(teamId));
            }

            // Put list into request scope
            request.setAttribute("teams", teams);
            request.setAttribute("projectCodesByTeam", projectCodesByTeam);
            request.setAttribute("membersByTeam", membersByTeam);
            
            request.setAttribute("q", q);

            // Forward to JSP for display
            request.getRequestDispatcher("/team-list.jsp")
                    .forward(request, response);
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
