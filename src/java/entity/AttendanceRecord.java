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
public class AttendanceRecord {
    // Primary Key (INT IDENTITY)
    private Integer attendanceId; 

    // Required Fields
    private Integer userId;            // INT NOT NULL
    private LocalDate attendanceDate;  // DATE NOT NULL
    private String status;             // NVARCHAR(20) NOT NULL DEFAULT 'OPEN'

    // Optional Fields (TIME NULL)
    private LocalTime clockInTime;     // TIME NULL
    private LocalTime clockOutTime;    // TIME NULL
    
    // Calculated Field (INT NULL)
    private Integer totalMinutes;      // INT NULL
    
    // System/Audit Fields
    private LocalDateTime createdAt;   // DATETIME2 NOT NULL
    private LocalDateTime updatedAt;   // DATETIME2 NOT NULL

    public AttendanceRecord() {
    }

    public AttendanceRecord(Integer attendanceId, Integer userId, LocalDate attendanceDate, String status, LocalTime clockInTime, LocalTime clockOutTime, Integer totalMinutes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.totalMinutes = totalMinutes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
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
    
    
    
}
