package controller;

import dao.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");

//        if ("updateProfile".equals(action)) {
//            String fullName = req.getParameter("fullName");
//            String email = req.getParameter("email");
//
//            user.setFullName(fullName);
//            user.setEmail(email);
//
//            if (userDAO.updateUserProfile(user)) {
//                req.setAttribute("success", "Profile updated successfully.");
//            } else {
//                req.setAttribute("error", "Failed to update profile. Please try again.");
//            }
//        } else if ("changePassword".equals(action)) {
//            String currentPassword = req.getParameter("currentPassword");
//            String newPassword = req.getParameter("newPassword");
//            String confirmPassword = req.getParameter("confirmPassword");
//
//            // Verify current password (plain-text comparison)
//            if (!currentPassword.equals(user.getPassword())) {
//                req.setAttribute("error", "Current password is incorrect.");
//            } else if (!newPassword.equals(confirmPassword)) {
//                req.setAttribute("error", "New passwords do not match.");
//            } else {
//                if (userDAO.changePassword(user.getUserID(), newPassword)) {
//                    user.setPassword(newPassword); // Update session
//                    req.setAttribute("success", "Password changed successfully.");
//                } else {
//                    req.setAttribute("error", "Failed to change password. Please try again.");
//                }
//            }
//        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }
}
