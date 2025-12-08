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
public class TaskAssignee {

    // Primary Key (INT IDENTITY)
    private Integer taskAssigneeId;

    // Required Foreign Keys (INT NOT NULL)
    private Integer taskId;
    private Integer userId;

    // System/Audit Field
    private LocalDateTime assignedAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    public TaskAssignee(Integer taskAssigneeId, Integer taskId, Integer userId, LocalDateTime assignedAt) {
        this.taskAssigneeId = taskAssigneeId;
        this.taskId = taskId;
        this.userId = userId;
        this.assignedAt = assignedAt;
    }

    public TaskAssignee() {
    }

    public Integer getTaskAssigneeId() {
        return taskAssigneeId;
    }

    public void setTaskAssigneeId(Integer taskAssigneeId) {
        this.taskAssigneeId = taskAssigneeId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    
}
