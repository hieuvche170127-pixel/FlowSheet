/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import utilities.DateTimeConverter;
import entity.TimesheetEntry;

import java.util.ArrayList;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
                + "[DelayMinutes], [Note], [CreatedAt] "
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

}
