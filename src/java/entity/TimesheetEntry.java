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
public class TimesheetEntry {

    private int entryId;
    private int timesheetId;
    // Optional reference fields populated via JOINs when available
    private Integer projectId;
    private Integer taskId;

    // Dùng hàng cổ java.sql
    private Date workDate;
    private Time startTime;
    private Time endTime;

    private int delayMinutes;
    private String note;
    private Timestamp createdAt;

    public TimesheetEntry() {
    }

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

    public void setDelayMinutes(int delayMinutes) {
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

    /**
     * Derived value to keep compatibility with views expecting a minutesWorked field.
     */
    public int getMinutesWorked() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        long diffMillis = endTime.getTime() - startTime.getTime();
        long minutes = diffMillis / (60 * 1000);
        long worked = minutes - delayMinutes;
        return (int) Math.max(worked, 0);
    }

    public TimesheetEntry(int entryId, int timesheetId, Date workDate, Time startTime, Time endTime, int delayMinutes, String note, Timestamp createdAt) {
        this.entryId = entryId;
        this.timesheetId = timesheetId;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.delayMinutes = delayMinutes;
        this.note = note;
        this.createdAt = createdAt;
    }




}

