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

    /**
     * Get a single task report that belongs to the given user.
     */
    public TaskReport getTaskReportByIdAndUser(int reportId, int userId) {
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return null;
        }

        String sql = """
            SELECT ReportID, UserID, TaskID, ReportDescription,
                   EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID, CreatedAt
            FROM TaskReport
            WHERE ReportID = ? AND UserID = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TaskReport report = new TaskReport();
                report.setReportId(rs.getInt("ReportID"));
                report.setUserId(rs.getInt("UserID"));
                report.setTaskId(rs.getInt("TaskID"));
                report.setReportDescription(rs.getString("ReportDescription"));
                report.setEstimateWorkPercentDone(rs.getObject("EstimateWorkPercentDone", Double.class));
                report.setTotalHourUsed(rs.getObject("TotalHourUsed", Double.class));
                report.setTimesheetEntryId(rs.getObject("TimesheetEntryID", Integer.class));
                report.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return report;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error retrieving task report by id", ex);
        }
        return null;
    }

    /**
     * Update an existing task report (ownership enforced by userId).
     */
    public boolean updateTaskReport(TaskReport report) {
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }

        String sql = """
            UPDATE TaskReport
            SET TaskID = ?, ReportDescription = ?, EstimateWorkPercentDone = ?, TotalHourUsed = ?, TimesheetEntryID = ?
            WHERE ReportID = ? AND UserID = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, report.getTaskId());

            if (report.getReportDescription() == null || report.getReportDescription().trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(2, report.getReportDescription());
            }

            if (report.getEstimateWorkPercentDone() == null) {
                ps.setNull(3, java.sql.Types.DOUBLE);
            } else {
                ps.setDouble(3, report.getEstimateWorkPercentDone());
            }

            if (report.getTotalHourUsed() == null) {
                ps.setNull(4, java.sql.Types.DOUBLE);
            } else {
                ps.setDouble(4, report.getTotalHourUsed());
            }

            if (report.getTimesheetEntryId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, report.getTimesheetEntryId());
            }

            ps.setInt(6, report.getReportId());
            ps.setInt(7, report.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error updating task report", ex);
        }
        return false;
    }

    /**
     * Delete a task report owned by the specified user.
     */
    public boolean deleteTaskReport(int reportId, int userId) {
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }

        String sql = "DELETE FROM TaskReport WHERE ReportID = ? AND UserID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error deleting task report", ex);
        }
        return false;
    }

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
     * nghia&gemini Lấy danh sách tất cả TaskReport liên quan đến một Timesheet
     * cụ thể. Hàm này thực hiện JOIN giữa TaskReport và TimesheetEntry để lọc
     * dữ liệu.
     *
     * * @param timesheetId ID của Timesheet tổng
     * @return Danh sách các TaskReport tìm thấy
     */
    public ArrayList<TaskReport> getTaskReportsByTimesheetId(int timesheetId) {
        ArrayList<TaskReport> list = new ArrayList<>();

        // Sử dụng JOIN để nối TaskReport với TimesheetEntry
        String sql = "SELECT tr.* "
                + "FROM TaskReport tr "
                + "JOIN TimesheetEntry te ON tr.TimesheetEntryID = te.EntryID "
                + "WHERE te.TimesheetID = ? "
                + "ORDER BY tr.CreatedAt DESC";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, timesheetId);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    // Sử dụng hàm mapping để tái sử dụng code
                    TaskReport report = mapResultSetToReport(rs);
                    list.add(report);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại getTaskReportsByTimesheetId: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Check if a task has any task reports
     *
     * @param taskId The task ID to check
     * @return true if the task has at least one report, false otherwise
     */
    public boolean hasTaskReports(int taskId) {
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }

        String sql = "SELECT COUNT(*) as reportCount FROM TaskReport WHERE TaskID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("reportCount");
                return count > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error checking task reports", ex);
        }
        return false;
    }

    /**
     * Get the count of task reports for a specific task
     *
     * @param taskId The task ID
     * @return The number of reports for this task
     */
    public int countTaskReports(int taskId) {
        if (connection == null) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return 0;
        }

        String sql = "SELECT COUNT(*) as reportCount FROM TaskReport WHERE TaskID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("reportCount");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReportDAO.class.getName()).log(Level.SEVERE, "Error counting task reports", ex);
        }
        return 0;
    }
}
