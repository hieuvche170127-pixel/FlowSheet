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

// import log nhìn cho chuyên nghiệp :)))))) 
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Admin
 */
public class TeamInvitationManagement extends HttpServlet {

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
                    // 4. teammem ko được mời teamlead
                    // 6. mỗi người chỉ được có duy nhất 1 lời mời đến 1 người nào đó,
                    //    6.1 nếu lời mời tới 1 người chưa được trả lời và còn hạn thì sẽ ko thể add thêm mà chỉ có update lại lời mời cũ
                    // 7. Team lead giống cái số 6 nhưng được mời thêm với vai trò là team lead.
                    // 8.  người được add phải là student ? 
                    // 1. email có tồn tại trong Useraccount -> ko có thì add lỗi
                    int invitedUserID = userDao.getUserIdByEmail(StringEmail);
                    if (invitedUserID < 0) {
                        errorList.add("email không tồn tại trong database, không thể gửi lời mời");
                    } else {
                        // 2. người được mời phải là một người ngoài nhóm - cũng may có cái 1 validate trước xem người đó có trong sys hay chưa
                        if (teamMemberDao.isMemberExistInTeam(invitedUserID, intTeamId)) {
                            // nếu tồn tại thì add lỗi
                            errorList.add("người nhận lời mời đã ở trong team, không thể gửi lời mời");
                        }
                        // nếu người được mời ko phải học sinh.
                        if (userDao.getRoleIdByEmail(StringEmail) != 1) {
                            errorList.add("người nhận lời mời không phải student, không thể gửi lời mời");
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

                    // 6. 
                    // cái này ko tính role, tức kể cả thk Admin cũng ko gửi được 2 lời mời tới 1 người
                    // nếu mà tồn tại 1 pending, mà đếm thử xem có những cái nào asshshh lằng nhằng quá. thôi chắc dừng ở đây vậy ;-;
                    // nếu 2 lời mời đó là khác vị trí.
                    if (inviDao.hasPendingTeamInvitation(intTeamId, user.getUserID(), StringEmail, roleID)) {
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
                    System.out.println("đã có exception đã xảy ra khi validdate");
                    request.getRequestDispatcher("/MyTeamList").forward(request, response);
                } catch (Exception e) {
                    System.out.println("đã có exception đã xảy ra khi validdate");
                    request.getRequestDispatcher("/MyTeamList").forward(request, response);
                }
                break;
            }

            case "edit": {
                try {
                    ArrayList<String> errorList = new ArrayList<>();
                    String invitationIdStr = request.getParameter("invitationId");
                    String email = request.getParameter("email");
                    String roleIdStr = request.getParameter("roleId");
                    String StringExpiresAt = request.getParameter("expiresAt");
                    String status = request.getParameter("status");

                    // expired mớ - sau thời điểm bây giờ  (checked)
                    // status mới - nhận 1 trong 2 giá trị pending và cancelled (checked)
                    // roleid là 4 hoặc 5 ? - là teammem hoặc team lead  (checked)
                    LocalDateTime localExpiresAt = null;
                    int invitationId = Integer.parseInt(invitationIdStr);
                    // dựa trên value form gửi
                    int roleId = Integer.parseInt(roleIdStr);
                    if (roleId != 4 && roleId != 5) {
                        errorList.add("Chọn không đúng role.");
                    }
                    if (StringExpiresAt != null && !StringExpiresAt.isEmpty()) {
                        if (StringExpiresAt.length() == 16) {
                            StringExpiresAt += ":00";
                        }
                        localExpiresAt = LocalDateTime.parse(StringExpiresAt);
                    }
                    if (!status.equalsIgnoreCase("PENDING") && !status.equalsIgnoreCase("CANCELLED")) {
                        errorList.add("Status ko hợp lệ.");
                    }

                    // validate sâu
                    // 1. chỉ có người tạo mới là người được chỉnh sửa  (checked)
                    // 2. email mới có trong hệ thống (checked)
                    // 3. người được mời mới theo email phải là người ở ngoài nhóm (checked)
                    // 4. nếu invitation cũ đã expired, accepted != null thì ko được sửa nữa. (checked)
                    // 5. có cho chỉnh từ cancelled sang pending (mời lại) nhưng phải thỏa mãn điều kiện (4)
                    // tức là cái đấy phải chưa trả lời, và expired cũ chưa qua (tức chưa hết hạn- nếu hết hạn thì ko cho update)
                    Invitation oldInvitationBeforeUpdate = inviDao.getInvitationByInvitationId(invitationId);
                    // so user session
                    if (oldInvitationBeforeUpdate.getInvitedById() != user.getUserID()) {
                        errorList.add("Người dùng ko có quyền update");
                    }
                    // 2 và 3
                    int invitedUserID = userDao.getUserIdByEmail(email);
                    if (invitedUserID < 0) {
                        errorList.add("email không tồn tại trong database, không thể gửi lời mời");
                    } // nếu tìm thấy mạng nào đó, check xem 
                    else {
                        if (teamMemberDao.isMemberExistInTeam(invitedUserID, oldInvitationBeforeUpdate.getTeamId())) {
                            // nếu tồn tại thì add lỗi
                            errorList.add("người nhận lời mời đã ở trong team, không thể gửi lời mời");
                        }
                        if (userDao.getRoleIdByEmail(email) != 1) {
                            errorList.add("người nhận lời mời không phải student, không thể gửi lời mời");
                        }
                    }
                    if (oldInvitationBeforeUpdate.getExpiresAt().isBefore(LocalDateTime.now())
                            || oldInvitationBeforeUpdate.getAcceptedAt() != null) {
                        errorList.add("lời mời đã hết hạn hoặc đã được reply");
                    }
                    if (!errorList.isEmpty()) {
                        request.setAttribute("errorList", errorList);
                        request.getRequestDispatcher("/MyTeamList").forward(request, response);
                        return;
                    }
                    //nếu chưa expired và chwua except (có thể là cancell hoặc pending thì vẫn chuyển được)
                    // thực thi sau validate
                    Invitation updatedInvitation = new Invitation();
                    updatedInvitation.setInvitationId(invitationId);
                    updatedInvitation.setEmail(email);
                    updatedInvitation.setRoleId(roleId);
                    updatedInvitation.setStatus(status);
                    updatedInvitation.setExpiresAt(localExpiresAt);
                    boolean editResult = inviDao.editInvitation(updatedInvitation);

                    if (editResult) {
                        request.setAttribute("action result", "add successfully");
                    }
                    request.getRequestDispatcher("/MyTeamList").forward(request, response);
                    return;
                } catch (NumberFormatException numberFormatException) {
                    System.out.println("đã có exception đã xảy ra khi parse string to int");
                    request.getRequestDispatcher("/MyTeamList").forward(request, response);
                } catch (Exception e) {
                    System.out.println("đã có exception đã xảy ra khi validdate");
                    request.getRequestDispatcher("/MyTeamList").forward(request, response);
                }
                break;
            }

            case "delete": {
                // TODO: Xử lý logic cho việc xóa
                // 1. chỉ có người tạo ra hoặc teamlead mới có thể xóa  
                // nếu user là team lead, cho xóa
                // else, nếu người xóa đúng là người tạo -> cho xóa
                ArrayList<String> errorList = new ArrayList<>();
                try {
                    String stringInvitationId = request.getParameter("invitationId");

                    // do ảnh hưởng nhiều nên return  luôn - hoặc đợi NPE vì mình cx có try catch ?
                    if (stringInvitationId == null) {
                        errorList.add("the invitation string is null");
                        request.setAttribute("errorList", errorList);
                        request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
                    }

                    int intInvitationId = Integer.parseInt(stringInvitationId);
                    Invitation deletedInvitation = inviDao.getInvitationByInvitationId(intInvitationId);
                    // nếu ko tìm thấy 
                    if (deletedInvitation == null) {
                        errorList.add("the invitation is not exist in the database");
                    } else {
                        // lấy thk user hiện tại(trong session) xem role nó là gì
                        TeamMember teamMember = teamMemberDao.getTeamMemberByUserIDAndTeamId(user.getUserID(), deletedInvitation.getTeamId());
                        // trước khi xem role thì xem nó có trong team hay ko ? 
                        if (teamMember == null) {
                            errorList.add("bạn ko ở trong team, ko thể thay xóa lời mời.");
                        } else {
                            if (teamMember.getRole().equalsIgnoreCase("Team Leader")
                                    || deletedInvitation.getInvitedById() == user.getUserID()) {
                                boolean result = inviDao.deleteInvitation(intInvitationId);
                                if (result) {
                                    request.setAttribute("Result", "delete successfull");
                                } else {
                                    request.setAttribute("Result", "delete failed, i dont know why :))), some exception might happened");
                                }
                                request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
                                return;
                            }
                            if (deletedInvitation.getInvitedById() != user.getUserID()) {
                                // để errorlist cho đồng nhất -> jsp dễ lấy. 
                                errorList.add("bạn không có quyền xóa invitation này");
                            }
                        }
                    }
                    
                    request.setAttribute("errorList", errorList);
                    request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
                    return;
                } catch (NumberFormatException ex) {
                    errorList.add("the invitation string is not able to parse to int, wrong input type");
                    request.setAttribute("errorList", errorList);
                    request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
                } catch (Exception e) {
                    errorList.add("An exception has occur when delete the invitation");
                    request.setAttribute("errorList", errorList);
                    request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
                }
                break;
            }

//            case "updateStatus": {
//                // new status chỉ có thể là 2 giá trị: pending, cancell 1.
//
//                // giống update thôi,
//                // chỉ có người tạo ra hoặc teamlead mới có thể update? thế sao nó ko xóa :))))  
//                // t cũng ko biết :)))) 
//                // nếu user là team lead, cho xóa
//                // else, nếu người xóa đúng là người tạo -> cho xóa
//                // chir update được khih còn hạn && chưa được trả lời
//                try {
//                    ArrayList<String> errorList = new ArrayList<>();
//                    String stringInvitationId = request.getParameter("invitationId");
//                    String newStatus = request.getParameter("newstatus");
//
//                    // Validate tham số đầu vào cơ bản
//                    if (stringInvitationId == null || newStatus == null) {
//                        errorList.add("Tham số không hợp lệ.");
//                        request.setAttribute("errorList", errorList);
//                        request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
//                        return;
//                    }
//
//                    int intInvitationId = Integer.parseInt(stringInvitationId);
//                    Invitation updatedInvitation = inviDao.getInvitationByInvitationId(intInvitationId);
//
//                    // invi ko valid
//                    if (updatedInvitation == null) {
//                        errorList.add("lời mời ko tồn tại.");
//                    } else {
//                        // nếu đã được trả lời hoặc đã hết hạn.
//                        if (updatedInvitation.getAcceptedAt() != null
//                                || updatedInvitation.getExpiresAt().isBefore(LocalDateTime.now())) {
//                            errorList.add("lời mời đã hết hạn hoặc đã được reply");
//                        }
//                    }
//
//                    // phải là 1 trong 2 state
//                    if (!newStatus.equalsIgnoreCase("PENDING") && !newStatus.equalsIgnoreCase("CANCELLED")) {
//                        errorList.add("status phải là pending hoặc là cancelled");
//                    }
//
//                    if (errorList.isEmpty()) {
//                        //lấy người hiện tại role gì, nếu teamlead, cho xóa, ko thì phải là người tạo ra.
//                        TeamMember teamMember = teamMemberDao.getTeamMemberByUserIDAndTeamId(user.getUserID(), updatedInvitation.getTeamId());
//                        // nếu ông được lấy ra là null- tức là ko có ở trong team
//                        if (teamMember == null) {                             // tránh NPE
//                            errorList.add("bạn ko ở trong team, ko thể thay đổi lời mời.");
//                        } else {
//                            if (teamMember.getRole().equalsIgnoreCase("Team Leader")
//                                    || updatedInvitation.getInvitedById() == user.getUserID()) {
//                                boolean result = inviDao.editStatus(intInvitationId, newStatus);
//                                if (result) {
//                                    request.setAttribute("Result", "update successfull");
//                                } else {
//                                    request.setAttribute("Result", "update failed, i dont know why :))), some exception might happened");
//                                }
//                                request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
//                                return;
//                            } else {
//                                errorList.add("bạn không có quyền xóa invitation này");
//                            }
//                        }
//                    }
//                    request.setAttribute("errorList", errorList);
//                    request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
//                    return;
//                } catch (NumberFormatException e) {
//                    request.setAttribute("error", "ID lời mời không hợp lệ.");
//                    request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    request.setAttribute("error", "Đã xảy ra lỗi hệ thống.");
//                    request.getRequestDispatcher("/nghia/TeamDetail").forward(request, response);
//                }
//                break;
//            }
            
            case "reply":{
                
            }

            default: {
                System.out.println(
                        "Hành động không xác định, chuyển về MyTeamList.");
                request.getRequestDispatcher(
                        "/MyTeamList").forward(request, response);

                break;
            }
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
