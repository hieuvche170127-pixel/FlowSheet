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
     * Lấy danh sách các TimesheetEntry từ các TimeSheet đang chờ supervisor
     * review (status = Submitted). Join với TimeSheet để chỉ lấy entries từ các
     * timesheets đã submit.
     *
     * @return Danh sách các TimesheetEntry từ các TimeSheet có status =
     * "Submitted"
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
     * Lấy danh sách các TimesheetEntry của một người dùng dựa trên UserID. Join
     * với Timesheet để lấy entries từ các timesheets của user.
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
     * Tìm một TimesheetEntry theo EntryID và UserID để đảm bảo entry thuộc về
     * user đó.
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

    public boolean existEntryByDateAndTimesheetId(java.sql.Date workDate, int timesheetId) throws SQLException {
        // tìm xem có ngày đấy trong timesheet đấy chưa ? nếu có thì trả về true - tức count ==1
        // select Count (*)  from  timesheetEntry where TimesheetID = 7 and WorkDate = '2025-12-15'
        String sql = "select Count (*)  from  timesheetEntry where TimesheetID = ? and WorkDate = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, timesheetId);
            ps.setDate(2, workDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // Nếu count > 0 tức là ĐÃ TỒN TẠI (true)
                }
            }
        }
        return false;
    }

    public boolean addTimeSheetEntry(TimesheetEntry addedTsEntry) {
        // Câu lệnh SQL (Bỏ chữ 'from' nhé anh, INSERT INTO + tên bảng)
        String sql = "INSERT INTO timesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // 1. Set các giá trị cơ bản
            ps.setInt(1, addedTsEntry.getTimesheetId());
            ps.setDate(2, addedTsEntry.getWorkDate());
            ps.setTime(3, addedTsEntry.getStartTime());
            // 2. Xử lý EndTime (Nếu null thì báo cho SQL biết là NULL)
            if (addedTsEntry.getEndTime() != null) {
                ps.setTime(4, addedTsEntry.getEndTime());
            } else {
                ps.setNull(4, java.sql.Types.TIME);
            }
            // 3. Các trường còn lại
            ps.setInt(5, addedTsEntry.getDelayMinutes());
            ps.setString(6, addedTsEntry.getNote());
            // 4. Thực thi
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu chèn thành công ít nhất 1 dòng
        } catch (SQLException e) {
            System.out.println("Lỗi addTimeSheetEntry: " + e.getMessage());
            return false;
        }
    }

    // kiểm tra xem entry này đã chứa task report nào hay chưa
    // trả về true nếu đã có ít nhaaset 1 bản ghi
    // false nếu chưa có bản ghi nào.
    public boolean isEntryContainAnyTaskReport(int entryId) {
//        (nghĩa viết)  select Count (*)  from timesheetEntry t join TaskReport tr on t.EntryID = tr.TimesheetEntryID where EntryID = 9
// (gemini viết- đúng hơn thật :)) )  SELECT COUNT(*) FROM TaskReport WHERE TimesheetEntryID = 9
        String sql = "SELECT COUNT(*) FROM TaskReport WHERE TimesheetEntryID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Trả về true nếu có báo cáo con
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi addTimeSheetEntry: " + e.getMessage());
            return false;
        }
        return false;
    }

    // kiểm tra xem liệu 1 timesheetentry có thực sự thuộc về 1 timesheet hay không
    // vì có thể người dùng sẽ gửi một request mà timesheetentry đó ko thuộc timesheet đó, 
    // nếu xóa luôn theo id thì có thể xảy ra vấn đề. 
    // vì data từ bên client là ko tin được, nên chỉ lấy cả 2 rồi validate lại chứ ko có chuyện check từ
    // bên client, thấy nó hợp lý mà cho xóa luôn được. 
    public boolean isTimesheetEntryBelongToTimesheet(int timesheet, int timesheetEntry) {
//         select Count (*)  from  timesheetEntry where TimesheetID = 7 and EntryID = 3
        String query = "select count (*) from timesheetEntry where timesheetId = ? and entryid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, timesheet);
            ps.setInt(2, timesheetEntry);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // tại có mỗi 1 column lên lấy luôn 
                    int numberOfRecord = rs.getInt(1);
                    // nếu số lượng bản ghi ko lớn hơn 1, tức trả về false, nếu lớn hơn thì trả về true. simple
                    return numberOfRecord > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi addTimeSheetEntry: " + e.getMessage());
            return false;
        }
        return false;
    }

    public boolean deleteTimesheetEntry(int entryId) {
        String sql = "DELETE FROM timesheetEntry WHERE EntryID = ?"; // Anh check lại tên cột trong DB là EntryID hay TimesheetEntryId nhé
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // 1. Truyền ID cần xóa vào
            ps.setInt(1, entryId);
            // 2. Thực thi (Xóa cũng dùng executeUpdate vì nó làm thay đổi dữ liệu)
            int rowsAffected = ps.executeUpdate();
            // 3. Nếu số dòng bị tác động > 0 là xóa thành công
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa Entry: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // hàm này dùng để check xem 1 user id có quyền được xem cái timesheetentry cụ thể nào đó hay ko 
    // dung join từ bên timesheet là được chắc  dùng count :)))
    public boolean isOwnerOfEntry(int userId, int entryId) {
        String sql = "SELECT COUNT(*) FROM TimesheetEntry tse "
                + "JOIN Timesheet ts ON tse.TimesheetID = ts.TimesheetID "
                + "WHERE ts.UserID = ? AND tse.EntryID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi check owner: " + e.getMessage());
        }
        return false;
    }

    public TimesheetEntry getTimesheetEntryByTimesheetEntryId(int timesheetEntryId) {
        String query = "select * from timesheetentry where entryId = ? ";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, timesheetEntryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TimesheetEntry tsEntry = mapResultSetToEntry(rs);
                    return tsEntry;
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi check owner: " + e.getMessage());
        }
        return null;
    }

    public boolean updateTimesheetEntry(TimesheetEntry updated) {
        // Câu SQL chuẩn: UPDATE [Tên bảng] SET [Cột = ?] WHERE [Điều kiện]
        String sql = "UPDATE timesheetentry SET starttime = ?, endtime = ?, delayminutes = ?, note = ? WHERE entryid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // 1. Set StartTime
            ps.setTime(1, updated.getStartTime());

            // 2. Kiểm tra EndTime có null hay không
            if (updated.getEndTime() != null) {
                ps.setTime(2, updated.getEndTime());
            } else {
                // Nếu null thì đẩy giá trị NULL của SQL vào
                ps.setNull(2, java.sql.Types.TIME);
            }

            // 3. Set các trường còn lại
            ps.setInt(3, updated.getDelayMinutes());
            ps.setString(4, updated.getNote());
            ps.setInt(5, updated.getEntryId());

            // Chạy lệnh update
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
