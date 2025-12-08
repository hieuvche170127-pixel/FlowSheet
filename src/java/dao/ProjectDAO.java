package dao;

import dal.DBContext;
import entity.Project;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProjectDAO extends DBContext{

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
    
}
