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
public class Team {

    private Integer teamId;

    // Required Fields
    private String teamName;     // NVARCHAR(200) NOT NULL

    // Optional Fields
    private String description;  // NVARCHAR(MAX) NULL
    private Integer createdBy;   // INT NULL (UserID of creator)

    // System/Audit Field
    private LocalDateTime createdAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    public Team() {
    }

    public Team(Integer teamId, String teamName, String description, Integer createdBy, LocalDateTime createdAt) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
}
