package controller;

import dao.ProjectDAO;
import dao.TaskDAO;
import entity.Project;
import entity.Task;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/task/create")
public class CreateTaskServlet extends HttpServlet {

    private final TaskDAO taskDAO = new TaskDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        User user = (User) session.getAttribute("user");
//
//        if (user == null || (!"2".equals(user.getRoleID()) && !"3".equals(user.getRoleID()))) {
//            resp.sendRedirect("login");  // Restrict access
//            return;
//        }

        List<Project> projects = projectDAO.getAllProjects();
//        if (projects == null) {
//            projects = new ArrayList<>();
//        }
        req.setAttribute("projects", projects);
        req.getRequestDispatcher("/createTask.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        User user = (User) session.getAttribute("user");
//
//        if (user == null || (!"2".equals(user.getRoleID()) && !"3".equals(user.getRoleID()))) {
//            resp.sendRedirect("login");
//            return;
//        }

        String taskCode = req.getParameter("taskCode");
        String taskName = req.getParameter("taskName");
        String description = req.getParameter("description");
        String projectIdStr = req.getParameter("projectId");

        Integer projectId = (projectIdStr != null && !projectIdStr.isEmpty()) ? Integer.parseInt(projectIdStr) : null;

        Task task = new Task();
        task.setTaskCode(taskCode);
        task.setTaskName(taskName);
        task.setDescription(description);
        task.setProjectId(projectId);
        task.setIsActive(true);
        task.setStatus("TO_DO");

        if (taskDAO.createTask(task)) {
            req.setAttribute("success", "Task created successfully with ID: " + task.getTaskId());
        } else {
            req.setAttribute("error", "Failed to create task. Please try again.");
        }

        doGet(req, resp);  // Reload form with message
    }
}