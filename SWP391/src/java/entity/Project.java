/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;
import java.time.LocalDate;


/**
 *
 * @author Admin
 */
public class Project {
    // --- Fields (Trường) ---

    private Integer projectId;      // INT IDENTITY(1,1) PRIMARY KEY
    private String projectCode;     // NVARCHAR(50) NOT NULL UNIQUE
    private String projectName;     // NVARCHAR(200) NOT NULL
    private String description;     // NVARCHAR(MAX) NULL
    private Boolean isActive;       // BIT NOT NULL DEFAULT 1
    private LocalDateTime createdAt;  // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    // Thêm các trường mới
    private LocalDate startDate;      // DATE NULL
    private LocalDate deadline;       // DATE NULL
    private String status;          // NVARCHAR(20) NOT NULL DEFAULT N'OPEN'
    //(Status IN (N'OPEN', N'IN_PROGRESS', N'COMPLETE'));
    // note lại để mng để ý các constraint

    public Project() {
    }
    
    
    
    public Project(Integer projectId, String projectCode, String projectName,
            String description, Boolean isActive,
            LocalDateTime createdAt, LocalDate startDate, LocalDate deadline, String status) {
        this.projectId = projectId;
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
