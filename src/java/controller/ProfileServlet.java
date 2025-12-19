package controller;

import dao.UserDAO;
import entity.UserAccount;
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
        UserAccount user = (UserAccount) session.getAttribute("user");

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
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");

        if ("updateProfile".equals(action)) {
            String username = req.getParameter("username");
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                req.setAttribute("error", "Username is required.");
            } else if (fullName == null || fullName.trim().isEmpty()) {
                req.setAttribute("error", "Full name is required.");
            } else if (email == null || email.trim().isEmpty()) {
                req.setAttribute("error", "Email is required.");
            } else {
                // Check if username is changed and if it already exists for another user
                if (!username.equals(user.getUsername()) && userDAO.isUsernameExistsForOtherUser(username, user.getUserID())) {
                    req.setAttribute("error", "Username already exists. Please choose another username.");
                } else {
                    user.setUsername(username.trim());
                    user.setFullName(fullName.trim());
                    user.setEmail(email.trim());

                    if (userDAO.updateUserProfile(user)) {
                        // Update session with new username
                        session.setAttribute("user", user);
                        req.setAttribute("success", "Profile updated successfully.");
                    } else {
                        req.setAttribute("error", "Failed to update profile. Please try again.");
                    }
                }
            }
        } else if ("changePassword".equals(action)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            // Validate input
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                req.setAttribute("error", "Current password is required.");
            } else if (newPassword == null || newPassword.trim().isEmpty()) {
                req.setAttribute("error", "New password is required.");
            } else if (newPassword.length() < 4) {
                req.setAttribute("error", "New password must be at least 4 characters.");
            } else if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("error", "New passwords do not match.");
            } else if (userDAO.verifyPassword(user.getUserID(), currentPassword)) {
                // Current password is correct, update to new password
                if (userDAO.changePassword(user.getUserID(), newPassword)) {
                    req.setAttribute("success", "Password changed successfully.");
                } else {
                    req.setAttribute("error", "Failed to change password. Please try again.");
                }
            } else {
                req.setAttribute("error", "Current password is incorrect.");
            }
        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }
}
