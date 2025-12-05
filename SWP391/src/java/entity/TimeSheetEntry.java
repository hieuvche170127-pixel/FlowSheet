/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author Admin
 */
public class TimeSheetEntry {
    private Integer entryId;      

    // Foreign Keys (INT NOT NULL)
    private Integer userId;      // member
    
    // Optional Foreign Keys (INT NULL)
    private Integer projectId;   // optional
    private Integer taskId;      // optional
    
    // Date/Time Fields
    private LocalDate workDate;  // DATE NOT NULL
    private LocalTime startTime; // TIME NULL
    private LocalTime endTime;   // TIME NULL
    
    // Data Fields
    private Integer minutesWorked; // INT NOT NULL
    private String note;         // NVARCHAR(MAX) NULL
    
    // System/Audit Fields
    private LocalDateTime createdAt; // DATETIME2 NOT NULL
    private LocalDateTime updatedAt; // DATETIME2 NOT NULL
    
    // Fields added via ALTER TABLE
    private String status;         // NVARCHAR(20) NOT NULL DEFAULT 'PENDING'
    private Integer approvedById;  // INT NULL (FK to UserAccount)
    private LocalDateTime approvedAt; // DATETIME2 NULL

    public TimeSheetEntry(Integer entryId, Integer userId, Integer projectId, Integer taskId, LocalDate workDate, LocalTime startTime, LocalTime endTime, Integer minutesWorked, String note, LocalDateTime createdAt, LocalDateTime updatedAt, String status, Integer approvedById, LocalDateTime approvedAt) {
        this.entryId = entryId;
        this.userId = userId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minutesWorked = minutesWorked;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.approvedById = approvedById;
        this.approvedAt = approvedAt;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMinutesWorked() {
        return minutesWorked;
    }

    public void setMinutesWorked(Integer minutesWorked) {
        this.minutesWorked = minutesWorked;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Integer approvedById) {
        this.approvedById = approvedById;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    
}
