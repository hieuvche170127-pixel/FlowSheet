/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.Role;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class RoleDAO extends DBContext {

    /**
     * Lấy toàn bộ role (id, name, code)
     *
     * * @return ArrayList<Role> chứa các đối tượng role 
     */
    public ArrayList<Role> getAllRoleData() {
        ArrayList<Role> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Role";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);

            //lắp ráp
            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("RoleId"));
                r.setRoleCode(rs.getString("RoleCode"));
                r.setRoleName(rs.getString("RoleName"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }

}
