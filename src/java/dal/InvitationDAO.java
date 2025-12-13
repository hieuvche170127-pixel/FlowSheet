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

/**
 *
 * @author Admin
 */
public class InvitationDAO extends DBContext {

    // code 3 dong
    private Invitation mapInvitationFromResultSet(ResultSet rs) {
        Invitation invitation = null;
        try {
            if (rs.next()) {
                // Khởi tạo đối tượng Invitation mới
                invitation = new Invitation();

                // 1. Ánh xạ các trường Bắt buộc (Required Fields)
                invitation.setInvitationId(rs.getInt("InvitationID"));
                invitation.setEmail(rs.getString("Email"));
                invitation.setRoleId(rs.getInt("RoleID"));
                invitation.setInvitedById(rs.getInt("InvitedByID"));

                // Ánh xạ UUID: Dùng rs.getObject(String, Class) hoặc rs.getString() và UUID.fromString()
                // Tùy thuộc vào driver SQL Server của bạn, rs.getObject() là cách hiện đại:
                invitation.setToken(UUID.fromString(rs.getString("Token")));

                invitation.setStatus(rs.getString("Status"));

                // Ánh xạ LocalDateTime: Dùng rs.getTimestamp() và toLocalDateTime()
                invitation.setExpiresAt(rs.getTimestamp("ExpiresAt").toLocalDateTime());

                // 2. Ánh xạ các trường Tùy chọn (Optional Fields - Có thể NULL)
                // Cần kiểm tra NULL để tránh lỗi NullPointerException khi lấy int từ ResultSet
                // ProjectID
                Integer projectId = rs.getInt("ProjectID");
                if (!rs.wasNull()) {
                    invitation.setProjectId(projectId);
                }

                // TeamID
                Integer teamId = rs.getInt("TeamID");
                if (!rs.wasNull()) {
                    invitation.setTeamId(teamId);
                }

                // 3. Ánh xạ các trường Hệ thống/Kiểm toán (Audit Fields)
                invitation.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());

                // AcceptedAt (Có thể NULL)
                java.sql.Timestamp acceptedTimestamp = rs.getTimestamp("AcceptedAt");
                if (acceptedTimestamp != null) {
                    invitation.setAcceptedAt(acceptedTimestamp.toLocalDateTime());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(e.getMessage());
        }
        return invitation;
    }

    // nghia - code 10 dong :)) 
    // hàm lấy tất cả các invitation 
    public ArrayList<Invitation> getAllInvitation() {
        ArrayList<Invitation> list = new ArrayList<>();
        try {
            String sql = "select * from invitation";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Invitation invitation = mapInvitationFromResultSet(rs);
                list.add(invitation);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(e.getMessage());
        }
        return list;
    }

    // có thể lấy được tất cả lời mời bằng email
    // giả sử nghia có email là nghiakhac2005@gmail.com
    // sẽ trả về list chứa các invitation đến email đấy.
    public ArrayList<Invitation> getAllInvitationByEmail(String email) {
        ArrayList<Invitation> list = new ArrayList<>();
        String query = "select * from invitation where email = ? ";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Invitation mappedInvitation = new Invitation();
                mappedInvitation = mapInvitationFromResultSet(rs);
                list.add(mappedInvitation);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    // thuần gemini, xóa vài dòng
    public boolean createInvitation(Invitation invitation) {
        // SQL Server có thể tự động điền Token (UNIQUEIDENTIFIER DEFAULT NEWID()) và CreatedAt,
        // nhưng chúng ta sẽ chèn các giá trị này để kiểm soát tốt hơn.
        String SQL_INSERT = "INSERT INTO Invitation ("
                + "Email, RoleID, Token, Status, ExpiresAt, "
                + "ProjectID, TeamID, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        boolean success = false;
        try {
            ps = connection.prepareStatement(SQL_INSERT);
            // 1. Gán giá trị cho các cột theo thứ tự của câu lệnh SQL
            int i = 1;
            // Required Fields
            ps.setString(i++, invitation.getEmail());
            ps.setInt(i++, invitation.getRoleId());
            ps.setInt(i++, invitation.getInvitedById());

            ps.setString(i++, invitation.getStatus());

            // ExpiresAt (LocalDateTime -> Timestamp)
            ps.setTimestamp(i++, Timestamp.valueOf(invitation.getExpiresAt()));

            // Optional Fields (Phải xử lý NULL)
            // ProjectID
            if (invitation.getProjectId() != null) {
                ps.setInt(i++, invitation.getProjectId());
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            // TeamID
            if (invitation.getTeamId() != null) {
                ps.setInt(i++, invitation.getTeamId());
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            // CreatedAt (LocalDateTime -> Timestamp)
            // Giả sử bạn set CreatedAt ngay trước khi gọi hàm này
            if (invitation.getCreatedAt() != null) {
                ps.setTimestamp(i++, Timestamp.valueOf(invitation.getCreatedAt()));
            } else {
                // Nếu không set, sử dụng thời điểm hiện tại của Java
                ps.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.now()));
            }

            // 2. Thực thi lệnh INSERT
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            Logger.getLogger(e.getMessage());
            // Xử lý các lỗi liên quan đến ràng buộc (Constraint Violations)
        } finally {
            // 3. Đóng tài nguyên
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                Logger.getLogger(e.getMessage());
            }
        }
        return success;
    }

}
