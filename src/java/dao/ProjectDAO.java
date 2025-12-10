package dao;

import dal.DBContext;
import entity.Project;
import java.security.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectDAO extends DBContext {

    public List<Project> getAllProjects() {
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

    //--------------- ADD/UPDATE -------------------
    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM Project";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Project p = new Project();
                p.setProjectID(rs.getInt("ProjectID"));
                p.setProjectCode(rs.getString("ProjectCode"));
                p.setProjectName(rs.getString("ProjectName"));
                p.setDescription(rs.getString("Description"));
                p.setIsActive(rs.getBoolean("IsActive"));
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));

                java.sql.Date start = rs.getDate("StartDate");
                p.setStartDate(start); // ts will be null safely if DB value is null

                java.sql.Date end = rs.getDate("Deadline");
                p.setStartDate(end); // ts will be null safely if DB value is null

                p.setStatus(rs.getString("Status"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<String> findProjectCodesByTeam(int teamId) {
        List<String> codes = new ArrayList<>();

        String sql = """
        SELECT p.ProjectCode
        FROM TeamProject tp
        JOIN Project p ON tp.ProjectID = p.ProjectID
        WHERE tp.TeamID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codes.add(rs.getString("ProjectCode"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codes;
    }

    public List<Project> findProjectsByTeam(int teamId) throws SQLException {
        List<Project> list = new ArrayList<>();

        String sql
                = "SELECT p.* "
                + "FROM TeamProject tp "
                + "JOIN Project p ON tp.ProjectID = p.ProjectID "
                + "WHERE tp.TeamID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Project p = new Project();
                    p.setProjectID(rs.getInt("ProjectID"));
                    p.setProjectCode(rs.getString("ProjectCode"));
                    p.setProjectName(rs.getString("ProjectName"));
                    p.setDescription(rs.getString("Description"));
                    p.setIsActive(rs.getBoolean("IsActive"));
                    // set other fields if your entity has them (Status, CreatedAt, etc.)

                    list.add(p);
                }
            }
        }
        return list;
    }
}
