package dal;

import dal.DBContext;
import entity.UserAccount;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserDAO extends DBContext{

    public List<UserAccount> getAllUsersForTeam() {
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
    
}
