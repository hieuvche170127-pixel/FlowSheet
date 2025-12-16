/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import utilities.DateTimeConverter;
import entity.TimeSheet;

import java.util.ArrayList;
import java.util.List;

import java.sql.Statement;
import java.sql.ResultSet;


/**
 *
 * @author Admin
 */
public class TimesheetEntryDAO extends DBContext {

    private TimeSheet mapTimesheetFromResultset(ResultSet rs) throws Exception {
        TimeSheet timesheet = null;
        try {
            timesheet = new TimeSheet();

            // 1. Primary Key
            timesheet.setEntryId(rs.getInt("entryId"));

            // 2. Foreign Keys (INT NOT NULL)
            timesheet.setUserId(rs.getInt("userId"));

            timesheet.setProjectId(rs.getInt("projectId"));
            if (rs.wasNull()) {
                timesheet.setProjectId(null); // Nếu giá trị DB là NULL, set Java object là null
            }
            timesheet.setTaskId(rs.getInt("taskId"));
            if (rs.wasNull()) {
                timesheet.setTaskId(null);
            }
            // ở đây thì rs.getDate sẽ trả về java.sql.Date (một subclass của util.date)
            // mình có viết hàm để convert rồi nên ngon choét 
//                java.sql.Date date = rs.getDate("workDate");
//                LocalDate localDate = DateConverter.convertSqlDateToLocalDate(rs.getDate("workDate"));
            timesheet.setWorkDate(DateTimeConverter.convertSqlDateToLocalDate(rs.getDate("workDate")));

            timesheet.setStartTime(DateTimeConverter.convertSqlTimeToLocalTime(rs.getTime("startTime")));
            timesheet.setEndTime(DateTimeConverter.convertSqlTimeToLocalTime(rs.getTime("endTime")));

            timesheet.setMinutesWorked(rs.getInt("minutesWorked"));
            timesheet.setNote(rs.getString("note"));

            // lõi ở đây, fix tiếp thôi kiểu ở đây có cả giờ,phút,giấy và siêu chi tiết ? dù lúc input có mỗi giờ với phút :)))
            timesheet.setCreatedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(rs.getTimestamp("createdAt")));
            timesheet.setUpdatedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(rs.getTimestamp("updatedAt")));

            // 7. Fields added via ALTER TABLE (Status và Approval)
            timesheet.setStatus(rs.getString("status"));

            // approvedById có thể NULL
            timesheet.setApprovedById(rs.getInt("approvedById"));
            if (rs.wasNull()) {
                timesheet.setApprovedById(null);
            }
            // approvedAt có thể NULL
            timesheet.setApprovedAt(DateTimeConverter.convertSqlTimestampToLocalDateTime(rs.getTimestamp("approvedAt")));
        } catch (Exception e) {
            throw e;
        }

        return timesheet;
    }

    public ArrayList<TimeSheet> getAllTimesheet() {
        ArrayList<TimeSheet> list = new ArrayList<>();
        try {
            String sql = "select * from timesheetentry";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                TimeSheet timesheet = mapTimesheetFromResultset(rs);
                list.add(timesheet);
            }
            // Đóng tài nguyên
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
    
    public List<TimeSheet> getPendingTimesheetEntries() {
        List<TimeSheet> list = new ArrayList<>();
        try {
            String sql = "select * from timesheetentry where status = 'Submitted' or status = 'Draft' order by createdAt desc";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                TimeSheet timesheet = mapTimesheetFromResultset(rs);
                list.add(timesheet);
            }
            // Đóng tài nguyên
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
