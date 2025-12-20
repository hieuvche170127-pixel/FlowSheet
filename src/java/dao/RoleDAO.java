package dao;

import dal.DBContext; // <-- use the GROUP context
import entity.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO extends DBContext { // extend the shared DBContext

    public List<Role> findAll() throws SQLException {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM [Role]";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql); // use inherited 'connection'
            rs = ps.executeQuery();

            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleCode(rs.getString("RoleCode"));
                r.setRoleName(rs.getString("RoleName"));
                list.add(r);
            }

        } finally {
            // Close only PS and RS — NOT the connection (shared by DBContext)
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }

        return list;
    }

    public List<Role> findTeamRoles() throws SQLException {
        List<Role> list = new ArrayList<>();
        String sql
                = "SELECT RoleID, RoleCode, RoleName FROM [Role] "
                + "WHERE RoleCode IN ('TEAM_MEMBER','TEAM_LEADER') "
                + "ORDER BY RoleID";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql); // use inherited 'connection'
            rs = ps.executeQuery();

            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleCode(rs.getString("RoleCode"));
                r.setRoleName(rs.getString("RoleName"));
                list.add(r);
            }
        } finally {
            // Close only PS and RS — NOT the connection (shared by DBContext)
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }

        }
        return list;

    }
}
