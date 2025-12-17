package dao;

import dal.DBContext;
import entity.User;
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
                u.setUserId(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setRoleId(rs.getInt("RoleID"));

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
                u.setUserId(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setRoleId(rs.getInt("RoleID"));
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
            ps.setString(2, user.getPasswordHash()); // plain text – matches current seed data
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setInt(5, user.getRoleId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
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
                u.setUserId(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));          // DB column: Username
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setRoleId(rs.getInt("RoleID"));
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
    public List<User> findMembersByTeam(int teamId) {
        List<User> list = new ArrayList<>();

        String sql
                = "SELECT ua.UserID, ua.Username, ua.FullName, ua.Email, "
                + "       tm.RoleID AS TeamRoleID, r.RoleName AS TeamRoleName "
                + "FROM TeamMember tm "
                + "JOIN UserAccount ua ON tm.UserID = ua.UserID "
                + "JOIN [Role] r ON tm.RoleID = r.RoleID "
                + "WHERE tm.TeamID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setFullName(rs.getString("FullName"));
                    u.setEmail(rs.getString("Email"));

                    // IMPORTANT: use TeamMember.RoleID, not UserAccount.RoleID
                    u.setRoleId(rs.getInt("TeamRoleID"));

                    // if your User entity has extra field, set team role name too
                    // u.setRoleName(rs.getString("TeamRoleName"));
                    list.add(u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    // Update user profile (full name and email)
    public boolean updateUserProfile(User user) {
        String sql = """
            UPDATE UserAccount 
            SET FullName = ?, Email = ?
            WHERE UserID = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getUserId());
            return ps.executeUpdate() > 0;
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

    // Tìm thành viên theo team + keyword (username hoặc full name)
    public List<User> findMembersByTeamAndKeyword(int teamId, String keyword) {
        List<User> list = new ArrayList<>();

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
                    User u = new User();
                    u.setUserId(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setFullName(rs.getString("FullName"));
                    u.setEmail(rs.getString("Email"));

                    // ✅ Team role (4/5), not lab role (1/2/3)
                    u.setRoleId(rs.getInt("TeamRoleID"));
                    list.add(u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

}
