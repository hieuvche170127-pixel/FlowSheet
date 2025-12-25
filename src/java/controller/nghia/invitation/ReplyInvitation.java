/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.invitation;

import dal.InvitationDAO;
import dal.TeamMemberDAO;
import dao.RoleDAO;
import entity.Invitation;
import entity.Role;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class ReplyInvitation extends HttpServlet {

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
            out.println("<title>Servlet ReplyInvitation</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReplyInvitation at " + request.getContextPath() + "</h1>");
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
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8"); // Đặt Content Type và Encoding

        HttpSession session = request.getSession(true);
        UserAccount user = (UserAccount) session.getAttribute("user");
        InvitationDAO invitationDao = new InvitationDAO();
        TeamMemberDAO teamMemDao = new TeamMemberDAO();
        RoleDAO roleDao = new RoleDAO();
        if (user == null) {
            response.sendRedirect("login.jsp");
            // tuy không cần return (hoặc là có) nhưng mà để đây để đánh dấu kết thúc luồng 
            return;
        }

        // check null trước để ko NPE 
        if (user.getRoleID() == null) {
            session.invalidate();
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            if (user.getRoleID() == 1) {
                String action = request.getParameter("action"); // accept hoặc reject
                String invitationidString = request.getParameter("id");

                int invitationId = Integer.parseInt(invitationidString);
                boolean isAbleToReply = invitationDao.countSpecificPendingInvitation(invitationId, user.getEmail());
                Invitation invitation = invitationDao.getInvitationByInvitationId(invitationId);

                // nếu có thể update - tức là đúng người, đúng thời điểm, chưa bị cancell
                if (isAbleToReply) {
                    if (action.equalsIgnoreCase("ACCEPTED")
                            || action.equalsIgnoreCase("reject")) {
                        boolean replyStatus;
                        boolean editInvitationStatus = invitationDao.editStatus(invitationId, action);
                        boolean addTeamMemberStatus = false;
                        if (action.equalsIgnoreCase("ACCEPTED")) {
                            addTeamMemberStatus = teamMemDao.addTeamMember(user.getUserID(), invitation.getTeamId(), invitation.getRoleId());
                            replyStatus = addTeamMemberStatus;
                        } else {
                            replyStatus = true;
                        }
                        session.setAttribute("replyStatus", replyStatus);

                    }
                } else {
                    String errorMsg = "người dùng ko có quyền update hoặc là lời mời đã bị hủy, hết hạn, hoặc đã được trả lời.";
                    session.setAttribute("sessionError", errorMsg);
                }
                // vẫn gửi về bên viewall để lấy data
                response.sendRedirect(request.getContextPath() + "/ViewAllInvitationSentToMe");
                return;
            } else {
                //redirect về homepage tương ứng với 2 và 3, còn lại thì session.invalidate rồi tống về login
                if (user.getRoleID() == 2 || user.getRoleID() == 3) {
                    // redirect về trang tương ứng
                    response.sendRedirect(request.getContextPath() + "/supervisor/dashboard");
                    return;
                } else {
                    session.invalidate();
                    response.sendRedirect("login.jsp");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
            String errorMsg = "Đã có exception xảy ra: " + e.getMessage();
            session.setAttribute("sessionError", errorMsg);
            request.getRequestDispatcher("/nghiapages/errorPage.jsp").forward(request, response);
            return;
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
