/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 *
 * @author Admin
 */
public class TimeSheetEntry {
       private int entryId;
    private int timesheetId; // Khóa ngoại
    private Date workDate;   // java.sql.Date (chỉ chứa ngày, không giờ)
    private Time startTime;  // java.sql.Time (chỉ chứa giờ)
    private Time endTime;    // java.sql.Time
    
    // Default = 0 trong SQL -> trong Java int mặc định cũng là 0
    private int delayMinutes; 
    
    private String note;
    private Timestamp createdAt;

    // --- Constructor Rỗng ---
    public TimeSheetEntry() {
    }

    // --- Constructor Đầy Đủ ---
    public TimeSheetEntry(int entryId, int timesheetId, Date workDate, Time startTime, Time endTime, int delayMinutes, String note, Timestamp createdAt) {
        this.entryId = entryId;
        this.timesheetId = timesheetId;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        setDelayMinutes(delayMinutes); // Validate delay không âm
        this.note = note;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    // Validate: Thời gian nghỉ không được là số âm
    public void setDelayMinutes(int delayMinutes) {
        if (delayMinutes < 0) {
            throw new IllegalArgumentException("Thời gian delay không được âm.");
        }
        this.delayMinutes = delayMinutes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
}
