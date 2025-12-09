/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.Project;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

/**
 *
 * @author Admin
 */
public class ProjectDAO extends DBContext {

    /**
     * Map dữ liệu từ ResultSet sang Project object
     *
     * @param rs - ResultSet từ query
     * @return Project object đã được map đầy đủ dữ liệu
     * @throws Exception
     */
    private Project mapProjectFromResultSet(ResultSet rs) throws Exception {
        Project project = null;
        try {
            project = new Project();

            // 1. Primary Key
            project.setProjectID(rs.getInt("projectID"));

            // 2. Basic Information
            project.setProjectCode(rs.getString("projectCode"));
            project.setProjectName(rs.getString("projectName"));
            project.setDescription(rs.getString("description"));

            // 3. Boolean Field
            project.setIsActive(rs.getBoolean("isActive"));

            // 4. Date Fields
            // startDate và deadline là java.sql.Date -> convert sang LocalDate nếu cần
            project.setStartDate(rs.getDate("startDate"));
            project.setDeadline(rs.getDate("deadline"));

            // 5. Timestamp Field
            project.setCreatedAt(rs.getTimestamp("createdAt"));

            // 6. Status Field
            project.setStatus(rs.getString("status"));

        } catch (Exception e) {
            throw e;
        }
        return project;
    }

    public ArrayList<Project> getAllProject() {
        ArrayList<Project> list = new ArrayList<>();
        try {
            String sql = "select * from project";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Project p = mapProjectFromResultSet(rs);
                list.add(p);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ArrayList<Project> getALlProjectUserWithIdInvolve(int userId) {
        ArrayList<Project> list = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT p.*\n"
                    + "FROM Project p\n"
                    + "INNER JOIN TeamProject tp ON p.ProjectID = tp.ProjectID\n"
                    + "INNER JOIN Team t ON tp.TeamID = t.TeamID\n"
                    + "INNER JOIN TeamMember tm ON t.TeamID = tm.TeamID\n"
                    + "WHERE tm.UserID = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Project p = mapProjectFromResultSet(rs);
                list.add(p);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
