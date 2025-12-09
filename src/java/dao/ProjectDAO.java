package dao;

import dal.DBContext;
import entity.Project;
import entity.Team;
import entity.UserAccount;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProjectDAO extends DBContext{

    public List<Project> getAllProjectsForTeam() {
        List<Project> list = new ArrayList<>();
        
        String sql = "SELECT * FROM Project WHERE IsActive = 1 AND Status IN ('OPEN', 'IN_PROGRESS')";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Project p = new Project();
                p.setProjectID(rs.getInt("ProjectID"));
                p.setProjectCode(rs.getString("ProjectCode"));
                p.setProjectName(rs.getString("ProjectName"));
                p.setStatus(rs.getString("Status"));
                
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<Project> searchProjects(int userId, int roleId, String keyword, String status) {
        List<Project> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ProjectID, ProjectCode, ProjectName, Description, ");
        sql.append("StartDate, Deadline, Status, IsActive ");
        sql.append("FROM Project ");
        sql.append("WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (ProjectName LIKE ? OR ProjectCode LIKE ?) ");
        }
        
        if ("Active".equalsIgnoreCase(status)) {
            sql.append("AND IsActive = 1 ");
        }
        
        sql.append("ORDER BY CreatedAt DESC");

        try (
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            
            int index = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Project p = new Project();
                
                // Mapping dữ liệu khớp với file SQL
                p.setProjectID(rs.getInt("ProjectID"));
                p.setProjectCode(rs.getString("ProjectCode"));
                p.setProjectName(rs.getString("ProjectName"));
                p.setDescription(rs.getString("Description"));
                p.setStartDate(rs.getDate("StartDate"));
                p.setDeadline(rs.getDate("Deadline"));
                p.setStatus(rs.getString("Status")); 
                p.setIsActive(rs.getBoolean("IsActive")); 
                
                list.add(p);
            }
            rs.close();
            ps.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Team> getAllTeamsForProject() {
        List<Team> list = new ArrayList<>();
        
        String sql = "SELECT TeamID, TeamName FROM Team";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Team t = new Team();
                t.setTeamID(rs.getInt("TeamID"));
                t.setTeamName(rs.getString("TeamName"));
                list.add(t);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<UserAccount> getAllActiveMembers() {
        List<UserAccount> list = new ArrayList<>();
        
        String sql = "SELECT UserID, FullName, Email FROM UserAccount WHERE IsActive = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserAccount u = new UserAccount();
                u.setUserId(rs.getInt("UserID"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                list.add(u);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addProject(Project p, int teamId, String[] memberIds) {
        boolean result = false;
        PreparedStatement psProject = null;
        PreparedStatement psTeam = null;
        PreparedStatement psMember = null;
        ResultSet rs = null;
        
        // SQL thêm Project
        String sqlProject = "INSERT INTO Project (ProjectCode, ProjectName, StartDate, Deadline, Description, Status, IsActive, CreatedAt) "
                          + "VALUES (?, ?, ?, ?, ?, ?, 1, GETDATE())";
        
        // SQL thêm TeamProject
        String sqlTeam = "INSERT INTO TeamProject (ProjectID, TeamID, AssignedAt) VALUES (?, ?, GETDATE())";

        // SQL thêm ProjectMember
        String sqlMember = "INSERT INTO ProjectMember (ProjectID, UserID, RoleInProject, JoinedAt) VALUES (?, ?, ?, GETDATE())";

        try {
            // 1. TẮT chế độ tự động lưu để bắt đầu Transaction thủ công
            connection.setAutoCommit(false);

            // --- BƯỚC 1: INSERT PROJECT ---
            // Statement.RETURN_GENERATED_KEYS: Yêu cầu trả về ID vừa sinh ra
            psProject = connection.prepareStatement(sqlProject, java.sql.Statement.RETURN_GENERATED_KEYS);
            psProject.setString(1, p.getProjectCode());
            psProject.setString(2, p.getProjectName());
            psProject.setDate(3, p.getStartDate()); // java.sql.Date
            psProject.setDate(4, p.getDeadline());  // java.sql.Date
            psProject.setString(5, p.getDescription());
            psProject.setString(6, "OPEN"); // Mặc định trạng thái OPEN

            int rows = psProject.executeUpdate();
            
            if (rows > 0) {
                rs = psProject.getGeneratedKeys();
                int newProjectId = 0;
                if (rs.next()) {
                    newProjectId = rs.getInt(1);
                }
                if (teamId > 0) {
                    psTeam = connection.prepareStatement(sqlTeam);
                    psTeam.setInt(1, newProjectId);
                    psTeam.setInt(2, teamId);
                    psTeam.executeUpdate();
                }
                if (memberIds != null && memberIds.length > 0) {
                    psMember = connection.prepareStatement(sqlMember);
                    for (String idStr : memberIds) {
                        int uid = Integer.parseInt(idStr);
                        
                        psMember.setInt(1, newProjectId);
                        psMember.setInt(2, uid);
                        psMember.setString(3, "Member");
                        psMember.addBatch();
                    }
                    psMember.executeBatch();
                }
                connection.commit();
                result = true;
            } else {
                // Nếu Insert Project thất bại -> Hoàn tác
                connection.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Dọn dẹp tài nguyên
            try {
                if (rs != null) rs.close();
                if (psProject != null) psProject.close();
                if (psTeam != null) psTeam.close();
                if (psMember != null) psMember.close();

                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
