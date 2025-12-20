/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.UserAccount;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class UserAccountDAO extends DBContext {

    private UserAccount mappingFromResultset(ResultSet rs) throws Exception {
        UserAccount user = null;
        try {
            user = new UserAccount();
            user.setUserID(rs.getInt("UserID"));

            // NVARCHAR
            user.setUsername(rs.getString("Username"));
            user.setPassword(rs.getString("PasswordHash"));
            user.setFullName(rs.getString("FullName"));

            // NVARCHAR (NULLABLE)
            user.setEmail(rs.getString("Email"));
            user.setPhone(rs.getString("Phone"));

            // INT Foreign Key
            user.setRoleID(rs.getInt("RoleID"));

            // BIT NOT NULL (Mặc dù là Boolean Wrapper, ta dùng getBoolean)
            user.setIsActive(rs.getBoolean("IsActive"));

            // 2. Ánh xạ các trường DATETIME2 (Cần chuyển đổi qua Timestamp)
            // CreatedAt (DATETIME2 NOT NULL)
            Timestamp createdAtTs = rs.getTimestamp("CreatedAt");
            if (createdAtTs != null) {
                user.setCreatedAt(createdAtTs.toLocalDateTime());
            }

            // UpdatedAt (DATETIME2 NOT NULL)
            Timestamp updatedAtTs = rs.getTimestamp("UpdatedAt");
            if (updatedAtTs != null) {
                user.setUpdatedAt(updatedAtTs.toLocalDateTime());
            }
        } catch (Exception e) {
            throw e;
        }
        return user;
    }

    public ArrayList<UserAccount> getAllAccount() {
        ArrayList<UserAccount> list = new ArrayList<>();
        try {
            String sql = "Select * from UserAccount";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                UserAccount userAccount = new UserAccount();
//                userAccount.set
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public UserAccount getAccountByUsername(String username) {
        String sql = "Select * from UserAccount Where Username = ? ";
        UserAccount user = null;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = mappingFromResultset(rs);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return user;
    }
    
    public List<UserAccount> getAllUsersForTeam() {
        List<UserAccount> list = new ArrayList<>();
        
        String sql = "SELECT UserID, Username, FullName, Email, RoleID FROM UserAccount WHERE RoleID = 1 AND IsActive = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserAccount u = new UserAccount();
                u.setUserID(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setRoleID(rs.getInt("RoleID"));
                
                list.add(u);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;    
    }

    public int getUserIdByUsername(String username) {
        String sql = "SELECT UserID FROM UserAccount WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; 
    }

    public int countUsers(String search, String roleFilter) {
        String sql = "SELECT COUNT(*) FROM UserAccount u WHERE (u.FullName LIKE ? OR u.Email LIKE ?) ";
        if (roleFilter != null && !roleFilter.isEmpty()) {
            sql += "AND u.RoleID = ? ";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setNString(1, "%" + search + "%");
            ps.setNString(2, "%" + search + "%");
            if (roleFilter != null && !roleFilter.isEmpty()) {
                ps.setInt(3, Integer.parseInt(roleFilter));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<UserAccount> getUsers(String search, String roleFilter, int pageIndex, int pageSize) {
        List<UserAccount> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.Username, u.FullName, u.Email, u.Phone, u.RoleID, u.IsActive, r.RoleName " +
                     "FROM UserAccount u " +
                     "JOIN Role r ON u.RoleID = r.RoleID " +
                     "WHERE (u.FullName LIKE ? OR u.Email LIKE ?) " +
                     "AND u.RoleID <> 3 ";
        if (roleFilter != null && !roleFilter.isEmpty()) {
            sql += "AND u.RoleID = ? ";
        }

        sql += "ORDER BY u.UserID DESC " +
               "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setNString(1, "%" + search + "%");
            ps.setNString(2, "%" + search + "%");

            int paramIndex = 3;
            if (roleFilter != null && !roleFilter.isEmpty()) {
                ps.setInt(paramIndex++, Integer.parseInt(roleFilter));
            }

            ps.setInt(paramIndex++, (pageIndex - 1) * pageSize);
            ps.setInt(paramIndex, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserAccount user = new UserAccount();
                user.setUserID(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setRoleID(rs.getInt("RoleID"));
                user.setIsActive(rs.getBoolean("IsActive"));
                user.setRoleName(rs.getString("RoleName"));

                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateUserStatus(int userId, boolean status) {
        String sql = "UPDATE UserAccount SET IsActive = ? WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUserEmail(int userId, boolean status) {
        String sql = "UPDATE UserAccount SET Email = NULL AND IsActive = ? WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

}
