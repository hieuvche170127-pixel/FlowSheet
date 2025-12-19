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

        if (user == null || user.getRoleID()==1) {
            resp.sendRedirect("view");  // Restrict access
            return;
        }

        List<Project> projects = projectDAO.getAllProjects();

        req.setAttribute("projects", projects);
        req.getRequestDispatcher("/createTask.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null || user.getRoleID()==1) {
            resp.sendRedirect("view");
            return;
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
                task.setDeadline(new Timestamp(parsedDate.getTime()));
            } catch (ParseException e) {
                // Try alternative format
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(deadlineStr);
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
            req.setAttribute("success", "Task created successfully with ID: " + task.getTaskId());
        } else {
            req.setAttribute("error", "Failed to create task. Please try again.");
        }
        req.getRequestDispatcher("/task/view").forward(req, resp);
//        doGet(req, resp);  // Reload form with message
    }
}