/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.ProjectTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class ProjectTaskDAO extends DBContext {

    /**
     * Map dữ liệu từ ResultSet sang đối tượng ProjectTask
     * 
     * @param rs ResultSet từ query
     * @return ProjectTask object đã được map đầy đủ dữ liệu
     * @throws SQLException
     */
    private ProjectTask mapProjectTaskFromResultSet(ResultSet rs) throws SQLException {
        ProjectTask task = new ProjectTask();
        
        task.setTaskId(rs.getInt("TaskID"));
        
        // ProjectID có thể NULL
        Integer projectId = rs.getObject("ProjectID", Integer.class);
        task.setProjectId(projectId);
        
        task.setTaskName(rs.getString("TaskName"));
        task.setDescription(rs.getString("Description"));
        
        // Deadline có thể NULL
        task.setDeadline(rs.getTimestamp("Deadline"));
        
        // EstimateHourToDo có thể NULL (DECIMAL trong DB)
        Double estimateHours = rs.getObject("EstimateHourToDo", Double.class);
        task.setEstimateHourToDo(estimateHours);
        
        task.setCreatedAt(rs.getTimestamp("CreatedAt"));
        task.setStatus(rs.getString("Status"));
        
        return task;
    }

    /**
     * Lấy tất cả ProjectTask từ database
     * 
     * @return Danh sách tất cả ProjectTask
     */
    public ArrayList<ProjectTask> getAllProjectTask() {
        ArrayList<ProjectTask> list = new ArrayList<>();
        String sql = "SELECT [TaskID], [ProjectID], [TaskName], [Description], [Deadline], "
                + "[EstimateHourToDo], [CreatedAt], [Status] "
                + "FROM [ProjectTask] "
                + "ORDER BY [TaskID] DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ProjectTask task = mapProjectTaskFromResultSet(rs);
                list.add(task);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectTaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving all project tasks", ex);
        }
        
        return list;
    }

    /**
     * Lấy tất cả ProjectTask theo ProjectID
     * 
     * @param projectId ID của project
     * @return Danh sách ProjectTask thuộc project đó
     */
    public ArrayList<ProjectTask> getAllTaskByProjectId(int projectId) {
        ArrayList<ProjectTask> list = new ArrayList<>();
        String sql = "SELECT [TaskID], [ProjectID], [TaskName], [Description], [Deadline], "
                + "[EstimateHourToDo], [CreatedAt], [Status] "
                + "FROM [ProjectTask] "
                + "WHERE [ProjectID] = ? "
                + "ORDER BY [TaskID] DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProjectTask task = mapProjectTaskFromResultSet(rs);
                    list.add(task);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectTaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving tasks by project ID", ex);
        }
        
        return list;
    }
}
