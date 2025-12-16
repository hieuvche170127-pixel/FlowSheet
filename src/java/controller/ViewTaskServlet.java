package controller;

import dao.TaskDAO;
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
//        HttpSession session = req.getSession(false);
//        User user = (User) session.getAttribute("user");

//        if (user == null || (!"2".equals(user.getRoleID()) && !"3".equals(user.getRoleID()))) {
//            resp.sendRedirect("login");
//            return;
//        }
        TaskDAO taskDAO = new TaskDAO();
        
        // Get filter parameters
        String taskNameFilter = req.getParameter("taskName");
        String projectNameFilter = req.getParameter("projectName");
        
        // Combine filters into a single search string (method searches in TaskName and ProjectName)
        String searchFilter = null;
        if (taskNameFilter != null && !taskNameFilter.trim().isEmpty()) {
            searchFilter = taskNameFilter.trim();
        } else if (projectNameFilter != null && !projectNameFilter.trim().isEmpty()) {
            searchFilter = projectNameFilter.trim();
        }
        
        // Get filtered tasks
        List<ProjectTask> tasks = taskDAO.getTasksWithFilter(searchFilter);
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        
        // Set attributes for display
        req.setAttribute("tasks", tasks);
        req.setAttribute("taskNameFilter", taskNameFilter != null ? taskNameFilter : "");
        req.setAttribute("projectNameFilter", projectNameFilter != null ? projectNameFilter : "");
        
        req.getRequestDispatcher("/viewTask.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        User user = (User) session.getAttribute("user");

//        if (user == null || (!"2".equals(user.getRoleID()) && !"3".equals(user.getRoleID()))) {
//            resp.sendRedirect("login");
//            return;
//        }

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
