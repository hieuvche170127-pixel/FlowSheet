package dao;

import dal.DBContext;
import dal.TaskReportDAO;
import entity.ProjectTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskDAO extends DBContext{

    public List<ProjectTask> getAllTasksByUserId(int userId) {
        List<ProjectTask> list = new ArrayList<>();
        
        if (connection == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return list;
        }
        
        String sql = """
            SELECT T.TaskID, T.TaskName, T.Status, T.Deadline, T.EstimateHourToDo,
                   P.ProjectName, P.ProjectID
            FROM ProjectTask T
            JOIN TaskAssignee TA ON T.TaskID = TA.TaskID
            LEFT JOIN Project P ON T.ProjectID = P.ProjectID
            WHERE TA.UserID = ? 
            ORDER BY P.ProjectName, T.TaskName
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProjectTask t = new ProjectTask();
                    t.setTaskId(rs.getInt("TaskID"));
                    t.setTaskName(rs.getString("TaskName"));
                    t.setStatus(rs.getString("Status"));
                    t.setDeadline(rs.getTimestamp("Deadline"));
                    Double estimateHours = rs.getObject("EstimateHourToDo", Double.class);
                    t.setEstimateHourToDo(estimateHours);
                    Integer projectId = rs.getObject("ProjectID", Integer.class);
                    t.setProjectId(projectId);
                    t.setProjectName(rs.getString("ProjectName"));
                    
                    list.add(t);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

        public List<ProjectTask> getAllTasks() {
            return getTasksWithFilter(null, null);
        }
        
        public List<ProjectTask> getTasksWithFilter(Integer projectIdFilter, String search) {
            List<ProjectTask> list = new ArrayList<>();
            if (connection == null) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
                return list;
            }
            
            StringBuilder sql = new StringBuilder(
                "SELECT pt.TaskID, pt.ProjectID, pt.TaskName, pt.Description, " +
                "pt.Deadline, pt.EstimateHourToDo, pt.CreatedAt, pt.Status, p.ProjectName " +
                "FROM ProjectTask pt " +
                "LEFT JOIN Project p ON pt.ProjectID = p.ProjectID"
            );
            
            List<String> conditions = new ArrayList<>();
            List<Object> parameters = new ArrayList<>();

            if (projectIdFilter != null) {
                conditions.add("pt.ProjectID = ?");
                parameters.add(projectIdFilter);
            }
            
            if (search != null && !search.trim().isEmpty()) {
                String searchValue = search.trim();
                // Search in TaskID (as string), TaskName, and ProjectName with OR logic
                // Convert TaskID to string for partial matching (e.g., "1" matches 1, 10, 11, etc.)
                conditions.add("(CAST(pt.TaskID AS NVARCHAR) LIKE ? OR pt.TaskName LIKE ? OR p.ProjectName LIKE ?)");
                String likePattern = "%" + searchValue + "%";
                parameters.add(likePattern); // TaskID as string
                parameters.add(likePattern); // TaskName
                parameters.add(likePattern); // ProjectName
            }
            
            if (!conditions.isEmpty()) {
                sql.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    ps.setObject(i + 1, parameters.get(i));
                }
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ProjectTask t = new ProjectTask();
                    t.setTaskId(rs.getInt("TaskID"));
                    Integer projectId = rs.getObject("ProjectID", Integer.class);
                    t.setProjectId(projectId);
                    t.setProjectName(rs.getString("ProjectName"));
                    t.setTaskName(rs.getString("TaskName"));
                    t.setDescription(rs.getString("Description"));
                    t.setDeadline(rs.getTimestamp("Deadline"));
                    Double estimateHours = rs.getObject("EstimateHourToDo", Double.class);
                    t.setEstimateHourToDo(estimateHours);
                    t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    t.setStatus(rs.getString("Status"));
                    // Note: ProjectName is not a field in ProjectTask entity - it's only used in SQL joins for display
                    list.add(t);
                }
            } catch (SQLException ex) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving tasks", ex);
            }
            return list;
        }

    /**
     * Delete a task. This method will fail if the task has any task reports.
     * @param taskId The task ID to delete
     * @return true if deleted successfully, false otherwise
     * @throws IllegalStateException if the task has task reports
     */
    public boolean deleteTask(int taskId) throws IllegalStateException {
        if (connection == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }
        
        // Check if task has any reports - if yes, cannot delete
        TaskReportDAO taskReportDAO = new TaskReportDAO();
        if (taskReportDAO.hasTaskReports(taskId)) {
            throw new IllegalStateException("Cannot delete task: Task has task reports. Please delete all task reports first.");
        }
        
        try {
            // Disable auto-commit to handle transaction
            connection.setAutoCommit(false);
            
            // First, delete related records in TaskAssignee table
            String deleteTaskAssigneeSql = "DELETE FROM TaskAssignee WHERE TaskID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteTaskAssigneeSql)) {
                ps.setInt(1, taskId);
                ps.executeUpdate();
            }

            // Finally, delete the task from ProjectTask table
            // Note: TaskReport entries are not deleted here because we prevent deletion if reports exist
            String deleteTaskSql = "DELETE FROM ProjectTask WHERE TaskID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteTaskSql)) {
                ps.setInt(1, taskId);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error rolling back transaction", rollbackEx);
            }
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error deleting task", ex);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error resetting auto-commit", ex);
            }
        }
        return false;
    }

    public ProjectTask getTaskById(int taskId) {
        if (connection == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return null;
        }
        
        String sql = "SELECT pt.TaskID, pt.ProjectID, pt.TaskName, pt.Description, " +
                "pt.Deadline, pt.EstimateHourToDo, pt.CreatedAt, pt.Status, p.ProjectName " +
                "FROM ProjectTask pt " +
                "LEFT JOIN Project p ON pt.ProjectID = p.ProjectID " +
                "WHERE pt.TaskID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ProjectTask t = new ProjectTask();
                t.setTaskId(rs.getInt("TaskID"));
                Integer projectId = rs.getObject("ProjectID", Integer.class);
                t.setProjectId(projectId);
                t.setTaskName(rs.getString("TaskName"));
                t.setDescription(rs.getString("Description"));
                t.setDeadline(rs.getTimestamp("Deadline"));
                Double estimateHours = rs.getObject("EstimateHourToDo", Double.class);
                t.setEstimateHourToDo(estimateHours);
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                t.setStatus(rs.getString("Status"));
                t.setProjectName(rs.getString("ProjectName")); // Set project name for display
                return t;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving task by ID", ex);
        }
        return null;
    }

    public boolean updateTask(ProjectTask task) {
        if (connection == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }
        
        String sql = """
            UPDATE ProjectTask 
            SET ProjectID = ?, TaskName = ?, Description = ?, 
                Deadline = ?, EstimateHourToDo = ?, Status = ?
            WHERE TaskID = ?
            """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            
            // ProjectId is required - every task must belong to a project
            if (task.getProjectId() == null) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Cannot update task: ProjectId is required");
                return false;
            }
            ps.setInt(paramIndex++, task.getProjectId());
            ps.setString(paramIndex++, task.getTaskName());
            ps.setString(paramIndex++, task.getDescription());
            
            if (task.getDeadline() == null) {
                ps.setNull(paramIndex++, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(paramIndex++, task.getDeadline());
            }
            
            if (task.getEstimateHourToDo() == null) {
                ps.setNull(paramIndex++, java.sql.Types.DOUBLE);
            } else {
                ps.setDouble(paramIndex++, task.getEstimateHourToDo());
            }
            
            ps.setString(paramIndex++, task.getStatus());
            ps.setInt(paramIndex++, task.getTaskId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error updating task", ex);
        }
        return false;
    }

    public boolean createTask(ProjectTask task) {
        
        String sql = """
            INSERT INTO ProjectTask (ProjectID, TaskName, Description, Deadline, EstimateHourToDo, Status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int paramIndex = 1;
            
            // ProjectId is required - every task must belong to a project
            if (task.getProjectId() == null) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Cannot create task: ProjectId is required");
                return false;
            }
            ps.setInt(paramIndex++, task.getProjectId());
            ps.setString(paramIndex++, task.getTaskName());
            ps.setString(paramIndex++, task.getDescription());
            
            if (task.getDeadline() == null) {
                ps.setNull(paramIndex++, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(paramIndex++, task.getDeadline());
            }
            
            if (task.getEstimateHourToDo() == null) {
                ps.setNull(paramIndex++, java.sql.Types.DOUBLE);
            } else {
                ps.setDouble(paramIndex++, task.getEstimateHourToDo());
            }
            
            ps.setString(paramIndex++, task.getStatus() != null ? task.getStatus() : ProjectTask.STATUS_TODO);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    task.setTaskId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error creating task", ex);
        }
        return false;
    }

    // Reassign task to a project (projectId is required)
    public boolean reassignTask(int taskId, Integer projectId) {
        if (projectId == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Cannot reassign task: ProjectId is required");
            return false;
        }
        
        String sql = """
            UPDATE ProjectTask 
            SET ProjectID = ?
            WHERE TaskID = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}
