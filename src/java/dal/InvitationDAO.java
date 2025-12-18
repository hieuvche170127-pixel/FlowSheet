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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import utilities.DateTimeConverter;
import java.sql.Types;
import java.util.List;
import java.util.Arrays;

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
                    + "SET Email = ?, "
                    + "    RoleID = ?, "
                    + "    ExpiresAt = ?, "
                    + "    status = ? " // 
                    + "WHERE invitationid = ?"; // Đây là cái WHERE để chọn thằng cần update
            ps = connection.prepareStatement(sql);
            // 3. Set giá trị cho các dấu chấm hỏi (?)
            // ? thứ 1: Email
            ps.setString(1, updatedInvitation.getEmail());
            // ? thứ 2: Role ID (int)
            ps.setInt(2, updatedInvitation.getRoleId());
            ps.setTimestamp(3, Timestamp.valueOf(updatedInvitation.getExpiresAt()));
            ps.setString(4, updatedInvitation.getStatus());
            ps.setInt(5, updatedInvitation.getInvitationId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result = true;
            }
        } catch (SQLException e) {
            Logger.getLogger("đã có lỗi ở inviDAO");
        }
        return result;

    }

    /**
     * Kiểm tra xem đã có lời mời nào đang CHỜ (Pending) và CÒN HẠN gửi đến
     * email này chưa. Logic: Trùng Team, Trùng người mời, Trùng Email nhận,
     * Status = PENDING, ExpiresAt > Hiện tại.
     */
    public boolean hasPendingTeamInvitation(int teamId, int invitedById, String email, int roleID) {
        boolean exists = false;

        // Dùng SYSDATETIME() của SQL Server để so sánh thời gian chính xác
        String sql = "SELECT COUNT(*) FROM Invitation "
                + "WHERE TeamID = ? "
                + "AND InvitedByID = ? "
                + "AND Email = ? "
                + "AND Status = 'PENDING' "
                + "AND AcceptedAt IS NULL "
                + "AND Roleid = ? "
                + "AND ExpiresAt > SYSDATETIME()"; // Chỉ lấy cái chưa hết hạn

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.setInt(2, invitedById);
            ps.setString(3, email);
            ps.setInt(4, roleID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu đếm được > 0 tức là đã có lời mời
                    int count = rs.getInt(1);
                    exists = count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    // muốn lấy invitedBy, expiredAt, status, acceptedAt,teamId
    // lấy thêm cái team để tìm xem thk mới có trong team hay chưa. 
    public Invitation getInvitationByInvitationId(int invitationId) {
        Invitation invitation = null;

        // Em chọn các cột cần thiết như yêu cầu
        String sql = "SELECT InvitationID, InvitedByID, ExpiresAt, Status, AcceptedAt, TeamID "
                + "FROM Invitation "
                + "WHERE InvitationID = ?";

        // Phần try-catch kết nối DB (giả sử anh dùng DBContext hoặc tương tự)
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, invitationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invitation = new Invitation();
                    // Map dữ liệu từ DB vào Object
                    invitation.setInvitationId(rs.getInt("InvitationID"));
                    invitation.setInvitationId(rs.getInt("InvitedByID"));
                    invitation.setStatus(rs.getString("Status"));
                    invitation.setTeamId(rs.getInt("TeamId"));
                    // Lấy kiểu Timestamp cho DateTime
                    invitation.setExpiresAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(rs.getTimestamp("ExpiresAt")));
                    invitation.setAcceptedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(rs.getTimestamp("AcceptedAt")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invitation;
    }

    /**
     * Xóa hoàn toàn một bản ghi lời mời (Invitation) khỏi cơ sở dữ liệu.
     * <p>
     * Hàm này thực hiện câu lệnh DELETE cứng. Dữ liệu sẽ mất vĩnh viễn.
     * </p>
     *
     * @param invitationId ID của lời mời cần xóa (Primary Key).
     * @return <code>true</code> nếu xóa thành công (có ít nhất 1 dòng bị ảnh
     * hưởng), <code>false</code> nếu thất bại hoặc ID không tồn tại.
     */
    public boolean deleteInvitation(int invitationId) {
        int rowAffected = 0;
        String sql = "DELETE FROM Invitation WHERE InvitationID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Giả sử class này đã kế thừa hoặc có biến 'connection' từ DBContext
            // Nếu chưa có, bạn cần: Connection conn = new DBContext().getConnection();
            ps.setInt(1, invitationId);
            rowAffected = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Nên dùng Logger để ghi lại lỗi thay vì print
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return rowAffected > 0;
    }

    /**
     * Cập nhật trạng thái (Status) của một lời mời cụ thể.
     * <p>
     * Các trạng thái hợp lệ thường là: "PENDING", "ACCEPTED", "CANCELLED",
     * "EXPIRED". Hàm sẽ tự động chuyển status về chữ in hoa để khớp với
     * Database Constraint.
     * </p>
     *
     * @param invitationId ID của lời mời (Primary Key) cần sửa.
     * @param newStatus Trạng thái mới muốn cập nhật.
     * @return {@code true} nếu cập nhật thành công, {@code false} nếu thất bại
     * hoặc ID không tồn tại.
     */
    /**
     * Cập nhật Status của Invitation với validate chặt chẽ.
     *
     * @param invitationId ID của lời mời.
     * @param newStatus Trạng thái mới (PENDING, ACCEPTED, EXPIRED, CANCELLED).
     * @return true nếu update thành công, false nếu lỗi SQL/Database.
     * @throws IllegalArgumentException Nếu status truyền vào không hợp lệ (null
     * hoặc sai chính tả).
     */
    public boolean editStatus(int invitationId, String newStatus) {
        // 1. Validate Input cơ bản
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Status không được null hoặc rỗng.");
        }

        // 2. Chuẩn hóa về chữ in hoa để so sánh
        String statusNormalized = newStatus.trim().toUpperCase();

        // 3. Danh sách các Status hợp lệ trong Database
        List<String> validStatuses = Arrays.asList("PENDING", "ACCEPTED", "REJECT", "CANCELLED");

        // 4. Validate logic: Nếu không nằm trong list trên -> Bắn Exception
        if (!validStatuses.contains(statusNormalized)) {
            throw new IllegalArgumentException("Status không hợp lệ: '" + newStatus + "'. "
                    + "Các giá trị cho phép: " + validStatuses.toString());
        }

        // 5. Thực thi câu lệnh SQL
        String sql = "UPDATE Invitation SET Status = ? WHERE InvitationID = ?";
        int rowAffected = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, statusNormalized);
            ps.setInt(2, invitationId);

            rowAffected = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi SQL
            return false;        // Lỗi DB thì trả về false
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return rowAffected > 0;
    }
}
