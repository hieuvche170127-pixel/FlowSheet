package entity;

import java.sql.Timestamp;

public class TeamMember {
    private int teamId;
    private int userId;
    private String role;
    private Timestamp joinedAt;

    public TeamMember() {
    }

    public TeamMember(int teamId, int userId, String role) {
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
    }

    public TeamMember(int teamId, int userId, String role, Timestamp joinedAt) {
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "TeamMember{" + "teamId=" + teamId + ", userId=" + userId + ", role=" + role + '}';
    }
    
    
}
