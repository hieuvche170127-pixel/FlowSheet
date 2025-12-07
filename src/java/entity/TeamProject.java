package entity;

import java.sql.Timestamp;

public class TeamProject {
    private int teamProjectId;
    private int teamId;
    private int projectId;
    private Timestamp assignedAt;

    public TeamProject() {
    }

    public TeamProject(int teamId, int projectId) {
        this.teamId = teamId;
        this.projectId = projectId;
    }

    public TeamProject(int teamProjectId, int teamId, int projectId, Timestamp assignedAt) {
        this.teamProjectId = teamProjectId;
        this.teamId = teamId;
        this.projectId = projectId;
        this.assignedAt = assignedAt;
    }

    public int getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(int teamProjectId) {
        this.teamProjectId = teamProjectId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Timestamp getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Timestamp assignedAt) {
        this.assignedAt = assignedAt;
    }
    
}
