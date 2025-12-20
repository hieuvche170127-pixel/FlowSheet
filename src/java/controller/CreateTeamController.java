package controller;

import dal.ProjectDAO;
import dal.TeamDAO;
import dal.UserAccountDAO;
import entity.Project;
import entity.Team;
import entity.TeamMember;
import entity.TeamProject;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "CreateTeamController", urlPatterns = {"/team/create"})
public class CreateTeamController extends HttpServlet {

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
            out.println("<title>Servlet CreateTeamController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreateTeamController at " + request.getContextPath() + "</h1>");
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
            response.sendRedirect(request.getContextPath() + "/login?msg=LoginRequired");
            return;
        }

        UserAccountDAO userDAO = new UserAccountDAO();
        ProjectDAO projectDAO = new ProjectDAO();

        List<UserAccount> userList = userDAO.getAllUsersForTeam();
        List<Project> projectList = projectDAO.getAllProjectsForTeam();

        request.setAttribute("userList", userList);
        request.setAttribute("projectList", projectList);

        request.getRequestDispatcher("/CreateTeam.jsp").forward(request, response);
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
            String teamName = request.getParameter("teamName");
            String description = request.getParameter("description");

            String[] memberIdsRaw = request.getParameterValues("memberIds");
            String[] roles = request.getParameterValues("roles");
            String[] projectIdsRaw = request.getParameterValues("projectIds");

            String errorMsg = null;

            if (teamName == null || teamName.trim().isEmpty()) {
                errorMsg = "Tên nhóm không được để trống!";
            }

            if (memberIdsRaw == null || memberIdsRaw.length == 0) {
                errorMsg = "Nhóm phải có ít nhất 1 thành viên!";
            }

            int leaderCount = 0;
            if (roles != null) {
                for (String role : roles) {
                    if ("Leader".equals(role)) {
                        leaderCount++;
                    }
                }
            }
            if (leaderCount > 1) {
                errorMsg = "Nhóm chỉ được phép có tối đa 1 Team Leader!";
            }

            Set<String> memberCheck = new HashSet<>();
            if (memberIdsRaw != null) {
                for (String uid : memberIdsRaw) {
                    if (!memberCheck.add(uid)) {
                        errorMsg = "Phát hiện thành viên bị trùng lặp!";
                        break;
                    }
                }
            }

            if (errorMsg != null) {
                handleError(request, response, errorMsg);
                return;
            }

            HttpSession session = request.getSession();
            UserAccount creator = (UserAccount) session.getAttribute("user");

            Team newTeam = new Team();
            newTeam.setTeamName(teamName);
            newTeam.setDescription(description);
            newTeam.setCreatedBy(creator.getUserID());

            List<TeamMember> members = new ArrayList<>();
            for (int i = 0; i < memberIdsRaw.length; i++) {
                int uid = Integer.parseInt(memberIdsRaw[i]);
                String roleString = roles[i]; 

                int roleId;
                if ("Leader".equalsIgnoreCase(roleString)) {
                    roleId = 4; // Quy ước: 4 là Leader
                } else {
                    roleId = 5; // Quy ước: 5 là Member
                }

                // Truyền số int vào constructor
                members.add(new TeamMember(0, uid, roleId));
            }

            List<TeamProject> projects = new ArrayList<>();
            if (projectIdsRaw != null) {
                for (String pidStr : projectIdsRaw) {
                    if (pidStr != null && !pidStr.isEmpty()) {
                        int pid = Integer.parseInt(pidStr);
                        projects.add(new TeamProject(0, pid));
                    }
                }
            }

            TeamDAO teamDAO = new TeamDAO();
            boolean isSuccess = teamDAO.createTeamTransaction(newTeam, members, projects);

            if (isSuccess) {
                response.sendRedirect(request.getContextPath() + "/team?msg=CreateSuccess");
            } else {
                handleError(request, response, "Lỗi hệ thống: Không thể tạo nhóm. Vui lòng thử lại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
        }
    }

private void handleError(HttpServletRequest request, HttpServletResponse response, String msg)
        throws ServletException, IOException {
    request.setAttribute("error", msg);
    doGet(request, response);
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
