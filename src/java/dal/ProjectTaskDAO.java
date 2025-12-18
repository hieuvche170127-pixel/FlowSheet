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

   private TaskReport mapResultSetToReport(ResultSet rs) throws SQLException {
    TaskReport report = new TaskReport();
    
    report.setReportId(rs.getInt("ReportID"));
    report.setUserId(rs.getInt("UserID"));
    report.setTaskId(rs.getInt("TaskID"));
    report.setReportDescription(rs.getString("ReportDescription"));
    
    // Sử dụng getDouble cho DECIMAL
    report.setEstimateWorkPercentDone(rs.getDouble("EstimateWorkPercentDone"));
    report.setTotalHourUsed(rs.getDouble("TotalHourUsed"));
    
    // Xử lý Integer có thể null cho TimesheetEntryID
    int entryId = rs.getInt("TimesheetEntryID");
    if (rs.wasNull()) {
        report.setTimesheetEntryId(null);
    } else {
        report.setTimesheetEntryId(entryId);
    }
    
    report.setCreatedAt(rs.getTimestamp("CreatedAt"));
    return report;
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
