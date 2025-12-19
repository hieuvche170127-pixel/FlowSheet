package dal;

import entity.ProjectTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskDAO extends DBContext {

    public int countTasksByProject(int projectId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM ProjectTask WHERE ProjectID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error counting tasks by project", ex);
        }
        return count;
    }

    public List<ProjectTask> getTasksInProject(int projectId, int pageIndex, int pageSize) {
        List<ProjectTask> list = new ArrayList<>();
        
        String sql = "SELECT pt.TaskID, pt.TaskName, pt.Status, pt.Deadline, pt.Description, "
                + "pt.EstimateHourToDo, pt.CreatedAt, pt.ProjectID "
                + "FROM ProjectTask pt "
                + "WHERE pt.ProjectID = ? "
                + "ORDER BY pt.TaskID DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int offset = (pageIndex - 1) * pageSize;

            ps.setInt(1, projectId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProjectTask task = new ProjectTask();
                    task.setTaskId(rs.getInt("TaskID"));
                    task.setTaskName(rs.getString("TaskName"));
                    task.setStatus(rs.getString("Status"));
                    
                    java.sql.Timestamp deadline = rs.getTimestamp("Deadline");
                    task.setDeadline(deadline);
                    
                    task.setDescription(rs.getString("Description"));
                    
                    Double estimateHours = rs.getObject("EstimateHourToDo", Double.class);
                    task.setEstimateHourToDo(estimateHours);
                    
                    java.sql.Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    task.setCreatedAt(createdAt);
                    
                    Integer projectIdFromDb = rs.getObject("ProjectID", Integer.class);
                    task.setProjectId(projectIdFromDb);
                    
                    list.add(task);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving tasks in project", ex);
        }
        return list;
    }
}
