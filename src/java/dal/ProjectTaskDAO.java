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

            // 2. Foreign Key (Sử dụng getObject để xử lý trường hợp projectId bị NULL trong DB)
            task.setProjectId((Integer) rs.getObject("projectId"));

            // 3. Thông tin cơ bản
            task.setTaskName(rs.getString("taskName"));
            task.setDescription(rs.getString("description"));

            // 4. Các trường thời gian (Dùng trực tiếp Timestamp theo Entity của bạn)
            task.setDeadline(rs.getTimestamp("deadline"));
            task.setCreatedAt(rs.getTimestamp("createdAt"));

            // 5. Trường số thực (Sử dụng getObject để tránh lỗi nếu estimateHourToDo là NULL)
            task.setEstimateHourToDo((Double) rs.getObject("estimateHourToDo"));

            // 6. Trạng thái (Sử dụng setter để trigger logic kiểm tra tính hợp lệ trong Entity)
            task.setStatus(rs.getString("status"));

            // 7. (Tùy chọn) Nếu trong câu SQL có Join với bảng Project để lấy tên
            // task.setProjectName(rs.getString("projectName"));
        } catch (Exception e) {
            // Log lỗi hoặc ném tiếp để tầng DAO xử lý
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
