package controller;

import dal.ProjectDAO;
import dal.TeamDAO;
import dal.TimesheetEntryDAO;
import dal.UserAccountDAO;
import entity.Project;
import entity.Team;
import entity.TimesheetEntry;
import entity.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/supervisor/dashboard")
public class SupervisorDashboardController extends HttpServlet {

    private final TimesheetEntryDAO timesheetDAO = new TimesheetEntryDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final TeamDAO teamDAO = new TeamDAO();
    private final UserAccountDAO userDAO = new UserAccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        UserAccount supervisor = (UserAccount) session.getAttribute("user");
        
        if (supervisor == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

//        try {
//            // Get pending timesheet entries
//            List<TimesheetEntry> pendingEntries = timesheetDAO.getPendingTimesheetEntries();
//            
//            // Get all active projects
//            List<Project> projects = projectDAO.findAll();
//            long activeProjectsCount = projects.stream()
//                    .filter(p -> p.isIsActive() && (p.getStatus() == null || 
//                            p.getStatus().equals("OPEN") || p.getStatus().equals("IN_PROGRESS")))
//                    .count();
//            
//            // Get all teams
//            List<Team> teams = teamDAO.findAll();
//            
//            // Get all students - using RoleID = 1 (assuming STUDENT role has ID 1)
//            // For now, get all active users and filter by role
//            List<UserAccount> allUsers = userDAO.getAllUsersForTeam();
//            List<UserAccount> students = new ArrayList<>();
//            for (UserAccount u : allUsers) {
////                if (u.getRoleID() != null && u.getRoleID() == 1) { // Assuming roleId 1 is STUDENT
//                if (u.getRoleID() == 1) { // Assuming roleId 1 is STUDENT
//                    students.add(u);
//                }
//            }
//            
//            // Calculate statistics
//            int pendingCount = pendingEntries.size();
//            int totalProjects = (int) activeProjectsCount;
//            int totalTeams = teams.size();
//            int totalStudents = students.size();
//            
//            // Get recent pending entries (limit to 10)
//            List<TimesheetEntry> recentPending = pendingEntries.size() > 10 
//                    ? pendingEntries.subList(0, 10) 
//                    : pendingEntries;
//            
//            // Get recent projects (limit to 5)
//            List<Project> recentProjects = projects.size() > 5 
//                    ? projects.subList(0, 5) 
//                    : projects;
//
//            // Set attributes for JSP
//            req.setAttribute("pendingCount", pendingCount);
//            req.setAttribute("totalProjects", totalProjects);
//            req.setAttribute("totalTeams", totalTeams);
//            req.setAttribute("totalStudents", totalStudents);
//            req.setAttribute("pendingEntries", recentPending);
//            req.setAttribute("recentProjects", recentProjects);
//            req.setAttribute("supervisor", supervisor);
//
//            req.getRequestDispatcher("/supervisor/dashboard.jsp").forward(req, resp);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            req.setAttribute("error", "Error loading dashboard: " + e.getMessage());
//            req.getRequestDispatcher("/supervisor/dashboard.jsp").forward(req, resp);
//        }
    }
}

