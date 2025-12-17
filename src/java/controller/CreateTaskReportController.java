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
import java.util.List;

@WebServlet("/task-report/create")
public class CreateTaskReportController extends HttpServlet {

    private final TaskReportDAO taskReportDAO = new TaskReportDAO();
    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Get tasks assigned to the current user
        List<ProjectTask> userTasks = taskDAO.getAllTasksByUserId(user.getUserID());
        req.setAttribute("tasks", userTasks);
        req.setAttribute("user", user);

        req.getRequestDispatcher("/createTaskReport.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Get form parameters
            String taskIdStr = req.getParameter("taskId");
            String reportDescription = req.getParameter("reportDescription");
            String estimateWorkPercentDoneStr = req.getParameter("estimateWorkPercentDone");
            String totalHourUsedStr = req.getParameter("totalHourUsed");
            String timesheetEntryIdStr = req.getParameter("timesheetEntryId");

            // Validate required fields
            if (taskIdStr == null || taskIdStr.trim().isEmpty()) {
                req.setAttribute("error", "Please select a task.");
                doGet(req, resp);
                return;
            }

            int taskId = Integer.parseInt(taskIdStr);
            
            // Verify that the task is assigned to the user
            List<ProjectTask> userTasks = taskDAO.getAllTasksByUserId(user.getUserID());
            boolean isTaskAssigned = userTasks.stream()
                    .anyMatch(t -> t.getTaskId() == taskId);
            
            if (!isTaskAssigned) {
                req.setAttribute("error", "You are not assigned to this task.");
                doGet(req, resp);
                return;
            }

            // Create TaskReport object
            TaskReport report = new TaskReport();
            report.setUserId(user.getUserID());
            report.setTaskId(taskId);
            report.setReportDescription(reportDescription);

            // Parse estimateWorkPercentDone
            if (estimateWorkPercentDoneStr != null && !estimateWorkPercentDoneStr.trim().isEmpty()) {
                try {
                    Double percentDone = Double.parseDouble(estimateWorkPercentDoneStr);
                    report.setEstimateWorkPercentDone(percentDone);
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "Invalid percentage format. Please enter a number between 0 and 100.");
                    doGet(req, resp);
                    return;
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", e.getMessage());
                    doGet(req, resp);
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
                    doGet(req, resp);
                    return;
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", e.getMessage());
                    doGet(req, resp);
                    return;
                }
            } else {
                report.setTotalHourUsed(0.0);
            }

            // Parse timesheetEntryId (optional)
            if (timesheetEntryIdStr != null && !timesheetEntryIdStr.trim().isEmpty()) {
                try {
                    Integer timesheetEntryId = Integer.parseInt(timesheetEntryIdStr);
                    report.setTimesheetEntryId(timesheetEntryId);
                } catch (NumberFormatException e) {
                    // If invalid, just leave it as null
                    report.setTimesheetEntryId(null);
                }
            }

            // Create the report
            if (taskReportDAO.createTaskReport(report)) {
                req.setAttribute("success", "Task report created successfully!");
                // Redirect to show the created report or task list
                resp.sendRedirect(req.getContextPath() + "/task/view");
            } else {
                req.setAttribute("error", "Failed to create task report. Please try again.");
                doGet(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid input format. Please check your entries.");
            doGet(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            doGet(req, resp);
        }
    }
}

