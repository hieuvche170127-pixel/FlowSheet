/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.TimeSheet;
import java.util.ArrayList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

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

    // dùng để nghiệp vụ trước khi add timesheet
    // check trung lặp
    public boolean isTimesheetExist(int accountId, Date start) {
        String sql = "SELECT COUNT(*) FROM Timesheet WHERE AccountID = ? AND StartDate = ?";
        // Lấy connection mới, dùng xong tự đóng (Safe & Clean)
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setDate(2, start); // start này phải là java.sql.Date nhé anh

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu kết quả > 0 nghĩa là đã tồn tại
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi để anh dễ debug
        }
        return false;
    }

    // hàm tạo timesheet dựa trên ngày start và end và userid, mặc định là draft
    public boolean createTimesheet(int userId, java.sql.Date start, java.sql.Date end) {
        // Chỉ insert những trường bắt buộc và quan trọng nhất
        String sql = "INSERT INTO Timesheet (UserID, DayStart, DayEnd, Status) "
                + "VALUES (?, ?, ?, N'Draft')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, start);
            ps.setDate(3, end); // Đã verify ở controller nên cứ thế mà vả vào thôi anh

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // Nếu dính Unique Constraint (trùng ngày start), nó sẽ nhảy vào đây
            System.out.println("Lỗi createTimesheet: " + e.getMessage());
        }
        return false;
    }

    // cho hàm update nói chung và update summary nói riêng.
    public boolean isAbleToUpdateTimesheet(int timesheetId, int userId) {
        // Lấy ngày Thứ 2 của tuần hiện tại để so sánh
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate currentMonday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        java.sql.Date sqlCurrentMonday = java.sql.Date.valueOf(currentMonday);
        String sql = "SELECT COUNT(*) FROM Timesheet "
                + "WHERE TimesheetID = ? "
                + "AND UserID = ? "
                + "AND Status <> N'Reviewed' "
                + "AND DayStart = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, timesheetId);
            ps.setInt(2, userId);
            ps.setDate(3, sqlCurrentMonday);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int column = rs.getInt(1);
                    return column > 0; // Nếu tìm thấy 1 bản ghi thỏa mãn thì trả về true
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTimesheetSummary(int timesheetId, String newSummary) {
        // SYSDATETIME() sẽ tự động lấy giờ hiện tại của máy chủ SQL Server
        String sql = "UPDATE Timesheet SET Summary = ?, LastUpdatedAt = SYSDATETIME() WHERE TimesheetID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newSummary);
            ps.setInt(2, timesheetId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu cập nhật thành công
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra xem Timesheet có đủ điều kiện để xóa hay không. Điều kiện: Phải
     * đúng chủ sở hữu VÀ chưa có bất kỳ công việc (Entry) nào bên trong.
     */
    public boolean isTimesheetAbleToDelete(int timesheetId, int userId) {
        // Sử dụng Subquery để đếm số lượng Entry của Timesheet đó
        // Câu lệnh WHERE đảm bảo chỉ xét Timesheet của đúng User đang đăng nhập
        String sql = "SELECT (SELECT COUNT(*) FROM TimesheetEntry WHERE TimesheetID = t.TimesheetID) AS EntryCount "
                + "FROM Timesheet t "
                + "WHERE t.TimesheetID = ? AND t.UserID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, timesheetId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                // Trường hợp 1: Nếu rs.next() false -> Không tìm thấy Timesheet (sai ID hoặc không phải của User này)
                // => Hàm sẽ thoát xuống dưới và trả về false (Không cho xóa vì không có quyền/không tồn tại)
                if (rs.next()) {
                    // Nếu vào được đây, nghĩa là: Đúng User, Đúng Timesheet ID
                    int entryCount = rs.getInt("EntryCount");

                    // Trường hợp 2: entryCount > 0 -> Đã có Task/Công việc bên trong
                    // => (entryCount == 0) sẽ là false -> Trả về false (Không cho xóa vì đã có dữ liệu)
                    // Trường hợp 3: entryCount == 0 -> Timesheet trống hoàn toàn
                    // => (entryCount == 0) sẽ là true -> Trả về true (Đủ điều kiện để xóa sạch)
                    return entryCount == 0;
                }
            }
        } catch (Exception e) {
            // Log lỗi nếu có vấn đề về kết nối DB
            e.printStackTrace();
        }
        // Mặc định trả về false để đảm bảo an toàn tối đa
        return false;
    }

    public boolean deleteTimesheetById(int timesheetId) {
        // Câu lệnh xóa đơn giản vì đã có ON DELETE CASCADE trong DB
        String sql = "DELETE FROM Timesheet WHERE TimesheetID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, timesheetId);

            // executeUpdate trả về số lượng dòng bị ảnh hưởng
            int rowsAffected = ps.executeUpdate();

            // Nếu xóa được ít nhất 1 dòng thì trả về true
            return rowsAffected > 0;
        } catch (Exception e) {
            // Ghi log lỗi để dễ debug nếu có vấn đề về khóa ngoại hoặc kết nối
            e.printStackTrace();
        }
        return false;
    }
}
