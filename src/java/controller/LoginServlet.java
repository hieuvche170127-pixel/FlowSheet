package controller;

import dao.UserDAO;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/FlowSheet/login.jsp")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/FlowSheet/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = userDAO.login(username, password);

        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Role-based redirect
            String redirectUrl = switch (user.getRoleID()) {
//                case "ADMIN"      -> "admin/dashboard.jsp";
//                case "SUPERVISOR" -> "supervisor/dashboard.jsp";
                default           -> "student/timesheet.jsp"; // STUDENT
            };
            resp.sendRedirect(redirectUrl);
        } else {
            req.setAttribute("error", "Invalid username or password");
            req.getRequestDispatcher("/FlowSheet/login.jsp").forward(req, resp);
        }
    }
}
