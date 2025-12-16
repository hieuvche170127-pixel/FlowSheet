package controller;

import dao.UserDAO;
import entity.UserAccount;

import entity.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // No token check → always show registration form
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");

        // Validation
        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (userDAO.isUsernameExists(username)) {
            req.setAttribute("error", "Username already taken.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        UserAccount newUser = new UserAccount();
        newUser.setUsername(username);
        newUser.setPassword(password);          // plain text – matches current lab setup
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setRoleID(1);                   // Hardcoded STUDENT (RoleID = 1)

        if (userDAO.createUser(newUser)) {
            // Auto-login after registration
            HttpSession session = req.getSession();
            session.setAttribute("user", newUser);

            //resp.sendRedirect(req.getContextPath() + "/student/timesheet.jsp");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } else {
            req.setAttribute("error", "Registration failed. Please try again.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
