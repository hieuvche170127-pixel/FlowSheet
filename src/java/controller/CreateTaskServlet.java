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

        if (user == null || user.getRoleId()==1 && user.getRoleId()==3) {
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

        if (user == null || user.getRoleId()==1 && user.getRoleId()==3) {
            resp.sendRedirect("view");
            return;
        }

        String taskCode = req.getParameter("taskCode");
        String taskName = req.getParameter("taskName");
        String description = req.getParameter("description");
        String projectIdStr = req.getParameter("projectId");

        Integer projectId = (projectIdStr != null && !projectIdStr.isEmpty()) ? Integer.parseInt(projectIdStr) : null;

        ProjectTask task = new ProjectTask();
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
        req.getRequestDispatcher("/task/view").forward(req, resp);
//        doGet(req, resp);  // Reload form with message
    }
}