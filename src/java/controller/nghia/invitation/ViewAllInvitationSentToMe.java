/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.invitation;

import dal.InvitationDAO;
import dao.RoleDAO;
import entity.Invitation;
import entity.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import entity.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class ViewAllInvitationSentToMe extends HttpServlet {

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
            out.println("<title>Servlet ViewAllInvitationSentToMe</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ViewAllInvitationSentToMe at " + request.getContextPath() + "</h1>");
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

        // vì có thể có NPE nên bọc cho chắc
        String error = "";
        try {
            if (user.getRoleID() == 1) {
                ArrayList<Invitation> invitedInvitationList = invitationDao.getAllInvitationByEmail(user.getEmail());
                List<Role> allRole = roleDao.findAll();
                // Chuyển List thành Map để tra cứu theo ID cho nhanh
                Map<Integer, String> roleMap = new HashMap<>();
                for (Role r : allRole) {
                    roleMap.put(r.getRoleId(), r.getRoleName());
                }
                request.setAttribute("roleMap", roleMap);
                request.setAttribute("invitedInvitationList", invitedInvitationList);
                request.getRequestDispatcher("/nghiapages/invitation/view_all_invitation_sent_to_me.jsp").forward(request, response);
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
            session = request.getSession();
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
