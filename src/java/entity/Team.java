package entity;

import java.sql.Timestamp;

public class Team {
    private int teamID;
    private String teamName;
    private String description;
    private int createdBy; 
    private Timestamp createdAt;

    public Team() {
    }

    public Team(String teamName, String description, int createdBy) {
        this.teamName = teamName;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Team(int teamID, String teamName, String description, int createdBy, Timestamp createdAt) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
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
    
    
}
