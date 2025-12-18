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

@WebServlet("/student/tasks")
public class StudentTaskListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = session != null ? (UserAccount) session.getAttribute("user") : null;

        if (user == null || user.getRoleID() != 1) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        TaskDAO taskDAO = new TaskDAO();
        List<ProjectTask> tasks = taskDAO.getAllTasksByUserId(user.getUserID());
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        req.setAttribute("tasks", tasks);
        req.setAttribute("student", user);
        req.getRequestDispatcher("/nghiapages/my_all_task.jsp").forward(req, resp);
    }
}

