/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.TimeSheet;
import entity.TimesheetEntry;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 *
 * @author Admin
 */
public class TimesheetEntryDAO extends DBContext {

    /**
     * Hàm hỗ trợ mapping dữ liệu từ ResultSet sang đối tượng TimesheetEntry.
     * Giúp tái sử dụng code ở nhiều hàm truy vấn khác nhau.
     */
    private TimesheetEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        TimesheetEntry entry = new TimesheetEntry();

        entry.setEntryId(rs.getInt("EntryID"));
        entry.setTimesheetId(rs.getInt("TimesheetID"));
        entry.setWorkDate(rs.getDate("WorkDate"));
        entry.setStartTime(rs.getTime("StartTime"));
        entry.setEndTime(rs.getTime("EndTime"));
        entry.setDelayMinutes(rs.getInt("DelayMinutes"));
        entry.setNote(rs.getString("Note"));
        entry.setCreatedAt(rs.getTimestamp("CreatedAt"));
        
        // Map ProjectID và TaskID nếu có trong ResultSet
        try {
            Integer projectId = rs.getObject("ProjectID", Integer.class);
            entry.setProjectId(projectId);
        } catch (SQLException e) {
            // Column không tồn tại, bỏ qua
        }
        
        try {
            Integer taskId = rs.getObject("TaskID", Integer.class);
            entry.setTaskId(taskId);
        } catch (SQLException e) {
            // Column không tồn tại, bỏ qua
        }

        return entry;
    }

    /**
     * Lấy danh sách tất cả các công việc chi tiết (entries) thuộc về một
     * TimesheetID. Kết quả được sắp xếp theo ngày làm việc và giờ bắt đầu để
     * hiển thị logic hơn.
     *
     * * @param timesheetId Mã định danh của Timesheet tổng (Header).
     * @return Danh sách các TimesheetEntry liên quan.
     */
    public ArrayList<TimesheetEntry> getEntriesByTimesheetId(int timesheetId) {
        ArrayList<TimesheetEntry> list = new ArrayList<>();
        // Sắp xếp theo WorkDate và StartTime để dữ liệu hiện ra theo đúng trình tự thời gian
        String sql = "SELECT [EntryID], [TimesheetID], [WorkDate], [StartTime], [EndTime], "
                + "[DelayMinutes], [Note], [CreatedAt]"
                + "FROM [TimesheetEntry] "
                + "WHERE [TimesheetID] = ? "
                + "ORDER BY [WorkDate] ASC, [StartTime] ASC";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, timesheetId);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    // Gọi hàm mapping để tạo đối tượng
                    TimesheetEntry entry = mapResultSetToEntry(rs);
                    list.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại getEntriesByTimesheetId: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy danh sách các TimesheetEntry từ các TimeSheet đang chờ supervisor review (status = Submitted).
     * Join với TimeSheet để chỉ lấy entries từ các timesheets đã submit.
     * 
     * @return Danh sách các TimesheetEntry từ các TimeSheet có status = "Submitted"
     */
    public ArrayList<TimesheetEntry> getPendingTimesheetEntries() {
        ArrayList<TimesheetEntry> list = new ArrayList<>();
        
        String sql = "SELECT te.[EntryID], te.[TimesheetID], te.[WorkDate], te.[StartTime], te.[EndTime], "
                + "te.[DelayMinutes], te.[Note], te.[CreatedAt], te.[ProjectID], te.[TaskID] "
                + "FROM [TimesheetEntry] te "
                + "INNER JOIN [Timesheet] t ON te.[TimesheetID] = t.[TimesheetID] "
                + "WHERE t.[Status] = ? "
                + "ORDER BY te.[WorkDate] DESC, te.[StartTime] DESC";
        
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, TimeSheet.STATUS_SUBMITTED);
            
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    TimesheetEntry entry = mapResultSetToEntry(rs);
                    list.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại getPendingTimesheetEntries: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * Lấy danh sách các TimesheetEntry của một người dùng dựa trên UserID.
     * Join với Timesheet để lấy entries từ các timesheets của user.
     * 
     * @param userId Mã định danh của người dùng
     * @return Danh sách các TimesheetEntry của user
     */
    public ArrayList<TimesheetEntry> getEntriesByUserId(int userId) {
        ArrayList<TimesheetEntry> list = new ArrayList<>();
        
        if (connection == null) {
            System.err.println("Database connection is null");
            return list;
        }
        
        String sql = "SELECT te.[EntryID], te.[TimesheetID], te.[WorkDate], te.[StartTime], te.[EndTime], "
                + "te.[DelayMinutes], te.[Note], te.[CreatedAt], te.[ProjectID], te.[TaskID] "
                + "FROM [TimesheetEntry] te "
                + "INNER JOIN [Timesheet] t ON te.[TimesheetID] = t.[TimesheetID] "
                + "WHERE t.[UserID] = ? "
                + "ORDER BY te.[WorkDate] DESC, te.[StartTime] DESC";
        
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, userId);
            
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    TimesheetEntry entry = mapResultSetToEntry(rs);
                    list.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại getEntriesByUserId: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * Tìm một TimesheetEntry theo EntryID và UserID để đảm bảo entry thuộc về user đó.
     * 
     * @param entryId Mã định danh của entry
     * @param userId Mã định danh của người dùng
     * @return TimesheetEntry nếu tìm thấy và thuộc về user, null nếu không
     */
    public TimesheetEntry findByIdAndUser(int entryId, int userId) {
        if (connection == null) {
            System.err.println("Database connection is null");
            return null;
        }
        
        String sql = "SELECT te.[EntryID], te.[TimesheetID], te.[WorkDate], te.[StartTime], te.[EndTime], "
                + "te.[DelayMinutes], te.[Note], te.[CreatedAt], te.[ProjectID], te.[TaskID] "
                + "FROM [TimesheetEntry] te "
                + "INNER JOIN [Timesheet] t ON te.[TimesheetID] = t.[TimesheetID] "
                + "WHERE te.[EntryID] = ? AND t.[UserID] = ?";
        
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, entryId);
            stm.setInt(2, userId);
            
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntry(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại findByIdAndUser: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

}
