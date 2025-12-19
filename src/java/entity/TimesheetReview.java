/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;
import java.sql.Timestamp;
/**
 *
 * @author Admin
 */
public class TimesheetReview {

    private int timesheetReviewId;

    // 2. Khóa ngoại
    private int timesheetId;
    private int reviewedById; // ID của người quản lý duyệt

    // 3. Thông tin review
    private String comment;
    private Timestamp reviewedAt;

    // --- Constructor Rỗng ---
    public TimesheetReview() {
    }

    // --- Constructor Đầy Đủ ---
    public TimesheetReview(int timesheetReviewId, int timesheetId, int reviewedById, String comment, Timestamp reviewedAt) {
        this.timesheetReviewId = timesheetReviewId;
        this.timesheetId = timesheetId;
        this.reviewedById = reviewedById;
        this.comment = comment;
        this.reviewedAt = reviewedAt;
    }

    // --- Getters & Setters ---
    public int getTimesheetReviewId() {
        return timesheetReviewId;
    }

    public void setTimesheetReviewId(int timesheetReviewId) {
        this.timesheetReviewId = timesheetReviewId;
    }

    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }

    public int getReviewedById() {
        return reviewedById;
    }

    public void setReviewedById(int reviewedById) {
        this.reviewedById = reviewedById;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
