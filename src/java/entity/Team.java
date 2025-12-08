<<<<<<< HEAD
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
=======
package entity;

import java.sql.Timestamp;

public class Team {
    private int teamID;
    private String teamName;
    private String description;
    private int createdBy; 
    private Timestamp createdAt;
>>>>>>> 6583d8c87ba9c50e083166b6b2e29e0f7f8434b7

    public Team() {
    }

<<<<<<< HEAD
    public Team(Integer teamId, String teamName, String description, Integer createdBy, LocalDateTime createdAt) {
        this.teamId = teamId;
=======
    public Team(String teamName, String description, int createdBy) {
        this.teamName = teamName;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Team(int teamID, String teamName, String description, int createdBy, Timestamp createdAt) {
        this.teamID = teamID;
>>>>>>> 6583d8c87ba9c50e083166b6b2e29e0f7f8434b7
        this.teamName = teamName;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

<<<<<<< HEAD
    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
=======
    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
>>>>>>> 6583d8c87ba9c50e083166b6b2e29e0f7f8434b7
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

<<<<<<< HEAD
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

=======
    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
>>>>>>> 6583d8c87ba9c50e083166b6b2e29e0f7f8434b7
    
}
