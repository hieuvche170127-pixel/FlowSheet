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
            Logger.getLogger(UserAccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;    
    }

    public int getUserIdByUsername(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    

}
