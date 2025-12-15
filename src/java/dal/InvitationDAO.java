/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.Invitation;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import utilities.DateTimeConverter;
import java.sql.Types;

/**
 *
 * @author Admin
 */
public class InvitationDAO extends DBContext {

    //Read
    // có thể lấy được tất cả lời mời bằng email
    // giả sử nghia có email là nghiakhac2005@gmail.com
    // sẽ trả về list chứa các invitation đến email đấy kể cả đã và chưa trả lời nhé.
    // trả về này:
    // invitationid, roleId, invitedByid, status, expiredAt, CreatedAt AceeptedAt, 
    // ProjectId nếu là Pr33oject,  Teamid nếu là lời mời team.
    public ArrayList<Invitation> getAllInvitationByEmail(String email) {
        ArrayList<Invitation> list = new ArrayList<>();
        String query = "select * from invitation where email = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            while (rs.next()) {
                Invitation invitation = new Invitation();

                invitation.setInvitationId(rs.getInt("InvitationID"));
                invitation.setRoleId(rs.getInt("RoleID"));
                invitation.setInvitedById(rs.getInt("InvitedByID"));
                invitation.setStatus(rs.getString("Status"));

                int projectId = rs.getInt("ProjectID");
                if (!rs.wasNull()) {
                    invitation.setProjectId(projectId);
                }
                int teamId = rs.getInt("TeamID");
                if (!rs.wasNull()) {
                    invitation.setTeamId(teamId);
                }
                invitation.setExpiresAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(
                        rs.getTimestamp("ExpiresAt")
                ));

                invitation.setCreatedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(
                        rs.getTimestamp("CreatedAt")
                ));

                invitation.setAcceptedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(
                        rs.getTimestamp("AcceptedAt")
                ));
                list.add(invitation);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    // hàm này để lấy các invitation được gửi bởi một team (invitation sent by team)
    // trả về này:
    // invitationid, email, roleId, invitedByid, status, expiredAt, CreatedAt AceeptedAt, 
    // do team biết rồi nên ko cần, là invi Team thì ko thể là Project, ko cần nốt, 
    public ArrayList<Invitation> getAllInvitationSentByTeamId(int teamId) {
        ArrayList<Invitation> list = new ArrayList<>();
        String query = "select * from invitation where teamId = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, teamId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Invitation invitation = new Invitation();

                invitation.setInvitationId(rs.getInt("InvitationID"));
                invitation.setEmail(rs.getString("Email"));
                invitation.setRoleId(rs.getInt("RoleID"));
                invitation.setInvitedById(rs.getInt("InvitedByID"));
                invitation.setStatus(rs.getString("Status"));

                invitation.setExpiresAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(
                        rs.getTimestamp("ExpiresAt")
                ));

                invitation.setCreatedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(
                        rs.getTimestamp("CreatedAt")
                ));

                invitation.setAcceptedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(
                        rs.getTimestamp("AcceptedAt")
                ));
                list.add(invitation);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public boolean addInvitation(Invitation invitation) {
        String sql = "INSERT INTO Invitation "
                + "(Email, RoleID, InvitedByID, TeamID, ProjectID, ExpiresAt, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // 1. Email
            ps.setString(1, invitation.getEmail());

            // 2. RoleID
            ps.setInt(2, invitation.getRoleId());

            // 3. InvitedByID
            ps.setInt(3, invitation.getInvitedById());

            // 4. TeamID (Xử lý Null)
            if (invitation.getTeamId() != null) {
                ps.setInt(4, invitation.getTeamId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            // 5. ProjectID (Xử lý Null)
            if (invitation.getProjectId() != null) {
                ps.setInt(5, invitation.getProjectId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            // 6. ExpiresAt
            if (invitation.getExpiresAt() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(invitation.getExpiresAt()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            // 7. CreatedAt
            if (invitation.getCreatedAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(invitation.getCreatedAt()));
            } else {
                ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            }

            // Thực thi
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editInvitation(Invitation updatedInvitation) {
        boolean result = false;
        PreparedStatement ps = null;
        try {

            // 2. Câu lệnh SQL
            // Lưu ý: Em giả định tên bảng là 'invitation' và tên cột trong DB như bên dưới.
            // Anh nhớ check lại tên cột trong SQL Server/MySQL của anh nhé.
            String sql = "UPDATE invitation "
                    + "SET email = ?, "
                    + "    role_id = ?, "
                    + "    expires_at = ?, "
                    + "    created_at = ? " // Đây là chỗ update cái biến localCreatedAt anh muốn
                    + "WHERE invitationid = ?"; // Đây là cái WHERE để chọn thằng cần update

            ps = connection.prepareStatement(sql);

            // 3. Set giá trị cho các dấu chấm hỏi (?)
            // ? thứ 1: Email
            ps.setString(1, updatedInvitation.getEmail());

            // ? thứ 2: Role ID (int)
            ps.setInt(2, updatedInvitation.getRoleId());

            // ? thứ 3: Expires At (chuyển LocalDateTime -> Timestamp)
            ps.setTimestamp(3, Timestamp.valueOf(updatedInvitation.getExpiresAt()));

            // ? thứ 4: Created At (Update thời gian hiện tại như anh yêu cầu)
            // Nếu trong object anh chưa set, em lấy luôn giờ hiện tại ở đây
            // Nhưng tốt nhất là anh set vào object trước khi truyền vào hàm này.
            // Ở đây em lấy từ object ra nhé:
            if (updatedInvitation.getCreatedAt() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(updatedInvitation.getCreatedAt()));
            } else {
                // Phòng trường hợp null thì lấy giờ hiện tại
                // okeee cảm ơn em
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            // ? thứ 5: Invitation ID (Cái điều kiện WHERE)
            ps.setInt(5, updatedInvitation.getInvitationId());

            // 4. Thực thi
            int rowsAffected = ps.executeUpdate();

            // Nếu số dòng bị ảnh hưởng > 0 tức là update thành công
            if (rowsAffected > 0) {
                result = true;
            }

        } catch (SQLException e) {
            Logger.getLogger("đã có lỗi ở inviDAO");
        } 
        return result;

    }
}
