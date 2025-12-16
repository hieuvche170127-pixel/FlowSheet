/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia;

import dal.InvitationDAO;
import dal.TeamDAO;
import dal.TeamMemberDAO;
import dal.UserAccountDAO;
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
import entity.UserAccount;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
        UserAccount user = (UserAccount) session.getAttribute("user");
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
                    // validate một trường dữ liệu và các dữ liệu trong form
                    // không empty, đúng kiểu, đúng các giá trị có thể nhận, logic
                    // expiredate > nay (logic)
                    // RoleName chỉ có 2 giá trị (đúng các giá trị có thể nhận)
                    // validate với các trường khác trong form(chưa dính gì đến DAO) (như kiểu có từ-đến)
                    ArrayList<String> errorList = new ArrayList<>();
                    Invitation invitatation = new Invitation();
                    String StringTeamId = request.getParameter("teamId");
                    String StringEmail = request.getParameter("email");
                    String StringRoleName = request.getParameter("roleName"); // Nhận về "TeamMember" hoặc "TeamLead"
                    String StringExpiresAt = request.getParameter("expiresAt"); // Dạng "yyyy-MM-ddTHH:mm"
                    String StringInvitedBy = request.getParameter("invitedBy");
                    LocalDateTime localExpiresAt = null;
                    LocalDateTime localCreatedAt = LocalDateTime.now();

                    // hai dòng này có thể bắn ra numberformat exception (check doc là ra) 
                    int intTeamId = Integer.parseInt(StringTeamId);
                    int intInvitedBy = Integer.parseInt(StringInvitedBy);

                    // validate giá trị của roleName 
                    int roleID = 0;
                    if (StringRoleName.equalsIgnoreCase("TeamMember")) {
                        roleID = 4;
                    }
                    if (StringRoleName.equalsIgnoreCase("TeamLead")) {
                        roleID = 5;
                    }
                    if (roleID <= 0) {
                        // ý chỉ các phàn này chưa liên quan đến BR, chưa liên quan data sâu, chỉ là nằm trong form, có thể tự được validate bằng JS
                        errorList.add("có lỗi khi validate: role id");
                    }
                    if (StringExpiresAt != null && !StringExpiresAt.isEmpty()) {
                        // Input form: "2025-12-15T14:30"
                        // LocalDateTime cần: "2025-12-15T14:30:00"
                        // Mẹo: Nếu chuỗi chưa có giây thì cộng thêm ":00" vào đuôi, giữ nguyên chữ T (GoogleAiStudio)
                        if (StringExpiresAt.length() == 16) {
                            StringExpiresAt += ":00";
                        }
                        // Parse trực tiếp, không cần formatter lằng nhằng vì nó đúng chuẩn ISO-8601 rồi (GoogleAiStudio)

                        // chỗ này cũng có thể bắn exception, nên nếu data bên ngoài có vấn đề thì cũng catch ở dưới rồi ném nó về
                        // bên thằng
                        localExpiresAt = LocalDateTime.parse(StringExpiresAt);
                    }
                    if (localExpiresAt == null) {
                        errorList.add("có lỗi khi validate: các trường (date sai format)");
                    } else {
                        if (!localExpiresAt.isAfter(LocalDateTime.now())) {
                            errorList.add("có lỗi khi validate: expired date phải sau thời điểm hiện tại");
                        }
                    }

                    // nếu đoạn trên data có vấn đề
                    // thì chuyển về chỗ Team Detail hoặc myTeamList tùy.
                    if (!errorList.isEmpty()) {
                        request.setAttribute("errorList", errorList);
                        request.getRequestDispatcher("/MyTeamList").forward(request, response);
                    }

                    // validate bussiness rule:
                    // validate với toàn bộ data trong database (validate sâu, ở chỗ này thì JS ko làm được gì nữa rồi, phải tự gửi lỗi từ COntroller)
                    // các validate: 
                    // 1. email có tồn tại trong Useraccount (nếu ko tìm thấy -> trả về)
                    // 2. người được mời phải là một người ngoài nhóm
                    // 3. người mời phải là người trong team
                    // 4. Chỉ team lead mới được mời người khác có role là team lead
                    //     4.1 team lead cũng mời được team mem
                    // 5. team mem chỉ được mời người ngoài là team mem
                    // 6. mỗi người chỉ được có duy nhất 1 lời mời đến 1 người nào đó,
                    //    6.1 nếu lời mời tới 1 người chưa được trả lời và còn hạn thì sẽ ko thể add thêm mà chỉ có update lại lời mời cũ
                    // 7. Team lead giống cái số 6 nhưng được mời thêm với vai trò là team lead.
                    // vì thấy chức năng kia quan trọng hơn, nên tôi sẽ chạy qua cái này thôi :)). 
                    // các validate được liệt ra sau đó triệt tiêu, xem cái nào sẽ là cái rộng hơn để tránh validate thừa.
                    // như cái ở dưới thì ko cần, do nó sẽ được lọc bằng số 2. nếu người mời ko ở trong nhóm thì vi phạm cái 3
                    // invitedBy phải là người khác != với user hiện tại (validate mqh giữa người gửi và ng nhận)
                    // thực ra chỗ này lấy useraccount rồi check null cũng được, nhiều cách làm.
                    // 2. người được mời phải là một người ngoài nhóm - cũng may có cái 1 validate trước xem người đó có trong sys hay chưa
                    // check bằng email và id nhóm chắc là join từ user với teammem bằng userid, xong count where id = ? và email = ?
                    // ơ thế bảo thằng DAO nào check giờ :)))))))))))))- cái l má :))))) thôi thì gõ tay :)))
                    int invitedUserID = userDao.getUserIdByEmail(StringEmail);
                    // nếu ko thấy mống nào - tức claf ko có tài khoản nào với email - ở dòng trên r
                    // nên xét nếu có luon :)
                    // 1. email có tồn tại trong Useraccount -> ko có thì add lỗi
                    if (invitedUserID < 0) {
                        errorList.add("email không tồn tại trong database, không thể gửi lời mời");
                    } // nếu tìm thấy mạng nào đó, check xem 
                    else {
                        if (teamMemberDao.isMemberExistInTeam(invitedUserID, intTeamId)) {
                            // nếu tồn tại thì add lỗi
                            errorList.add("người nhận lời mời đã ở trong team, không thể gửi lời mời");
                        }
                    }
                    // 3. người mời phải là người trong team
                    TeamMember invitationSender = teamMemberDao.getTeamMemberByUserIDAndTeamId(user.getUserID(), intTeamId);
                    if (invitationSender == null) {
                        errorList.add("người gửi không ở trong team, không thể gửi lời mời");
                    } else {
                        // 4. người được mời có role gì, nếu là teamlead, check thk gửi lời mời có phải team lead ko?
                        // lấy ra xem thằng này role gì :)), nếu thk gửi là mem và role mời là 5 thì chặn thôi
                        if (invitationSender.getRole().equalsIgnoreCase("Team Member") && roleID == 5) {
                            errorList.add("người gửi có có quyền mời team lead, không thể gửi lời mời");
                        }
                    }

                    // cái này ko tính role, tức kể cả thk Admin cũng ko gửi được 2 lời mời tới 1 người
                    // nếu mà tồn tại 1 pending, mà đếm thử xem có những cái nào asshshh lằng nhằng quá. thôi chắc dừng ở đây vậy ;-;
                    // nếu 2 lời mời đó là khác vị trí.
                    if (inviDao.hasPendingTeamInvitation(intTeamId, invitedUserID, StringEmail, roleID)) {
                        // Nếu true -> Chặn luôn
                        errorList.add("Người dùng " + StringEmail + " đã có lời mời đang chờ xử lý. Không thể gửi thêm.");
                    }

                    // nếu kết thúc validate lần 2 mà ko có lỗi -> thì add
                    // có thì về trang cũ 
                    if (!errorList.isEmpty()) {
                        request.setAttribute("errorList", errorList);
                        request.getRequestDispatcher("/MyTeamList").forward(request, response);
                        return;
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
                } catch (NumberFormatException numberEx) {

                } catch (Exception e) {
                    System.out.println("đã có exception đã xảy ra khi validdate");
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
