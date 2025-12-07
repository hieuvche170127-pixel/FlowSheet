package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import entity.Team;
import entity.TeamMember;
import entity.TeamProject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TeamDAO extends DBContext {

    public boolean createTeamTransaction(Team team, List<TeamMember> members, List<TeamProject> projects) {
        String sqlTeam = "INSERT INTO Team (TeamName, Description, CreatedBy) VALUES (?, ?, ?)";
        String sqlMember = "INSERT INTO TeamMember (TeamID, UserID, RoleInTeam) VALUES (?, ?, ?)";
        String sqlProject = "INSERT INTO TeamProject (TeamID, ProjectID) VALUES (?, ?)";
    
        try {
            connection.setAutoCommit(false);
            PreparedStatement psTeam = connection.prepareStatement(sqlTeam, Statement.RETURN_GENERATED_KEYS);
            psTeam.setString(1, team.getTeamName());
            psTeam.setString(2, team.getDescription());
            psTeam.setInt(3, team.getCreatedBy());
            
            if (psTeam.executeUpdate() == 0) {
                throw new SQLException("Creating team failed.");
            }
            int newTeamId = 0;
            try (ResultSet generatedKeys = psTeam.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newTeamId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating team failed, no ID obtained.");
                }
            }
            
            PreparedStatement psMember = connection.prepareStatement(sqlMember);
            for (TeamMember mem : members) {
                psMember.setInt(1, newTeamId);
                psMember.setInt(2, mem.getUserId());
                psMember.setString(3, mem.getRole());
                psMember.addBatch();
            }
            psMember.executeBatch();
            
            if (projects != null && !projects.isEmpty()) {
                PreparedStatement psProject = connection.prepareStatement(sqlProject);
                for (TeamProject tp : projects) {
                    psProject.setInt(1, newTeamId);
                    psProject.setInt(2, tp.getProjectId());
                    psProject.addBatch();
                }
                psProject.executeBatch();
            }
            
            connection.commit();
            return true;
        }catch (SQLException e) {
            // 3. Nếu có lỗi -> Rollback
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            // 4. Trả lại trạng thái AutoCommit ban đầu (Quan trọng với DBContext kiểu này)
            try {
                if (connection != null) connection.setAutoCommit(true);
                // Với kiểu DBContext này, ta thường KHÔNG đóng connection ở đây
                // vì có thể bạn muốn dùng tiếp DAO instance này.
                // Connection sẽ đóng khi Garbage Collector dọn dẹp hoặc bạn tự gọi close.
            } catch (SQLException ex) {
                Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
    }   
}
