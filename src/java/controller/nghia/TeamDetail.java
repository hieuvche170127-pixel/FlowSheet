/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia;

import dal.InvitationDAO;
import dao.RoleDAO;
import dao.TeamDAO;
import dao.TeamMemberDAO;
import dao.UserDAO;

import entity.Invitation;
import entity.Team;
import entity.User;
import entity.Role;
import entity.TeamMember;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class TeamDetail extends HttpServlet {

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
            out.println("<title>Servlet TeamDetail</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TeamDetail at " + request.getContextPath() + "</h1>");
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

        // tại sao phải làm như này à, do nếu ko set thì khi mình dùng forward, 
        // server sẽ dùng loại mặc định, Nếu lần đầu dùng thì ko sao 
        // như nãy chạy vào my teamlist rồi mới nhảy vào đây 
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8"); // Đặt Content Type và Encoding

        // chặn session ở mọi nơi
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            // tuy không cần return (hoặc là có) nhưng mà để đây để đánh dấu kết thúc luồng 
            return;
        }

        TeamDAO teamDao = new TeamDAO();
        UserDAO userDao = new UserDAO();
        InvitationDAO inviDao = new InvitationDAO();
        RoleDAO roleDao = new RoleDAO();
        TeamMemberDAO teamMemberDao = new TeamMemberDAO();
        String StringteamID = request.getParameter("teamId");

        try {
            int intTeamID = Integer.parseInt(StringteamID);
            Team teamFoundByID = teamDao.findById(intTeamID);
            List<User> teamMate = userDao.findMembersByTeam(intTeamID);
            ArrayList<Invitation> invitationSendByTeam = inviDao.getAllInvitationSentByTeamId(intTeamID);
            List<Role> allRole = roleDao.findAll();
            ArrayList<TeamMember> teamMemberList = teamMemberDao.getAllTeamMembersByTeamId(intTeamID);

            // check userid exist in teammember, if not thoát luồng và hủy session
            boolean isUserInTeam = teamMemberDao.isMemberExistInTeam(user.getUserID(), intTeamID);
            if (!isUserInTeam) {
                session.invalidate();
                response.sendRedirect("login.jsp");
                return;
            }

            //nếu có 1 bản ghi chứ teammem và user id -> cho người xem xem tiếp.
            request.setAttribute("team", teamFoundByID);
            request.setAttribute("teamMateList", teamMate);
            request.setAttribute("invitationSendByTeam", invitationSendByTeam);
            request.setAttribute("allRole", allRole);
            request.setAttribute("teamMemberList", teamMemberList);

            // gửi về trang nghiapages/team_detail_member.jsp
            request.getRequestDispatcher("/nghiapages/team_detail_member.jsp").forward(request, response);

            // IDE mồm nó báo error xong nhảy mọe xuống dòng catch :))) 
            // Q1: hay có lỗi dòng cuối nhỉ :)))
            // thêm return để thử luồng. ừ nó nhảy xuống vì có lỗi thật :)))
            // nó lỗi cái gì mà khác content type ý, một cái utp 8 một cái không ? thử solution thêm setcontenttype
            return;
        } catch (NumberFormatException e) {
            // gemini code 
            // Sử dụng đường dẫn tuyệt đối từ Context Root (dấu '/')
            // Đây là phương pháp an toàn nhất để gọi một Servlet.
            // đã test luồng và oke nhé. - 14/12/2025 6h13pm thôi cứ để ở get trước có gì còn thấy atribute
            request.getRequestDispatcher("/MyTeamList").forward(request, response);
//            để đây vì thích thế :))) 
            return;
        } catch (SQLException ex) {
            Logger.getLogger(TeamDetail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TeamDetail.class.getName()).log(Level.SEVERE, "không hiểu tại sao nó lại có lỗi lúc getRequestDispatcher r forward", ex);
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
