package controller;

import dao.TaskDAO;
import dal.ProjectDAO;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/task/view")
public class ViewTaskServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null || user.getRoleID() == 1) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        TaskDAO taskDAO = new TaskDAO();
        ProjectDAO projectDAO = new ProjectDAO();
        
        // Get filter parameters
        String searchFilter = req.getParameter("search");
        String projectIdParam = req.getParameter("projectId");

        // Project filter: null = all, -1 = lab/unassigned, other = project id
        Integer projectIdFilter = null;
        boolean labSelected = false;
        if (projectIdParam != null && !projectIdParam.isEmpty()) {
            if ("lab".equalsIgnoreCase(projectIdParam)) {
                projectIdFilter = -1;
                labSelected = true;
            } else {
                try {
                    projectIdFilter = Integer.parseInt(projectIdParam);
                } catch (NumberFormatException ignored) {
                    // invalid projectId ignored; defaults to all
                }
            }
        }

        // Normalize search filter
        if (searchFilter != null && searchFilter.trim().isEmpty()) {
            searchFilter = null;
        } else if (searchFilter != null) {
            searchFilter = searchFilter.trim();
        }
        
        // Get filtered tasks
        List<ProjectTask> tasks = taskDAO.getTasksWithFilter(projectIdFilter, searchFilter);
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        
        // Set attributes for display
        req.setAttribute("tasks", tasks);
        req.setAttribute("searchFilter", searchFilter != null ? searchFilter : "");
        req.setAttribute("projectIdFilter", projectIdFilter);
        req.setAttribute("labSelected", labSelected);
        List<Project> projects = projectDAO.getAllProjectsForTeam();
        req.setAttribute("projects", projects);
        
        req.getRequestDispatcher("/viewTask.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null || user.getRoleID() == 1) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        TaskDAO taskDAO = new TaskDAO();
        String action = req.getParameter("action");
        
        if ("delete".equals(action)) {
            try {
                String taskIdStr = req.getParameter("taskId");
                if (taskIdStr == null || taskIdStr.isEmpty()) {
                    req.setAttribute("error", "Task ID is required.");
                } else {
                    int taskId = Integer.parseInt(taskIdStr);
                    if (taskDAO.deleteTask(taskId)) {
                        req.setAttribute("success", "Task deleted successfully.");
                    } else {
                        req.setAttribute("error", "Failed to delete task. The task may not exist or has already been deleted.");
                    }
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Invalid task ID.");
            }
        }

        // Reload the task list with message
        doGet(req, resp);
    }
}
