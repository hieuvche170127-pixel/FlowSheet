/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.TaskReview;

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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class TaskReviewController extends HttpServlet {

    private final TaskReviewDAO dao = new TaskReviewDAO();

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

                //loadTasks(request, user.getUserID());
                loadAllTasks(request);

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
                    request.setAttribute("reviews", reviews);
                } else {
                    // students: only see reviews for tasks they have in TimesheetEntry
                    loadTasksForStudent(request, user.getUserID());

                    List<TaskReview> reviews = dao.listReviewsForStudent(filterTaskId, q, user.getUserID());
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

                    if (taskId <= 0) {
                        throw new IllegalArgumentException("Invalid taskId.");
                    }

                    String percentStr = request.getParameter("estimateWorkPercentDone");
                    String comment = request.getParameter("reviewComment");

                    Double percent = null;
                    if (percentStr != null && !percentStr.isBlank()) {
                        percent = Double.parseDouble(percentStr);
                        // entity setter will validate too :contentReference[oaicite:4]{index=4}
                        if (percent < 0 || percent > 100) {
                            throw new IllegalArgumentException("Percent must be 0..100.");
                        }
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
                        request.setAttribute("error", "Create review failed.");
                        request.setAttribute("taskId", taskId);

                        request.setAttribute("mode", "create");
                        request.setAttribute("formAction", request.getContextPath() + "/task-review?action=create");

                        request.getRequestDispatcher("/taskReview/taskReview-form.jsp").forward(request, response);
                        return;
                    }
                }

                case "edit" -> {
                    boolean isSupervisor = (user.getRoleID() == 2);
                    if (!isSupervisor) {
                        response.sendRedirect(request.getContextPath() + "/task-review?action=list&error=Access denied");
                        return;
                    }

                    int reviewId = parseIntOrMinus1(request.getParameter("reviewId"));
                    if (reviewId <= 0 || taskId <= 0) {
                        throw new IllegalArgumentException("Invalid reviewId/taskId.");
                    }

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
                        TaskReview existing = dao.getByIdAndReviewer(reviewId, user.getUserID());
                        request.setAttribute("review", existing);
                        request.setAttribute("taskId", taskId);

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

            loadTasks(request, user.getUserID());

            String action2 = request.getParameter("action");
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
                request.setAttribute("mode", "update");
                request.setAttribute("formAction", request.getContextPath() + "/task-review?action=edit");
                request.setAttribute("taskId", taskId);
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
