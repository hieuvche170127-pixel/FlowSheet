/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import utilities.DateTimeConverter;
import entity.ProjectTask;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class ProjectTaskDAO extends DBContext {

    private ProjectTask mapProjectTaskFromResultSet(ResultSet rs) throws Exception {
        ProjectTask task = null;
        try {
            task = new ProjectTask();

            // 1. Primary Key
            task.setTaskId(rs.getInt("taskId"));

            // 2. Foreign Key
            task.setProjectId(rs.getInt("projectId"));

            // 3. Basic Information
            task.setTaskCode(rs.getString("taskCode"));
            task.setTaskName(rs.getString("taskName"));
            task.setDescription(rs.getString("description"));

            // 4. Boolean Field
            task.setIsActive(rs.getBoolean("isActive"));

            // 5. Timestamp Field - convert sang LocalDateTime
            task.setCreatedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(rs.getTimestamp("createdAt")));

            // 6. Status Field
            task.setStatus(rs.getString("status"));

        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    /**
     * Lấy tất cả ProjectTask từ database
     */
    public ArrayList<ProjectTask> getAllProjectTask() {
        ArrayList<ProjectTask> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM ProjectTask";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                ProjectTask task = mapProjectTaskFromResultSet(rs);
                list.add(task);
            }

            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ArrayList<ProjectTask> getAllTaskByProjectId(int projectId) {
        ArrayList<ProjectTask> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM ProjectTask WHERE projectId = " + projectId;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                ProjectTask task = mapProjectTaskFromResultSet(rs);
                list.add(task);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
    
    
}
