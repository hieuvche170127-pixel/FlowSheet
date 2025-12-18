/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class TeamProjectDAO extends DBContext {
    
    public void deleteByTeam(int teamId) throws SQLException {
        String sql = "DELETE FROM TeamProject WHERE TeamID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.executeUpdate();
        }
    }
}
