/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.TaskReport;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class TaskReportDAO extends DBContext {

    /**
     * Create a new task report
     */
    public boolean createTaskReport(TaskReport report) {
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }

        String sql = """
            INSERT INTO TaskReport (UserID, TaskID, ReportDescription, EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, report.getUserId());
            ps.setInt(2, report.getTaskId());
            
            if (report.getReportDescription() == null || report.getReportDescription().trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(3, report.getReportDescription());
            }
            
            if (report.getEstimateWorkPercentDone() == null) {
                ps.setDouble(4, 0.0);
            } else {
                ps.setDouble(4, report.getEstimateWorkPercentDone());
            }
            
            if (report.getTotalHourUsed() == null) {
                ps.setDouble(5, 0.0);
            } else {
                ps.setDouble(5, report.getTotalHourUsed());
            }
            
            if (report.getTimesheetEntryId() == null) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, report.getTimesheetEntryId());
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    report.setReportId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error creating task report", ex);
        }
        return false;
    }

    /**
     * Get all task reports for a specific user
     */
    public List<TaskReport> getTaskReportsByUserId(int userId) {
        List<TaskReport> list = new ArrayList<>();
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return list;
        }

        String sql = """
            SELECT ReportID, UserID, TaskID, ReportDescription, 
                   EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID, CreatedAt
            FROM TaskReport
            WHERE UserID = ?
            ORDER BY CreatedAt DESC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TaskReport report = new TaskReport();
                report.setReportId(rs.getInt("ReportID"));
                report.setUserId(rs.getInt("UserID"));
                report.setTaskId(rs.getInt("TaskID"));
                report.setReportDescription(rs.getString("ReportDescription"));
                
                Double percentDone = rs.getObject("EstimateWorkPercentDone", Double.class);
                report.setEstimateWorkPercentDone(percentDone);
                
                Double totalHours = rs.getObject("TotalHourUsed", Double.class);
                report.setTotalHourUsed(totalHours);
                
                Integer timesheetEntryId = rs.getObject("TimesheetEntryID", Integer.class);
                report.setTimesheetEntryId(timesheetEntryId);
                
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                report.setCreatedAt(createdAt);
                
                list.add(report);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error retrieving task reports", ex);
        }
        return list;
    }

    /**
     * Get all task reports for a specific task
     */
    public List<TaskReport> getTaskReportsByTaskId(int taskId) {
        List<TaskReport> list = new ArrayList<>();
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return list;
        }

        String sql = """
            SELECT ReportID, UserID, TaskID, ReportDescription, 
                   EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID, CreatedAt
            FROM TaskReport
            WHERE TaskID = ?
            ORDER BY CreatedAt DESC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TaskReport report = new TaskReport();
                report.setReportId(rs.getInt("ReportID"));
                report.setUserId(rs.getInt("UserID"));
                report.setTaskId(rs.getInt("TaskID"));
                report.setReportDescription(rs.getString("ReportDescription"));
                
                Double percentDone = rs.getObject("EstimateWorkPercentDone", Double.class);
                report.setEstimateWorkPercentDone(percentDone);
                
                Double totalHours = rs.getObject("TotalHourUsed", Double.class);
                report.setTotalHourUsed(totalHours);
                
                Integer timesheetEntryId = rs.getObject("TimesheetEntryID", Integer.class);
                report.setTimesheetEntryId(timesheetEntryId);
                
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                report.setCreatedAt(createdAt);
                
                list.add(report);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error retrieving task reports", ex);
        }
        return list;
    }
}

