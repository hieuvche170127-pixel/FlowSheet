package dao;

import dal.DBContext;
import entity.UserAccount;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DBContext {

    public List<UserAccount> getAllUsers() {
        List<UserAccount> list = new ArrayList<>();

        String sql = "SELECT * FROM UserAccount WHERE RoleID = 1 AND IsActive = 1";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserAccount u = new UserAccount();
                u.setUserID(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setRoleID(rs.getInt("RoleID"));

                list.add(u);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public UserAccount login(String username, String password) {
        String sql = """
            SELECT u.UserID, u.Username, u.FullName, u.Email, u.RoleID, r.RoleCode 
            FROM UserAccount u 
            JOIN Role r ON u.RoleID = r.RoleID 
            WHERE u.Username = ? AND u.PasswordHash = ? AND u.IsActive = 1
            """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);  // Plain-text comparison (matches current seed data)

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserAccount u = new UserAccount();
                u.setUserID(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setRoleID(rs.getInt("RoleID"));
                return u;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM UserAccount WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean createUser(UserAccount user) {
        String sql = """
            INSERT INTO UserAccount (Username, PasswordHash, FullName, Email, RoleID)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // plain text – matches current seed data
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setInt(5, user.getRoleID());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserID(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //---------------------- ADD/UPDATE -----------------------------
    public List<UserAccount> findAll() {
        List<UserAccount> list = new ArrayList<>();
        String sql = "SELECT * FROM UserAccount";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserAccount u = new UserAccount();
                u.setUserID(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));          // DB column: Username
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setRoleID(rs.getInt("RoleID"));
                // if entity.User has phone / isActive / createdAt / updatedAt, set them here too

                list.add(u);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Your old findMembersByTeam(int teamId), now using entity.User. Returns
     * all members (Student + Supervisor) of a given team.
     */
    public List<UserAccount> findMembersByTeam(int teamId) {
        List<UserAccount> list = new ArrayList<>();

        String sql
                = "SELECT ua.UserID, ua.Username, ua.FullName, ua.Email, "
                + "       tm.RoleID AS TeamRoleID "
                + "FROM TeamMember tm "
                + "JOIN UserAccount ua ON tm.UserID = ua.UserID "
                + "WHERE tm.TeamID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserAccount u = new UserAccount();
                    u.setUserID(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setFullName(rs.getString("FullName"));
                    u.setEmail(rs.getString("Email"));

                    // ✅ this must be 4/5
                    u.setRoleID(rs.getInt("TeamRoleID"));
                    list.add(u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    // Tìm thành viên theo team + keyword (username hoặc full name)
    public List<UserAccount> findMembersByTeamAndKeyword(int teamId, String keyword) {
        List<UserAccount> list = new ArrayList<>();

        String sql
                = "SELECT ua.UserID, ua.Username, ua.FullName, ua.Email, "
                + "       tm.RoleID AS TeamRoleID "
                + "FROM TeamMember tm "
                + "JOIN UserAccount ua ON tm.UserID = ua.UserID "
                + "WHERE tm.TeamID = ? "
                + "  AND (ua.Username LIKE ? OR ua.FullName LIKE ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            String pattern = "%" + keyword + "%";
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserAccount u = new UserAccount();
                    u.setUserID(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setFullName(rs.getString("FullName"));
                    u.setEmail(rs.getString("Email"));

                    // ✅ Team role (4/5), not lab role (1/2/3)
                    u.setRoleID(rs.getInt("TeamRoleID"));
                    list.add(u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public int getAccountRoleId(int userId) throws SQLException {
        String sql = "SELECT RoleID FROM UserAccount WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("RoleID") : -1; // -1 means not found
            }
        }
    }

    //----------------------  UPDATE  ----------------------------------------------------//
    // Check if username exists for another user (excluding current user)
    public boolean isUsernameExistsForOtherUser(String username, int currentUserId) {
        String sql = "SELECT COUNT(*) FROM UserAccount WHERE Username = ? AND UserID != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Update user profile (username, full name and email)
    public boolean updateUserProfile(UserAccount user) {
        String sql = """
            UPDATE UserAccount 
            SET Username = ?, FullName = ?, Email = ?
            WHERE UserID = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setInt(4, user.getUserID());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Verify current password (plain-text for lab consistency)
    public boolean verifyPassword(int userId, String password) {
        String sql = """
            SELECT COUNT(*) FROM UserAccount 
            WHERE UserID = ? AND PasswordHash = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Change password (plain-text for lab consistency)
    public boolean changePassword(int userId, String newPassword) {
        String sql = """
            UPDATE UserAccount 
            SET PasswordHash = ?
            WHERE UserID = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Lấy UserID dựa trên email.
     *
     * @param email Email cần tìm.
     * @return UserID nếu tìm thấy, hoặc -1 nếu không tìm thấy.
     */
    public int getUserIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            // throw new IllegalArgumentException("Email must not be null or empty"); 
            // Hoặc đơn giản là trả về -1 luôn nếu input lỗi, tùy logic
            return -1;
        }

        int userId = -1; // Mặc định là -1 (không tìm thấy)
        String sql = "SELECT UserID FROM UserAccount WHERE Email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("UserID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nhớ đổi thành Logger nếu có thể
        }

        return userId;
    }
    
    /**
     * Lấy RoleID của user dựa trên Email.
     * Dùng để check quyền (ví dụ: Admin, Manager, Member...).
     * 
     * @param email Email cần tìm.
     * @return Trả về RoleID nếu tìm thấy. Trả về -1 nếu email không tồn tại.
     */
    public int getRoleIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            return -1;
        }

        String sql = "SELECT RoleID FROM UserAccount WHERE Email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoleID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy
    }
    
    
    
}
