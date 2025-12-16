package controller;

import dal.ProjectDAO;
import dao.TaskDAO;
import entity.Project;
import entity.ProjectTask;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/task/update")
public class UpdateTaskServlet extends HttpServlet {

    private final TaskDAO taskDAO = new TaskDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String taskIdStr = req.getParameter("taskId");
        
        if (taskIdStr == null || taskIdStr.isEmpty()) {
            req.setAttribute("error", "Task ID is required.");
            req.getRequestDispatcher("/viewTask.jsp").forward(req, resp);
            return;
        }
        
        try {
            int taskId = Integer.parseInt(taskIdStr);
            ProjectTask task = taskDAO.getTaskById(taskId);
            
            if (task == null) {
                req.setAttribute("error", "Task not found.");
                req.getRequestDispatcher("/viewTask.jsp").forward(req, resp);
                return;
            }
            
            List<Project> projects = projectDAO.getAllProjects();
            req.setAttribute("task", task);
            req.setAttribute("projects", projects);
            req.getRequestDispatcher("/updateTask.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid task ID.");
            req.getRequestDispatcher("/viewTask.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String taskIdStr = req.getParameter("taskId");
        String taskName = req.getParameter("taskName");
        String description = req.getParameter("description");
        String projectIdStr = req.getParameter("projectId");
        String status = req.getParameter("status");
        String deadlineStr = req.getParameter("deadline");
        String estimateHourToDoStr = req.getParameter("estimateHourToDo");

        if (taskIdStr == null || taskIdStr.isEmpty()) {
            req.setAttribute("error", "Task ID is required.");
            doGet(req, resp);
            return;
        }

        try {
            int taskId = Integer.parseInt(taskIdStr);
            Integer projectId = (projectIdStr != null && !projectIdStr.isEmpty()) 
                    ? Integer.parseInt(projectIdStr) : null;

            // ProjectID is NOT NULL in database
            if (projectId == null) {
                req.setAttribute("error", "Project is required.");
                doGet(req, resp);
                return;
            }

            ProjectTask task = new ProjectTask();
            task.setTaskId(taskId);
            task.setTaskName(taskName);
            task.setDescription(description);
            task.setProjectId(projectId);
            task.setStatus(status != null ? status : ProjectTask.STATUS_TODO);
            
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

            if (taskDAO.updateTask(task)) {
                req.setAttribute("success", "Task updated successfully.");
                resp.sendRedirect("view");
            } else {
                req.setAttribute("error", "Failed to update task. Please try again.");
                doGet(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid task ID or project ID.");
            doGet(req, resp);
        }
    }
}

