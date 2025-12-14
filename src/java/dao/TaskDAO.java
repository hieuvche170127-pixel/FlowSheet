package dao;

import dal.DBContext;
import entity.Task;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskDAO extends DBContext{

    public List<Task> getAllTasksByUserId(int userId) {
        List<Task> list = new ArrayList<>();
        
        String sql = """
            SELECT T.TaskID, T.TaskName, T.TaskCode, T.Status, 
                   P.ProjectName, P.ProjectID
            FROM ProjectTask T
            JOIN TaskAssignee TA ON T.TaskID = TA.TaskID
            LEFT JOIN Project P ON T.ProjectID = P.ProjectID
            WHERE TM.UserID = ? AND T.IsActive = 1
            ORDER BY P.ProjectName, T.TaskName
        """;
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Task t = new Task();
                t.setTaskId(rs.getInt("TaskID"));
                t.setTaskName(rs.getString("TaskName"));
                t.setTaskCode(rs.getString("TaskCode"));
                t.setStatus(rs.getString("Status"));
                
                String pjName = rs.getString("ProjectName");
                t.setProjectName(pjName != null ? pjName : "Cá nhân");
                t.setProjectId(rs.getInt("ProjectID"));
                
                list.add(t);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

        public List<Task> getAllTasks() {
            return getTasksWithFilter(null, null);
        }
        
        public List<Task> getTasksWithFilter(String taskName, String projectName) {
            List<Task> list = new ArrayList<>();
            if (connection == null) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
                return list;
            }
            
            StringBuilder sql = new StringBuilder(
                "SELECT pt.TaskID, pt.ProjectID, pt.TaskCode, pt.TaskName, pt.Description, " +
                "pt.IsActive, pt.CreatedAt, pt.Status, p.ProjectName " +
                "FROM ProjectTask pt " +
                "LEFT JOIN Project p ON pt.ProjectID = p.ProjectID " +
                "WHERE pt.IsActive = 1"
            );
            
            List<String> conditions = new ArrayList<>();
            List<Object> parameters = new ArrayList<>();
            
            if (taskName != null && !taskName.trim().isEmpty()) {
                conditions.add("pt.TaskName LIKE ?");
                parameters.add("%" + taskName.trim() + "%");
            }
            
            if (projectName != null && !projectName.trim().isEmpty()) {
                conditions.add("p.ProjectName LIKE ?");
                parameters.add("%" + projectName.trim() + "%");
            }
            
            if (!conditions.isEmpty()) {
                sql.append(" AND ").append(String.join(" AND ", conditions));
            }

            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    ps.setObject(i + 1, parameters.get(i));
                }
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Task t = new Task();
                    t.setTaskId(rs.getInt("TaskID"));
                    t.setProjectId(rs.getInt("ProjectID"));
                    t.setTaskCode(rs.getString("TaskCode"));
                    t.setTaskName(rs.getString("TaskName"));
                    t.setDescription(rs.getString("Description"));
                    t.setIsActive(rs.getBoolean("IsActive"));
                    t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    t.setStatus(rs.getString("Status"));
                    t.setProjectName(rs.getString("ProjectName"));
                    list.add(t);
                }
            } catch (SQLException ex) {
                Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving tasks", ex);
            }
            return list;
        }

    public boolean deleteTask(int taskId) {
        if (connection == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
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
            
            // Optionally, set TaskID to NULL in TimesheetEntry (since TaskID is nullable)
            // Or we can leave it as is since it's nullable and won't block deletion
            String updateTimesheetSql = "UPDATE TimesheetEntry SET TaskID = NULL WHERE TaskID = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateTimesheetSql)) {
                ps.setInt(1, taskId);
                ps.executeUpdate();
            }
            
            // Finally, delete the task from ProjectTask table
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

    public Task getTaskById(int taskId) {
        if (connection == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return null;
        }
        
        String sql = "SELECT pt.TaskID, pt.ProjectID, pt.TaskCode, pt.TaskName, pt.Description, " +
                "pt.IsActive, pt.CreatedAt, pt.Status, p.ProjectName " +
                "FROM ProjectTask pt " +
                "LEFT JOIN Project p ON pt.ProjectID = p.ProjectID " +
                "WHERE pt.TaskID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Task t = new Task();
                t.setTaskId(rs.getInt("TaskID"));
                t.setProjectId(rs.getInt("ProjectID"));
                t.setTaskCode(rs.getString("TaskCode"));
                t.setTaskName(rs.getString("TaskName"));
                t.setDescription(rs.getString("Description"));
                t.setIsActive(rs.getBoolean("IsActive"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                t.setStatus(rs.getString("Status"));
                t.setProjectName(rs.getString("ProjectName"));
                return t;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error retrieving task by ID", ex);
        }
        return null;
    }

    public boolean updateTask(Task task) {
        if (connection == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }
        
        // ProjectID is NOT NULL in database, so we need to ensure it's not null
        if (task.getProjectId() == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.WARNING, "Cannot update task: ProjectID is required (NOT NULL in database)");
            return false;
        }
        
        String sql = """
            UPDATE ProjectTask 
            SET ProjectID = ?, TaskCode = ?, TaskName = ?, Description = ?, 
                IsActive = ?, Status = ?
            WHERE TaskID = ?
            """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, task.getProjectId());
            ps.setString(2, task.getTaskCode());
            ps.setString(3, task.getTaskName());
            ps.setString(4, task.getDescription());
            ps.setBoolean(5, task.isIsActive());
            ps.setString(6, task.getStatus());
            ps.setInt(7, task.getTaskId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, "Error updating task", ex);
        }
        return false;
    }

    public boolean createTask(Task task) {
        // ProjectID is NOT NULL in database, so we need to ensure it's not null
        if (task.getProjectId() == null) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.WARNING, "Cannot create task: ProjectID is required (NOT NULL in database)");
            return false;
        }
        
        String sql = """
            INSERT INTO ProjectTask (ProjectID, TaskCode, TaskName, Description, IsActive, Status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, task.getProjectId());
            ps.setString(2, task.getTaskCode());
            ps.setString(3, task.getTaskName());
            ps.setString(4, task.getDescription());
            ps.setBoolean(5, task.isIsActive());
            ps.setString(6, task.getStatus());

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

    // Updated: Reassign task (only to project or unassign)
    public boolean reassignTask(int taskId, Integer projectId) {
        String sql = """
            UPDATE ProjectTask 
            SET ProjectID = ?
            WHERE TaskID = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, projectId);
            ps.setInt(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}
