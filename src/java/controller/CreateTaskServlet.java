package controller;

import dal.ProjectDAO;
import dao.TaskDAO;
import entity.Project;
import entity.ProjectTask;
import entity.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/task/create")
public class CreateTaskServlet extends HttpServlet {

    private final TaskDAO taskDAO = new TaskDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Allow access if user is not a student, OR if user is a student but is a project leader
        if (user.getRoleID() == 1) {
            // Student - check if they are a project leader
            String projectIdParam = req.getParameter("projectId");
            if (projectIdParam != null && !projectIdParam.trim().isEmpty()) {
                try {
                    int projectId = Integer.parseInt(projectIdParam);
                    if (!projectDAO.isProjectLeader(projectId, user.getUserID())) {
                        // Student is not a project leader - deny access
                        resp.sendRedirect("view");
                        return;
                    }
                    // Student is a project leader - allow access
                } catch (NumberFormatException e) {
                    // Invalid projectId - deny access
                    resp.sendRedirect("view");
                    return;
                }
            } else {
                // No projectId provided - deny access for students
                resp.sendRedirect("view");
                return;
            }
        }

        List<Project> projects = projectDAO.getAllProjects();

        // Get projectId from URL parameter if exists
        String projectIdParam = req.getParameter("projectId");
        if (projectIdParam != null && !projectIdParam.trim().isEmpty()) {
            try {
                Integer projectId = Integer.parseInt(projectIdParam);
                req.setAttribute("selectedProjectId", projectId);
            } catch (NumberFormatException e) {
                // Invalid projectId, ignore
            }
        }

        req.setAttribute("projects", projects);
        req.getRequestDispatcher("/createTask.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Allow access if user is not a student, OR if user is a student but is a project leader
        if (user.getRoleID() == 1) {
            // Student - check if they are a project leader
            String projectIdStr = req.getParameter("projectId");
            if (projectIdStr != null && !projectIdStr.trim().isEmpty()) {
                try {
                    int projectId = Integer.parseInt(projectIdStr);
                    if (!projectDAO.isProjectLeader(projectId, user.getUserID())) {
                        // Student is not a project leader - deny access
                        resp.sendRedirect("view");
                        return;
                    }
                    // Student is a project leader - allow access
                } catch (NumberFormatException e) {
                    // Invalid projectId - deny access
                    resp.sendRedirect("view");
                    return;
                }
            } else {
                // No projectId provided - deny access for students
                resp.sendRedirect("view");
                return;
            }
        }

        String taskName = req.getParameter("taskName");
        String description = req.getParameter("description");
        String projectIdStr = req.getParameter("projectId");
        String deadlineStr = req.getParameter("deadline");
        String estimateHourToDoStr = req.getParameter("estimateHourToDo");

        // Validate projectId is required
        if (projectIdStr == null || projectIdStr.trim().isEmpty()) {
            req.setAttribute("error", "Project is required. Every task must belong to a project.");
            doGet(req, resp);
            return;
        }

        Integer projectId;
        try {
            projectId = Integer.parseInt(projectIdStr);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid project ID.");
            doGet(req, resp);
            return;
        }

        ProjectTask task = new ProjectTask();
        task.setTaskName(taskName);
        task.setDescription(description);
        task.setProjectId(projectId);
        task.setStatus(ProjectTask.STATUS_TODO);
        
        // Parse deadline
        if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                java.util.Date parsedDate = dateFormat.parse(deadlineStr);
                
                // Validate deadline is not in the past
                java.util.Date now = new java.util.Date();
                if (parsedDate.before(now)) {
                    req.setAttribute("error", "Deadline cannot be in the past.");
                    doGet(req, resp);
                    return;
                }
                
                task.setDeadline(new Timestamp(parsedDate.getTime()));
            } catch (ParseException e) {
                // Try alternative format
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(deadlineStr);
                    
                    // Validate deadline is not in the past
                    java.util.Date now = new java.util.Date();
                    if (parsedDate.before(now)) {
                        req.setAttribute("error", "Deadline cannot be in the past.");
                        doGet(req, resp);
                        return;
                    }
                    
                    task.setDeadline(new Timestamp(parsedDate.getTime()));
                } catch (ParseException ex) {
                    req.setAttribute("error", "Invalid deadline format.");
                    doGet(req, resp);
                    return;
                }
            }
        }
        
        // Parse estimateHourToDo
        if (estimateHourToDoStr != null && !estimateHourToDoStr.trim().isEmpty()) {
            try {
                Double estimateHours = Double.parseDouble(estimateHourToDoStr);
                task.setEstimateHourToDo(estimateHours);
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Invalid estimate hours format.");
                doGet(req, resp);
                return;
            }
        }

        if (taskDAO.createTask(task)) {
            // Check if we came from a project details page - if so, redirect back there
            String refererProjectId = req.getParameter("projectId");
            if (refererProjectId != null && !refererProjectId.trim().isEmpty()) {
                // Redirect back to project details page
                resp.sendRedirect(req.getContextPath() + "/project/details?id=" + refererProjectId);
                return;
            }
            // Otherwise redirect to task view page
            resp.sendRedirect(req.getContextPath() + "/task/view");
        } else {
            req.setAttribute("error", "Failed to create task. Please try again.");
            doGet(req, resp);  // Reload form with error message
        }
    }
}