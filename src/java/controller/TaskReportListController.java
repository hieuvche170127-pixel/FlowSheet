package controller;

import dal.TaskReportDAO;
import dao.TaskDAO;
import entity.ProjectTask;
import entity.TaskReport;
import entity.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/task-report/list")
public class TaskReportListController extends HttpServlet {

    private final TaskReportDAO taskReportDAO = new TaskReportDAO();
    private final TaskDAO taskDAO = new TaskDAO();

    private void loadListData(HttpServletRequest req, UserAccount user) {
        List<TaskReport> reports = taskReportDAO.getTaskReportsByUserId(user.getUserID());
        List<ProjectTask> tasks = taskDAO.getAllTasksByUserId(user.getUserID());

        Map<Integer, ProjectTask> taskMap = new HashMap<>();
        if (tasks != null) {
            for (ProjectTask t : tasks) {
                taskMap.put(t.getTaskId(), t);
            }
        }

        req.setAttribute("reports", reports);
        req.setAttribute("taskMap", taskMap);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = session != null ? (UserAccount) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Support flash messages passed via redirect parameters
        String successParam = req.getParameter("success");
        String errorParam = req.getParameter("error");
        if (successParam != null && !successParam.isEmpty()) {
            req.setAttribute("success", successParam);
        }
        if (errorParam != null && !errorParam.isEmpty()) {
            req.setAttribute("error", errorParam);
        }

        loadListData(req, user);
        req.getRequestDispatcher("/viewTaskReports.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = session != null ? (UserAccount) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            String reportIdStr = req.getParameter("reportId");
            try {
                int reportId = Integer.parseInt(reportIdStr);
                boolean deleted = taskReportDAO.deleteTaskReport(reportId, user.getUserID());
                if (deleted) {
                    req.setAttribute("success", "Task report deleted successfully.");
                } else {
                    req.setAttribute("error", "Unable to delete the selected task report.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Invalid report ID.");
            }
        }

        loadListData(req, user);
        req.getRequestDispatcher("/viewTaskReports.jsp").forward(req, resp);
    }
}




