package controller;

import dal.ProjectDAO;
import entity.Project;
import entity.Team;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "CreateProjectController", urlPatterns = {"/project/create"})
public class CreateProjectController extends HttpServlet {

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
            out.println("<title>Servlet CreateProjectController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreateProjectController at " + request.getContextPath() + "</h1>");
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
        UserAccount currentUser = (UserAccount) session.getAttribute("LOGIN_USER"); // Key phải khớp với lúc Login

        if (currentUser == null) {

            currentUser = new UserAccount();
            currentUser.setUserID(3);
            currentUser.setUsername("stu_anh");
            currentUser.setFullName("Nguyen Hoang Anh (Test)");
            currentUser.setRoleID(1);

            session.setAttribute("user", currentUser);
            System.out.println("--- ĐÃ KÍCH HOẠT CHẾ ĐỘ TEST USER ---");
        }
//        if (currentUser == null) {
//            response.sendRedirect(request.getContextPath() + "/login.jsp");
//            return;
//        }

        ProjectDAO dao = new ProjectDAO();

        List<Team> teams = dao.getAllTeamsForProject();
        List<UserAccount> users = dao.getAllActiveMembers();

        request.setAttribute("teamList", teams);
        request.setAttribute("userList", users);

        request.getRequestDispatcher("/CreateProject.jsp").forward(request, response);
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
        request.setCharacterEncoding("UTF-8");

        try {
            String name = request.getParameter("projectName");
            String code = request.getParameter("projectCode");
            String startStr = request.getParameter("startDate");
            String endStr = request.getParameter("deadline");
            String desc = request.getParameter("description");

            Date startDate = (startStr != null && !startStr.isEmpty()) ? Date.valueOf(startStr) : null;
            Date deadline = (endStr != null && !endStr.isEmpty()) ? Date.valueOf(endStr) : null;

            Project p = new Project();
            p.setProjectName(name);
            p.setProjectCode(code);
            p.setStartDate(startDate);
            p.setDeadline(deadline);
            p.setDescription(desc);
            p.setStatus("OPEN");

            ProjectDAO dao = new ProjectDAO();
            int newProjectId = dao.createProject(p);

            if (newProjectId > 0) {
                String assignType = request.getParameter("assignType");

                if ("team".equals(assignType)) {
                    String teamIdStr = request.getParameter("teamId");
                    if (teamIdStr != null && !teamIdStr.isEmpty()) {
                        try {
                            int teamId = Integer.parseInt(teamIdStr);
                            dao.addTeamToProject(newProjectId, teamId);
                            dao.importMembersFromTeam(newProjectId, teamId);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                } else if ("member".equals(assignType)) {
                    String[] memberIdsRaw = request.getParameterValues("memberIds");
                    String[] memberRoles = request.getParameterValues("memberRoles");

                    if (memberIdsRaw != null) {
                        for (int i = 0; i < memberIdsRaw.length; i++) {
                            String uidStr = memberIdsRaw[i];
                            if (uidStr != null && !uidStr.isEmpty()) {
                                try {
                                    int uid = Integer.parseInt(uidStr);
                                    String role = "Member";
                                    if (memberRoles != null && i < memberRoles.length) {
                                        role = memberRoles[i];
                                    }
                                    dao.addMemberToProject(newProjectId, uid, role);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/projects");
            } else {
                request.setAttribute("error", "Create project failed! Project Code existed.");
                request.setAttribute("p", p);
                doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/views/error.jsp");
        }
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
