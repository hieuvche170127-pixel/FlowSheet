/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.sql.Date;
import java.sql.Timestamp;
/**
 *
 * @author Admin
 */
public class TimeSheet {
        // 1. Định nghĩa các trạng thái cho phép (Hardcode Strings)
    public static final String STATUS_DRAFT = "Draft";
    public static final String STATUS_SUBMITTED = "Submitted";
    public static final String STATUS_REVIEWED = "Reviewed";

    private int timesheetId;
    private int userId;
    
    // Dùng java.sql.Date cho trường chỉ chứa ngày
    private Date dayStart;
    private Date dayEnd; 
    
    private Timestamp lastUpdatedAt;
    private String status;
    
    private String summary;

    // --- Constructor Rỗng ---
    public TimeSheet() {
        // Mặc định giống SQL: tạo mới là Draft
        this.status = STATUS_DRAFT; 
    }

    // constructor để lấy cho mytimesheetlist
    public TimeSheet(int timesheetId, int userId, Date dayStart, Date dayEnd, Timestamp lastUpdatedAt, String status) {
        this.timesheetId = timesheetId;
        this.userId = userId;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.lastUpdatedAt = lastUpdatedAt;
        // Dùng setter để validate status
        setStatus(status);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    // --- Constructor Đầy Đủ ---cho mytimesheet detail
    public TimeSheet(int timesheetId, int userId, Date dayStart, Date dayEnd, Timestamp lastUpdatedAt, String status, String summary) {
        this.timesheetId = timesheetId;
        this.userId = userId;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.lastUpdatedAt = lastUpdatedAt;
        this.status = status;
        this.summary = summary;
    }
    

    // --- Getters & Setters ---

    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDayStart() {
        return dayStart;
    }

    public void setDayStart(Date dayStart) {
        this.dayStart = dayStart;
    }

    public Date getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(Date dayEnd) {
        this.dayEnd = dayEnd;
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Validate trạng thái: Chỉ chấp nhận Draft, Submitted, Reviewed.
     * Tự động set mặc định là Draft nếu truyền null.
     */
    public void setStatus(String status) {
        if (status == null) {
            this.status = STATUS_DRAFT;
            return;
        }

        // Chuẩn hóa chuỗi để so sánh (bỏ khoảng trắng thừa,...)
        String trimStatus = status.trim();

        // So sánh (IgnoreCase để "draft" hay "Draft" đều nhận)
        if (STATUS_DRAFT.equalsIgnoreCase(trimStatus)) {
            this.status = STATUS_DRAFT;
        } else if (STATUS_SUBMITTED.equalsIgnoreCase(trimStatus)) {
            this.status = STATUS_SUBMITTED;
        } else if (STATUS_REVIEWED.equalsIgnoreCase(trimStatus)) {
            this.status = STATUS_REVIEWED;
        } else {
            throw new IllegalArgumentException("Trạng thái Timesheet không hợp lệ: '" + status + "'. Chỉ chấp nhận: Draft, Submitted, Reviewed.");
        }
    }
}
