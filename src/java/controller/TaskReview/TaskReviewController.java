/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.TaskReview;

import dal.TaskReportDAO;
import dal.TaskReviewDAO;
import entity.TaskReview;
import entity.UserAccount;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import entity.ProjectTask;
import entity.TaskReport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Admin
 */
public class TaskReviewController extends HttpServlet {

    private final TaskReviewDAO dao = new TaskReviewDAO();
    private final TaskReportDAO reportDAO = new TaskReportDAO();

    private UserAccount requireLogin(HttpServletRequest request, HttpServletResponse respond) throws IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = session != null ? (UserAccount) session.getAttribute("user") : null;
        if (user == null) {
            respond.sendRedirect(request.getContextPath() + "/login.jsp");
            return null;
        }
        return user;
    }

    private int parseIntOrMinus1(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    private void loadList(HttpServletRequest request, int taskId) {
        List<TaskReview> reviews = dao.listByTaskId(taskId);
        Set<Integer> reviewerIds = new HashSet<>();
        for (TaskReview r : reviews) {
            reviewerIds.add(r.getReviewedBy());
        }

        Map<Integer, String> reviewerNameMap = dao.getUserFullNamesByIds(reviewerIds);
        request.setAttribute("reviewerNameMap", reviewerNameMap);
        request.setAttribute("reviews", reviews);
        request.setAttribute("taskId", taskId);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TaskReviewController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TaskReviewController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = requireLogin(request, response);
        if (user == null) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "list";
        }

        // flash messages
        String successParam = request.getParameter("success");
        String errorParam = request.getParameter("error");
        if (successParam != null && !successParam.isEmpty()) {
            request.setAttribute("success", successParam);
        }
        if (errorParam != null && !errorParam.isEmpty()) {
            request.setAttribute("error", errorParam);
        }

        switch (action) {
            case "create" -> {
                boolean isSupervisor = (user.getRoleID() == 2);
                if (!isSupervisor) {
                    response.sendRedirect(request.getContextPath() + "/task-review?action=list&error=Access denied");
                    return;
                }

                int taskId = parseIntOrMinus1(request.getParameter("taskId"));
                request.setAttribute("taskId", (taskId > 0) ? taskId : 0);

                // dropdown task list
                loadAllTasks(request);

                // when supervisor selects a task -> show all reports for that task
                if (taskId > 0) {
                    List<TaskReport> reports = reportDAO.getTaskReportsByTaskId(taskId);
                    request.setAttribute("reports", reports);

                    // map userId -> fullName for showing student name in table
                    Set<Integer> userIds = new HashSet<>();
                    for (TaskReport r : reports) {
                        userIds.add(r.getUserId());
                    }

                    Map<Integer, String> reportUserNameMap = dao.getUserFullNamesByIds(userIds);
                    request.setAttribute("reportUserNameMap", reportUserNameMap);
                }

                request.setAttribute("mode", "create");
                request.setAttribute("formAction", request.getContextPath() + "/task-review?action=create");
                request.getRequestDispatcher("/taskReview/taskReview-form.jsp").forward(request, response);
            }

            case "edit" -> {
                boolean isSupervisor = (user.getRoleID() == 2);
                if (!isSupervisor) {
                    response.sendRedirect(request.getContextPath() + "/task-review?action=list&error=Access denied");
                    return;
                }

                int reviewId = parseIntOrMinus1(request.getParameter("reviewId"));
                if (reviewId <= 0) {
                    /* handle error */ }

                TaskReview review = dao.getByIdAndReviewer(reviewId, user.getUserID());
                if (review == null) {
                    /* handle not found/unauthorized */ }

                request.setAttribute("review", review);

                loadAllTasks(request); // or loadReviewedTasks(request, user.getUserID());

                request.setAttribute("mode", "update");
                request.setAttribute("formAction", request.getContextPath() + "/task-review?action=edit");
                request.getRequestDispatcher("/taskReview/taskReview-form.jsp").forward(request, response);
            }

            default -> {
                int taskId = parseIntOrMinus1(request.getParameter("taskId"));
                String q = request.getParameter("q");
                Integer filterTaskId = (taskId > 0) ? taskId : null;

                //int reviewerId = user.getUserID();
                boolean isSupervisor = (user.getRoleID() == 2);
                boolean canManage = isSupervisor; // for now: students view-only

                request.setAttribute("canManage", canManage);

                if (isSupervisor) {
                    // dropdown: only tasks this supervisor has reviews for
                    loadReviewedTasks(request, user.getUserID());

                    // IMPORTANT: only show reviews by this supervisor or student
                    List<TaskReview> reviews = dao.listReviews(filterTaskId, q, user.getUserID());
                    Set<Integer> reviewerIds = new HashSet<>();
                    for (TaskReview r : reviews) {
                        reviewerIds.add(r.getReviewedBy());
                    }

                    Map<Integer, String> reviewerNameMap = dao.getUserFullNamesByIds(reviewerIds);
                    request.setAttribute("reviewerNameMap", reviewerNameMap);
                    request.setAttribute("reviews", reviews);
                } else {
                    // students: only see reviews for tasks they have in TimesheetEntry
                    loadTasksForStudent(request, user.getUserID());

                    List<TaskReview> reviews = dao.listReviewsForStudent(filterTaskId, q, user.getUserID());
                    Set<Integer> reviewerIds = new HashSet<>();
                    for (TaskReview r : reviews) {
                        reviewerIds.add(r.getReviewedBy());
                    }

                    Map<Integer, String> reviewerNameMap = dao.getUserFullNamesByIds(reviewerIds);
                    request.setAttribute("reviewerNameMap", reviewerNameMap);
                    request.setAttribute("reviews", reviews);
                }

                request.setAttribute("taskId", (taskId > 0) ? taskId : 0);
                request.getRequestDispatcher("/taskReview/taskReview-list.jsp").forward(request, response);
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = requireLogin(request, response);
        if (user == null) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "list";
        }

        int taskId = parseIntOrMinus1(request.getParameter("taskId"));

        try {
            switch (action) {
                case "create" -> {
                    boolean isSupervisor = (user.getRoleID() == 2);
                    if (!isSupervisor) {
                        response.sendRedirect(request.getContextPath() + "/task-review?action=list&error=Access denied");
                        return;
                    }

                    int reportId = parseIntOrMinus1(request.getParameter("reportId"));

                    if (taskId <= 0) {
                        throw new IllegalArgumentException("Please select a task.");
                    }
                    if (reportId <= 0) {
                        throw new IllegalArgumentException("Please select a student report to review.");
                    }

                    // You said you already added this method
                    TaskReport selected = reportDAO.getTaskReportById(reportId);
                    if (selected == null || selected.getTaskId() != taskId) {
                        throw new IllegalArgumentException("Invalid report selection.");
                    }

                    String percentStr = request.getParameter("estimateWorkPercentDone");
                    String comment = request.getParameter("reviewComment");

                    Double percent;
                    if (percentStr != null && !percentStr.isBlank()) {
                        percent = Double.parseDouble(percentStr);
                        if (percent < 0 || percent > 100) {
                            throw new IllegalArgumentException("Percent must be 0..100.");
                        }
                    } else {
                        // default from selected report
                        percent = selected.getEstimateWorkPercentDone();
                    }

                    TaskReview r = new TaskReview();
                    r.setTaskId(taskId);
                    r.setReviewedBy(user.getUserID());
                    r.setEstimateWorkPercentDone(percent);
                    r.setReviewComment(comment);

                    boolean ok = dao.create(r);
                    if (ok) {
                        response.sendRedirect(request.getContextPath() + "/task-review?action=list&success=Review created.");
                        return;
                    } else {
                        throw new IllegalArgumentException("Create review failed.");
                    }
                }

                case "edit" -> {
                    boolean isSupervisor = (user.getRoleID() == 2);
                    if (!isSupervisor) {
                        response.sendRedirect(request.getContextPath() + "/task-review?action=list&error=Access denied");
                        return;
                    }

                    int reviewId = parseIntOrMinus1(request.getParameter("reviewId"));
                    int taskIdParam = parseIntOrMinus1(request.getParameter("taskId")); // optional

                    if (reviewId <= 0) {
                        throw new IllegalArgumentException("Invalid reviewId.");
                    }

                    // Load existing to (1) verify ownership and (2) fallback taskId if missing
                    TaskReview existing = dao.getByIdAndReviewer(reviewId, user.getUserID());
                    if (existing == null) {
                        throw new IllegalArgumentException("Review not found or not owner.");
                    }

                    int taskIdToUse = (taskIdParam > 0) ? taskIdParam : existing.getTaskId();

                    String percentStr = request.getParameter("estimateWorkPercentDone");
                    String comment = request.getParameter("reviewComment");

                    Double percent = null;
                    if (percentStr != null && !percentStr.isBlank()) {
                        percent = Double.parseDouble(percentStr);
                        if (percent < 0 || percent > 100) {
                            throw new IllegalArgumentException("Percent must be 0..100.");
                        }
                    }

                    TaskReview r = new TaskReview();
                    r.setReviewId(reviewId);
                    r.setReviewedBy(user.getUserID()); // ownership enforced in SQL
                    r.setEstimateWorkPercentDone(percent);
                    r.setReviewComment(comment);

                    boolean ok = dao.update(r);
                    if (ok) {
                        response.sendRedirect(request.getContextPath() + "/task-review?action=list&success=Review updated.");
                        return;
                    } else {
                        request.setAttribute("error", "Update failed (not owner or not found).");
                        request.setAttribute("review", existing);
                        request.setAttribute("taskId", taskIdToUse);

                        request.setAttribute("mode", "update");
                        request.setAttribute("formAction", request.getContextPath() + "/task-review?action=edit");

                        request.getRequestDispatcher("/taskReview/taskReview-form.jsp").forward(request, response);
                        return;
                    }
                }

                case "delete" -> {
                    boolean isSupervisor = (user.getRoleID() == 2);
                    if (!isSupervisor) {
                        response.sendRedirect(request.getContextPath() + "/task-review?action=list&error=Access denied");
                        return;
                    }

                    int reviewId = parseIntOrMinus1(request.getParameter("reviewId"));
                    if (reviewId <= 0) {
                        throw new IllegalArgumentException("Invalid reviewId.");
                    }

                    boolean ok = dao.delete(reviewId, user.getUserID());
                    String msg = ok ? "Review deleted." : "Delete failed (not owner or not found).";

                    // redirect back to list; keep task filter only if provided (>0)
                    String redirect = request.getContextPath() + "/task-review?action=list";
//                    if (taskId > 0) {
//                        redirect += "&taskId=" + taskId;
//                    }
                    redirect += (ok ? "&success=" : "&error=") + msg;

                    response.sendRedirect(redirect);
                    return;
                }
                default -> {
                    // fallback: treat as list refresh
                    response.sendRedirect(request.getContextPath() + "/task-review?action=list&taskId=" + taskId);
                    return;
                }
            }
        } catch (Exception ex) {
            request.setAttribute("error", "Invalid input: " + ex.getMessage());

            // for create/edit pages we must reload the correct dropdown
            String action2 = request.getParameter("action");
            if ("create".equals(action2)) {
                loadAllTasks(request);

                int taskId2 = parseIntOrMinus1(request.getParameter("taskId"));
                request.setAttribute("taskId", (taskId2 > 0) ? taskId2 : 0);

                // also reload reports list if a task was selected
                if (taskId2 > 0) {
                    List<TaskReport> reports = reportDAO.getTaskReportsByTaskId(taskId2);
                    request.setAttribute("reports", reports);

                    Set<Integer> userIds = new HashSet<>();
                    for (TaskReport tr : reports) {
                        userIds.add(tr.getUserId());
                    }

                    Map<Integer, String> reportUserNameMap = dao.getUserFullNamesByIds(userIds);
                    request.setAttribute("reportUserNameMap", reportUserNameMap);
                }

                request.setAttribute("mode", "create");
                request.setAttribute("formAction", request.getContextPath() + "/task-review?action=create");
                request.getRequestDispatcher("/taskReview/taskReview-form.jsp").forward(request, response);
                return;
            }
            //String action2 = request.getParameter("action");
            if ("create".equals(action2)) {
                request.setAttribute("mode", "create");
                request.setAttribute("formAction", request.getContextPath() + "/task-review?action=create");
                request.setAttribute("taskId", taskId);
                request.getRequestDispatcher("/taskReview/taskReview-form.jsp").forward(request, response);
                return;
            }

            if ("edit".equals(action2)) {
                int reviewId = parseIntOrMinus1(request.getParameter("reviewId"));
                TaskReview existing = dao.getByIdAndReviewer(reviewId, user.getUserID());
                request.setAttribute("review", existing);

                int taskIdToUse = (existing != null) ? existing.getTaskId() : taskId; // fallback
                request.setAttribute("taskId", taskIdToUse);

                request.setAttribute("mode", "update");
                request.setAttribute("formAction", request.getContextPath() + "/task-review?action=edit");
                request.getRequestDispatcher("/taskReview/taskReview-form.jsp").forward(request, response);
                return;
            }

            request.getRequestDispatcher("/taskReview/taskReview-list.jsp").forward(request, response);
        }
    }

    private void loadTasks(HttpServletRequest request, int reviewerId) {
        List<ProjectTask> tasks = dao.listReviewedTasksBySupervisor(reviewerId);
        request.setAttribute("tasks", tasks);

        Map<Integer, ProjectTask> taskMap = new HashMap<>();
        for (ProjectTask t : tasks) {
            taskMap.put(t.getTaskId(), t);
        }
        request.setAttribute("taskMap", taskMap);
    }

    private void loadAllTasks(HttpServletRequest request) {
        List<ProjectTask> tasks = dao.listAllTasks();
        request.setAttribute("tasks", tasks);

        Map<Integer, ProjectTask> taskMap = new HashMap<>();
        for (ProjectTask t : tasks) {
            taskMap.put(t.getTaskId(), t);
        }
        request.setAttribute("taskMap", taskMap);
    }

    private void loadReviewedTasks(HttpServletRequest request, int reviewerId) {
        List<ProjectTask> tasks = dao.listReviewedTasksBySupervisor(reviewerId);
        request.setAttribute("tasks", tasks);

        Map<Integer, ProjectTask> taskMap = new HashMap<>();
        for (ProjectTask t : tasks) {
            taskMap.put(t.getTaskId(), t);
        }
        request.setAttribute("taskMap", taskMap);
    }

    private void loadTasksForStudent(HttpServletRequest request, int studentId) {
        List<ProjectTask> tasks = dao.listTasksForStudent(studentId);
        request.setAttribute("tasks", tasks);

        Map<Integer, ProjectTask> taskMap = new HashMap<>();
        for (ProjectTask t : tasks) {
            taskMap.put(t.getTaskId(), t);
        }
        request.setAttribute("taskMap", taskMap);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
