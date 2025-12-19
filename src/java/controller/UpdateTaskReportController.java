package controller;

import dal.TaskReportDAO;
import dal.TimesheetEntryDAO;
import dao.TaskDAO;
import entity.ProjectTask;
import entity.TaskReport;
import entity.TimesheetEntry;
import entity.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/task-report/update")
public class UpdateTaskReportController extends HttpServlet {

    private final TaskReportDAO taskReportDAO = new TaskReportDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final TimesheetEntryDAO timesheetEntryDAO = new TimesheetEntryDAO();

    private void loadFormData(HttpServletRequest req, UserAccount user) {
        List<ProjectTask> userTasks = taskDAO.getAllTasksByUserId(user.getUserID());
        //List<TimesheetEntry> entries = timesheetEntryDAO.getEntriesByUserId(user.getUserID());
        req.setAttribute("tasks", userTasks);
        //req.setAttribute("timesheetEntries", entries);
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

        String reportIdStr = req.getParameter("reportId");
        if (reportIdStr == null || reportIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/task-report/list?error=" +
                    URLEncoder.encode("Report ID is required.", StandardCharsets.UTF_8));
            return;
        }

        try {
            int reportId = Integer.parseInt(reportIdStr);
            TaskReport report = taskReportDAO.getTaskReportByIdAndUser(reportId, user.getUserID());
            if (report == null) {
                resp.sendRedirect(req.getContextPath() + "/task-report/list?error=" +
                        URLEncoder.encode("Task report not found.", StandardCharsets.UTF_8));
                return;
            }

            loadFormData(req, user);
            req.setAttribute("report", report);
            req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/task-report/list?error=" +
                    URLEncoder.encode("Invalid report ID.", StandardCharsets.UTF_8));
        }
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

        String reportIdStr = req.getParameter("reportId");
        String taskIdStr = req.getParameter("taskId");
        String reportDescription = req.getParameter("reportDescription");
        String estimateWorkPercentDoneStr = req.getParameter("estimateWorkPercentDone");
        String totalHourUsedStr = req.getParameter("totalHourUsed");
        String timesheetEntryIdStr = req.getParameter("timesheetEntryId");

        if (reportIdStr == null || reportIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/task-report/list?error=" +
                    URLEncoder.encode("Report ID is required.", StandardCharsets.UTF_8));
            return;
        }

        try {
            int reportId = Integer.parseInt(reportIdStr);
            TaskReport report = taskReportDAO.getTaskReportByIdAndUser(reportId, user.getUserID());
            if (report == null) {
                resp.sendRedirect(req.getContextPath() + "/task-report/list?error=" +
                        URLEncoder.encode("Task report not found.", StandardCharsets.UTF_8));
                return;
            }

            if (taskIdStr == null || taskIdStr.trim().isEmpty()) {
                req.setAttribute("error", "Please select a task.");
                loadFormData(req, user);
                req.setAttribute("report", report);
                req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
                return;
            }

            int taskId = Integer.parseInt(taskIdStr);
            List<ProjectTask> userTasks = taskDAO.getAllTasksByUserId(user.getUserID());
            boolean isTaskAssigned = userTasks != null && userTasks.stream()
                    .anyMatch(t -> t.getTaskId() == taskId);

            if (!isTaskAssigned) {
                req.setAttribute("error", "You are not assigned to this task.");
                loadFormData(req, user);
                req.setAttribute("report", report);
                req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
                return;
            }

            report.setTaskId(taskId);
            report.setReportDescription(reportDescription);

            // Parse estimateWorkPercentDone
            if (estimateWorkPercentDoneStr != null && !estimateWorkPercentDoneStr.trim().isEmpty()) {
                try {
                    Double percentDone = Double.parseDouble(estimateWorkPercentDoneStr);
                    report.setEstimateWorkPercentDone(percentDone);
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "Invalid percentage format. Please enter a number between 0 and 100.");
                    loadFormData(req, user);
                    req.setAttribute("report", report);
                    req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
                    return;
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", e.getMessage());
                    loadFormData(req, user);
                    req.setAttribute("report", report);
                    req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
                    return;
                }
            } else {
                report.setEstimateWorkPercentDone(0.0);
            }

            // Parse totalHourUsed
            if (totalHourUsedStr != null && !totalHourUsedStr.trim().isEmpty()) {
                try {
                    Double totalHours = Double.parseDouble(totalHourUsedStr);
                    report.setTotalHourUsed(totalHours);
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "Invalid hours format. Please enter a valid number.");
                    loadFormData(req, user);
                    req.setAttribute("report", report);
                    req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
                    return;
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", e.getMessage());
                    loadFormData(req, user);
                    req.setAttribute("report", report);
                    req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
                    return;
                }
            } else {
                report.setTotalHourUsed(0.0);
            }

            // Parse timesheetEntryId (optional)
            if (timesheetEntryIdStr != null && !timesheetEntryIdStr.trim().isEmpty()) {
                try {
                    Integer timesheetEntryId = Integer.parseInt(timesheetEntryIdStr);
//                    TimesheetEntry entry = timesheetEntryDAO.findByIdAndUser(timesheetEntryId, user.getUserID());
//                    if (entry == null) {
//                        req.setAttribute("error", "Selected timesheet entry is not valid for your account.");
//                        loadFormData(req, user);
//                        req.setAttribute("report", report);
//                        req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
//                        return;
//                    }
//                    report.setTimesheetEntryId(entry.getEntryId());
                } catch (NumberFormatException e) {
                    // If invalid, just leave it as null
                    report.setTimesheetEntryId(null);
                }
            } else {
                report.setTimesheetEntryId(null);
            }

            if (taskReportDAO.updateTaskReport(report)) {
                resp.sendRedirect(req.getContextPath() + "/task-report/list?success=" +
                        URLEncoder.encode("Task report updated successfully.", StandardCharsets.UTF_8));
            } else {
                req.setAttribute("error", "Failed to update task report. Please try again.");
                loadFormData(req, user);
                req.setAttribute("report", report);
                req.getRequestDispatcher("/updateTaskReport.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/task-report/list?error=" +
                    URLEncoder.encode("Invalid input format.", StandardCharsets.UTF_8));
        }
    }
}




