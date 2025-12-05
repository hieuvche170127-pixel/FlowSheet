/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class ProjectTask {

    private Integer taskId;         // INT IDENTITY(1,1) PRIMARY KEY (Integer là Wrapper Class của int)
    private Integer projectId;      // INT NOT NULL
    private String taskCode;    // NVARCHAR(50) NOT NULL UNIQUE
    private String taskName;    // NVARCHAR(200) NOT NULL
    private String description; // NVARCHAR(MAX) NULL (String có thể là null)
    private Boolean isActive;   // BIT NOT NULL DEFAULT 1 (Boolean là Wrapper Class của boolean)
    private LocalDateTime createdAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    private String status;      // NVARCHAR(20) NOT NULL DEFAULT N'TO_DO'
    //CHECK (Status IN (N'TO_DO', N'COMPLETE'));

    public ProjectTask() {
    }

    
    public ProjectTask(Integer taskId, Integer projectId, String taskCode, String taskName, String description, Boolean isActive, LocalDateTime createdAt, String status) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.taskCode = taskCode;
        this.taskName = taskName;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
