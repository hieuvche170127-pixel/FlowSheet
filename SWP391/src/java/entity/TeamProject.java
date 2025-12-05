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
public class TeamProject {
    // Primary Key (INT IDENTITY)
    private Integer teamProjectId; 

    // Required Foreign Keys (INT NOT NULL)
    private Integer teamId; 
    private Integer projectId; 
    
    // System/Audit Field
    private LocalDateTime assignedAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    public TeamProject() {
    }

    public TeamProject(Integer teamProjectId, Integer teamId, Integer projectId, LocalDateTime assignedAt) {
        this.teamProjectId = teamProjectId;
        this.teamId = teamId;
        this.projectId = projectId;
        this.assignedAt = assignedAt;
    }

    public Integer getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(Integer teamProjectId) {
        this.teamProjectId = teamProjectId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    
    
}
