package controller;

import dao.UserDAO;
import entity.UserAccount;
import java.io.IOException;

import entity.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import commonconstant.JSPUrll;

import commonconstant.Attributes;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        UserAccount user = userDAO.login(username, password);

        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Role-based redirect
            if ((user.getRoleID() == 2 || user.getRoleID() == 3)) {
                // Supervisor or Admin -> supervisor dashboard
                resp.sendRedirect(req.getContextPath() + "/supervisor/dashboard");
            } else {
                // Default: student homepage - forward directly to JSP
                req.setAttribute("user", user);
                req.getRequestDispatcher("/studentHomePage.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("error", "Invalid username or password");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
