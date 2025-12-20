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
                   + "pt.EstimateHourToDo, pt.CreatedAt, pt.ProjectID, "
                   + "STRING_AGG(u.FullName, ', ') WITHIN GROUP (ORDER BY u.FullName) AS AssigneeNames "
                   + "FROM ProjectTask pt "
                   + "LEFT JOIN TaskAssignee ta ON pt.TaskID = ta.TaskID "
                   + "LEFT JOIN UserAccount u ON ta.UserID = u.UserID "
                   + "WHERE pt.ProjectID = ? "
                   + "GROUP BY pt.TaskID, pt.TaskName, pt.Status, pt.Deadline, pt.Description, "
                   + "pt.EstimateHourToDo, pt.CreatedAt, pt.ProjectID "
                   + "ORDER BY pt.CreatedAt DESC "
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
                    task.setProjectId(rs.getInt("ProjectID"));
                    task.setTaskName(rs.getString("TaskName"));
                    task.setStatus(rs.getString("Status"));
                    task.setDeadline(rs.getTimestamp("Deadline"));
                    task.setDescription(rs.getString("Description"));
                    task.setEstimateHourToDo(rs.getObject("EstimateHourToDo", Double.class));
                    task.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    task.setAssigneeNames(rs.getString("AssigneeNames")); 
                    
                    list.add(task);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving tasks in project", ex);
        }
        return list;
    }
}
