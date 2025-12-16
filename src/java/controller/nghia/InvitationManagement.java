/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia;

import dal.InvitationDAO;
import dao.TeamDAO;
import dao.TeamMemberDAO;
import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Timestamp;
import entity.Invitation;
import entity.TeamMember;
import entity.User;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class InvitationManagement extends HttpServlet {

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
            out.println("<title>Servlet Invitation</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Invitation at " + request.getContextPath() + "</h1>");
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
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            // tuy không cần return (hoặc là có) nhưng mà để đây để đánh dấu kết thúc luồng 
            return;
        }

        String action = request.getParameter("action");
        InvitationDAO inviDao = new InvitationDAO();
        UserDAO userDao = new UserDAO();
        TeamMemberDAO teamMemberDao = new TeamMemberDAO();

        // Nếu action là null hoặc rỗng, coi như là default
        if (action == null || action.isEmpty()) {
            action = "default";
        }

        switch (action) {
            case "add": {
                try {
                    // bussiness rule:
                    // người trong team mới được mời, và phải mời người ở ngoài team 

                    //validate một trường dữ liệu
                    // không empty, đúng kiểu, đúng các giá trị có thể nhận, logic
                    // expiredate > nay (logic)
                    // RoleName chỉ có 2 giá trị (đúng các giá trị có thể nhận)
                    // validate với các trường khác trong form(chưa dính gì đến DAO) (như kiểu có từ-đến)
                    // invitedBy phải là người khác != với user hiện tại (validate mqh giữa người gửi và ng nhận)
                    // validate với toàn bộ data trong database
                    // email có tồn tại trong Useraccount (nếu ko tìm thấy -> trả về)
                    // nếu tìm thấy người, mà người đấy trong nhóm -> báo lỗi
                    // email phải là của 1 người ngoài nhóm 
                    // Teamid có tồn tại và người gửi trong team
                    String errorString = "";
                    Invitation invitatation = new Invitation();
                    String StringTeamId = request.getParameter("teamId");
                    String StringEmail = request.getParameter("email");
                    String StringRoleName = request.getParameter("roleName"); // Nhận về "TeamMember" hoặc "TeamLead"
                    String StringExpiresAt = request.getParameter("expiresAt"); // Dạng "yyyy-MM-ddTHH:mm"
                    String StringInvitedBy = request.getParameter("invitedBy");
                    LocalDateTime localExpiresAt = null;
                    LocalDateTime localCreatedAt = LocalDateTime.now();

                    int intTeamId = Integer.parseInt(StringTeamId);
                    int intInvitedBy = Integer.parseInt(StringInvitedBy);

                    // chỗ này siêu hardString, nên bảo trì thì ối dồi ôi :))), nhìn code validate của mình
                    // nhìn bần vl :)))) 
                    int roleID = 0;
                    if (StringRoleName.equalsIgnoreCase("TeamMember")) {
                        roleID = 4;
                    }
                    if (StringRoleName.equalsIgnoreCase("TeamLead")) {
                        roleID = 5;
                    }
                    if (roleID <= 0) {
                        throw new Exception("role khoong hop le ");
                    }
                    if (StringExpiresAt != null && !StringExpiresAt.isEmpty()) {
                        // Input form: "2025-12-15T14:30"
                        // LocalDateTime cần: "2025-12-15T14:30:00"
                        // Mẹo: Nếu chuỗi chưa có giây thì cộng thêm ":00" vào đuôi, giữ nguyên chữ T
                        if (StringExpiresAt.length() == 16) {
                            StringExpiresAt += ":00";
                        }
                        // Parse trực tiếp, không cần formatter lằng nhằng vì nó đúng chuẩn ISO-8601 rồi
                        localExpiresAt = LocalDateTime.parse(StringExpiresAt);
                    }
                    if (localExpiresAt != null) {
                        if (!localExpiresAt.isAfter(LocalDateTime.now())) {
                            errorString = "expired date phải sau thời điểm hiện tại";
                            request.setAttribute("errorString", errorString);
                            request.getRequestDispatcher("nghia/TeamDetail.jsp").forward(request, response);
                        }
                    }

                    // validate với toàn bộ data trong database
                    // email có tồn tại trong Useraccount (nếu ko tìm thấy -> trả về)
                    // nếu tìm thấy người, mà người đấy trong nhóm -> báo lỗi
                    // email phải là của 1 người ngoài nhóm 
                    // Teamid có tồn tại và người gửi trong team
                    // vì thấy chức năng kia quan trọng hơn, nên tôi sẽ chạy qua cái này thôi :)). 
                    if (!userDao.isEmailExists(StringEmail)) {
                        errorString = "email không tồn tại trong database, không thể gửi lời mời";
                        request.setAttribute("errorString", errorString);
                        request.getRequestDispatcher("nghia/TeamDetail.jsp").forward(request, response);
                    }
                    invitatation.setInvitedById(intInvitedBy);
                    invitatation.setTeamId(intTeamId);
                    invitatation.setEmail(StringEmail);
                    invitatation.setRoleId(roleID);
                    invitatation.setExpiresAt(localExpiresAt);
                    invitatation.setCreatedAt(localCreatedAt);
                    // mới validate phần nội dung add. chưa validate phần business(nghiệp vụ - check trùng các thứ)

                    boolean addStatus = inviDao.addInvitation(invitatation);
                    request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
                } catch (Exception e) {
                    System.out.println("đã có lỗi khi validdate");
                    request.getRequestDispatcher("/MyTeamList").forward(request, response);
                }
                break;
            }

            case "edit": {
                // new param new role, new thời hạn hết, new người được mời - email  tự tạo cái new createAt.
                // cũ: invitationid cũ để còn update
                try {
                    // validate nếu cái user.session nó ko đúng với invitation -> hủy sessio user luôn
                    String invitationIdStr = request.getParameter("invitationId");
                    String email = request.getParameter("email");
                    String roleIdStr = request.getParameter("roleId");
                    String expiresAtStr = request.getParameter("expiresAt");
                    LocalDateTime localCreatedAt = LocalDateTime.now();

                    int roleId = Integer.parseInt(roleIdStr);
                    int invitationId = Integer.parseInt(invitationIdStr);

                    Invitation updatedInvitation = new Invitation();
                    updatedInvitation.setEmail(email);
//                    updatedInvitation.setRoleId();
//                    updatedInvitation.

                    boolean editResult = inviDao.editInvitation(updatedInvitation);
                    if (editResult) {
                        request.setAttribute("result String", "add successfully");
                    }
                    request.getRequestDispatcher("/MyTeamList").forward(request, response);
                } catch (NumberFormatException numberFormatException) {
                    // set lỗi nhưng lười quá :)

                } catch (Exception e) {

                }
                break;
            }

            case "delete":
                // TODO: Xử lý logic cho việc xóa
                System.out.println("Thực hiện hành động: delete");
                break;

            // ... các case khác nếu có
            default:
                // Nếu action không khớp với bất kỳ case nào ở trên,
                // hoặc là null/rỗng ban đầu, thì forward về trang MyTeamList
                System.out.println("Hành động không xác định, chuyển về MyTeamList.");
                request.getRequestDispatcher("/MyTeamList").forward(request, response);
                break;
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
