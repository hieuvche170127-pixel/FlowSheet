package controller;

import dal.ProjectDAO;
import dal.TaskDAO;
import entity.Project;
import entity.ProjectTask;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@WebServlet(name = "ProjectDetailsController", urlPatterns = {"/project/details"})
public class ProjectDetailsController extends HttpServlet {

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
            out.println("<title>Servlet ProjectDetailsController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProjectDetailsController at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession();
        UserAccount currentUser = (UserAccount) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        String action = request.getParameter("action");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/projects");
            return;
        }

        try {
            int projectId = Integer.parseInt(idStr);
            ProjectDAO dao = new ProjectDAO();
            TaskDAO taskDAO = new TaskDAO();

            Project project = dao.getProjectById(projectId);
            if (project == null) {
                request.setAttribute("error", "Project not found!");
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                return;
            }

            if ("deactive".equals(action) && idStr != null) {
                dao.deactiveProject(projectId, false);
                session.setAttribute("notification", "Dự án đã được ngưng hoạt động!");
                response.sendRedirect(request.getContextPath() + "/projects");
                return;
            }
            List<UserAccount> members = dao.getProjectMembers(projectId);
            String indexPage = request.getParameter("page");
            if (indexPage == null) {
                indexPage = "1";
            }
            int index = Integer.parseInt(indexPage);

            int totalTasks = taskDAO.countTasksByProject(projectId);

            int tasksPerPage = 5;

            int endPage = totalTasks / tasksPerPage;
            if (totalTasks % tasksPerPage != 0) {
                endPage++;
            }

            List<ProjectTask> tasks = taskDAO.getTasksInProject(projectId, index, tasksPerPage);

            request.setAttribute("project", project);
            request.setAttribute("members", members);
            request.setAttribute("tasks", tasks);
            request.setAttribute("currentPage", index);
            request.setAttribute("endPage", endPage);

            request.getRequestDispatcher("/ProjectDetails.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/projects");
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
        processRequest(request, response);
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
