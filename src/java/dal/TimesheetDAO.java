/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.TimeSheet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class TimesheetDAO extends DBContext {

    /**
     * Truy vấn danh sách các bản ghi Timesheet của một người dùng dựa trên mã
     * định danh (UserID).
     * * <p>
     * Các đối tượng {@link TimeSheet} trong danh sách kết quả sẽ bao gồm các
     * thông tin:</p>
     * <ul>
     * <li><b>UserID</b>: Mã người dùng sở hữu bản ghi.</li>
     * <li><b>TimesheetID</b>: Mã định danh duy nhất của bản ghi.</li>
     * <li><b>DayStart</b>: Ngày bắt đầu tuần làm việc (Thứ Hai).</li>
     * <li><b>DayEnd</b>: Ngày kết thúc tuần làm việc (Chủ Nhật).</li>
     * <li><b>LastUpdatedAt</b>: Thời điểm cập nhật cuối cùng.</li>
     * <li><b>Status</b>: Trạng thái hiện tại (Draft, Submitted, Reviewed).</li>
     * </ul>
     * * <p>
     * <b>Lưu ý:</b> Phương thức này <i>không</i> nạp dữ liệu cho thuộc tính
     * <code>summary</code> để tối ưu hiệu suất truy vấn danh sách.</p>
     *
     * * @param userId Mã định danh của người dùng cần lấy dữ liệu (UserID
     * trong DB).
     * @return {@link ArrayList} chứa các đối tượng {@link TimeSheet}. Trả về
     * danh sách rỗng nếu không tìm thấy dữ liệu.
     */
    public ArrayList<TimeSheet> getTimeSheetByUserId(int userId) {
        ArrayList<TimeSheet> list = new ArrayList<>();

        // Câu lệnh SQL truy vấn dựa trên cấu trúc DB bạn cung cấp
        String sql = "SELECT [TimesheetID], [UserID], [DayStart], [DayEnd], [LastUpdatedAt], [Status] "
                + "FROM [Timesheet] "
                + "WHERE [UserID] = ? "
                + "ORDER BY [DayStart] DESC";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, userId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    // Sử dụng Constructor đầy đủ của Entity TimeSheet
                    TimeSheet ts = new TimeSheet(
                            rs.getInt("TimesheetID"),
                            rs.getInt("UserID"),
                            rs.getDate("DayStart"),
                            rs.getDate("DayEnd"),
                            rs.getTimestamp("LastUpdatedAt"),
                            rs.getString("Status")
                    );
                    list.add(ts);
                }
            }
        } catch (SQLException e) {
            // Log lỗi để dễ dàng debug
            System.out.println("Lỗi tại getTimeSheetByUserId: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Truy vấn thông tin chi tiết của một bản ghi Timesheet dựa trên
     * TimesheetID.
     * <p>
     * Phương thức này thực hiện "Eager Loading" - nạp đầy đủ tất cả các trường
     * dữ liệu, bao gồm cả nội dung tóm tắt công việc (Summary).</p>
     *
     * * @param timesheetId Mã định danh của bản ghi Timesheet cần lấy.
     * @return Đối tượng {@link TimeSheet} đầy đủ thông tin, hoặc
     * <code>null</code> nếu không tìm thấy.
     */
    public TimeSheet getTimesheetByTimesheetId(int timesheetId) {
        // Câu lệnh SQL lấy tất cả các trường
        String sql = "SELECT [TimesheetID], [UserID], [DayStart], [DayEnd], [LastUpdatedAt], [Status], [Summary] "
                + "FROM [Timesheet] "
                + "WHERE [TimesheetID] = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, timesheetId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    // Sử dụng Constructor đầy đủ (đã bao gồm Summary)
                    return new TimeSheet(
                            rs.getInt("TimesheetID"),
                            rs.getInt("UserID"),
                            rs.getDate("DayStart"),
                            rs.getDate("DayEnd"),
                            rs.getTimestamp("LastUpdatedAt"),
                            rs.getString("Status"),
                            rs.getString("Summary") // Trường này cực kỳ quan trọng cho trang Detail
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại getTimesheetByTimesheetId: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Trả về null nếu ID không tồn tại trong DB
    }

    /**
     * Lấy danh sách các TimeSheet đang chờ supervisor review (status = Submitted).
     * 
     * @return Danh sách các TimeSheet có status = "Submitted", sắp xếp theo LastUpdatedAt DESC
     */
    public ArrayList<TimeSheet> getPendingTimesheets() {
        ArrayList<TimeSheet> list = new ArrayList<>();
        
        String sql = "SELECT [TimesheetID], [UserID], [DayStart], [DayEnd], [LastUpdatedAt], [Status] "
                + "FROM [Timesheet] "
                + "WHERE [Status] = ? "
                + "ORDER BY [LastUpdatedAt] DESC";
        
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, TimeSheet.STATUS_SUBMITTED);
            
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    TimeSheet ts = new TimeSheet(
                            rs.getInt("TimesheetID"),
                            rs.getInt("UserID"),
                            rs.getDate("DayStart"),
                            rs.getDate("DayEnd"),
                            rs.getTimestamp("LastUpdatedAt"),
                            rs.getString("Status")
                    );
                    list.add(ts);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại getPendingTimesheets: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

}
