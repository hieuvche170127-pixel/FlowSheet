/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class LeaveRequest {

    private Integer leaveId;

    // Required Fields
    private Integer userId;          // INT NOT NULL - member applying
    private LocalDate fromDate;      // DATE NOT NULL
    private LocalDate toDate;        // DATE NOT NULL
    private Integer durationDays;    // INT NOT NULL - total days of leave
    private String leaveType;        // NVARCHAR(50) NOT NULL DEFAULT 'GENERAL'
    private String status;           // NVARCHAR(20) NOT NULL DEFAULT 'PENDING'
    private String reason;           // NVARCHAR(MAX) NULL

    private LocalDateTime reviewedAt;  // DATETIME2 NULL
    private Integer approverId;      // INT NULL - supervisor/admin
    private String approverComment;  // NVARCHAR(MAX) NULL
    private LocalDateTime withdrawnAt; // DATETIME2 NULL - set when member withdraws

    // System/Audit Field
    private LocalDateTime appliedAt;   // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    public LeaveRequest() {
    }

    public LeaveRequest(Integer leaveId, Integer userId, LocalDate fromDate, LocalDate toDate, Integer durationDays, String leaveType, String status, String reason, LocalDateTime reviewedAt, Integer approverId, String approverComment, LocalDateTime withdrawnAt, LocalDateTime appliedAt) {
        this.leaveId = leaveId;
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.durationDays = durationDays;
        this.leaveType = leaveType;
        this.status = status;
        this.reason = reason;
        this.reviewedAt = reviewedAt;
        this.approverId = approverId;
        this.approverComment = approverComment;
        this.withdrawnAt = withdrawnAt;
        this.appliedAt = appliedAt;
    }

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public String getApproverComment() {
        return approverComment;
    }

    public void setApproverComment(String approverComment) {
        this.approverComment = approverComment;
    }

    public LocalDateTime getWithdrawnAt() {
        return withdrawnAt;
    }

    public void setWithdrawnAt(LocalDateTime withdrawnAt) {
        this.withdrawnAt = withdrawnAt;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

}
