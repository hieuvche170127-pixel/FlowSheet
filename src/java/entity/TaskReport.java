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
public class TaskReport {
     private int reportId;
    private int userId;
    private int taskId;
    private String reportDescription;
    
    // Mapping DECIMAL -> Double
    private Double estimateWorkPercentDone;
    private Double totalHourUsed;
    
    // Integer để chấp nhận giá trị NULL (vì SQL cho phép NULL)
    private Integer timesheetEntryId;
    
    private Timestamp createdAt;

    // --- Constructor Rỗng ---
    public TaskReport() {
    }

    // --- Constructor Đầy Đủ ---
    public TaskReport(int reportId, int userId, int taskId, String reportDescription, 
                      Double estimateWorkPercentDone, Double totalHourUsed, 
                      Integer timesheetEntryId, Timestamp createdAt) {
        this.reportId = reportId;
        this.userId = userId;
        this.taskId = taskId;
        this.reportDescription = reportDescription;
        // Dùng setter để validate dữ liệu
        setEstimateWorkPercentDone(estimateWorkPercentDone);
        setTotalHourUsed(totalHourUsed);
        this.timesheetEntryId = timesheetEntryId;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public Double getEstimateWorkPercentDone() {
        return estimateWorkPercentDone;
    }

    // Validate: 0% - 100%
    public void setEstimateWorkPercentDone(Double estimateWorkPercentDone) {
        if (estimateWorkPercentDone != null) {
            if (estimateWorkPercentDone < 0 || estimateWorkPercentDone > 100) {
                throw new IllegalArgumentException("Tiến độ báo cáo phải từ 0% đến 100%. Bạn nhập: " + estimateWorkPercentDone);
            }
        }
        this.estimateWorkPercentDone = estimateWorkPercentDone;
    }

    public Double getTotalHourUsed() {
        return totalHourUsed;
    }

    // Validate: Thời gian không được âm
    public void setTotalHourUsed(Double totalHourUsed) {
        if (totalHourUsed != null && totalHourUsed < 0) {
            throw new IllegalArgumentException("Thời gian sử dụng không được là số âm.");
        }
        this.totalHourUsed = totalHourUsed;
    }

    public Integer getTimesheetEntryId() {
        return timesheetEntryId;
    }

    public void setTimesheetEntryId(Integer timesheetEntryId) {
        this.timesheetEntryId = timesheetEntryId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
