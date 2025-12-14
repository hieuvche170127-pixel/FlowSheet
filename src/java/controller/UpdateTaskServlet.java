package controller;

import dao.ProjectDAO;
import dao.TaskDAO;
import entity.Project;
import entity.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            Task task = taskDAO.getTaskById(taskId);
            
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
        String taskCode = req.getParameter("taskCode");
        String taskName = req.getParameter("taskName");
        String description = req.getParameter("description");
        String projectIdStr = req.getParameter("projectId");
        String status = req.getParameter("status");
        String isActiveStr = req.getParameter("isActive");

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

            boolean isActive = isActiveStr != null && "true".equals(isActiveStr);

            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskCode(taskCode);
            task.setTaskName(taskName);
            task.setDescription(description);
            task.setProjectId(projectId);
            task.setStatus(status != null ? status : "TO_DO");
            task.setIsActive(isActive);

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

