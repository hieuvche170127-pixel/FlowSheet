package dao;

import dal.DBContext;
import entity.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DBContext {

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();

        String sql = "SELECT * FROM UserAccount WHERE RoleID = 1 AND IsActive = 1";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User u = new User();
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

    public User login(String username, String password) {
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
                User u = new User();
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

    public boolean createUser(User user) {
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
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM UserAccount";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
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
    public List<User> findMembersByTeam(int teamId) {
        List<User> list = new ArrayList<>();

        String sql
                = "SELECT ua.* "
                + "FROM TeamMember tm "
                + "JOIN UserAccount ua ON tm.UserID = ua.UserID "
                + "JOIN [Role] r ON ua.RoleID = r.RoleID "
                + "WHERE tm.TeamID = ? "
                + "  AND r.RoleName IN ('Student', 'Supervisor')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserID(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setFullName(rs.getString("FullName"));
                    u.setEmail(rs.getString("Email"));
                    u.setRoleID(rs.getInt("RoleID"));
                    // again, set more fields if your entity.User defines them

                    list.add(u);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM UserAccount WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
