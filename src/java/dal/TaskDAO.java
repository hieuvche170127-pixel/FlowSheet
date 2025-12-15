package dal;

import entity.ProjectTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<ProjectTask> getTasksInProject(int projectId, int pageIndex, int pageSize) {
        List<ProjectTask> list = new ArrayList<>();
        
        String sql = "SELECT pt.TaskID, pt.TaskCode, pt.TaskName, pt.Status, "
                + "STRING_AGG(ua.Username, ', ') WITHIN GROUP (ORDER BY ua.Username) AS AssigneeNames "
                + "FROM ProjectTask pt "
                + "LEFT JOIN TaskAssignee ta ON pt.TaskID = ta.TaskID "
                + "LEFT JOIN UserAccount ua ON ta.UserID = ua.UserID "
                + "WHERE pt.ProjectID = ? "
                + "GROUP BY pt.TaskID, pt.TaskCode, pt.TaskName, pt.Status "
                + "ORDER BY pt.TaskID DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int offset = (pageIndex - 1) * pageSize;

            ps.setInt(1, projectId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProjectTask task = new ProjectTask();
                task.setTaskId(rs.getInt("TaskID"));
                task.setTaskCode(rs.getString("TaskCode"));
                task.setTaskName(rs.getString("TaskName"));
                task.setStatus(rs.getString("Status"));
                task.setAssigneeNames(rs.getString("AssigneeNames"));
//                if (rs.getDate("Deadline") != null) {
//                        task.setDeadline(rs.getDate("Deadline"));
//                    }
                list.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
