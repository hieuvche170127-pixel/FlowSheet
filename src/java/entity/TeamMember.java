package entity;

import java.sql.Timestamp;

public class TeamMember {
    private int teamId;
    private int userId;
    private int roleId;
    private Timestamp joinedAt;

    public TeamMember(int teamId, int userId, int roleId) {
        this.teamId = teamId;
        this.userId = userId;
        this.roleId = roleId;
    }

    public TeamMember(int teamId, int userId, int roleId, Timestamp joinedAt) {
        this.teamId = teamId;
        this.userId = userId;
        this.roleId = roleId;
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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "TeamMember{" + "teamId=" + teamId + ", userId=" + userId + ", roleId=" + roleId + '}';
    }

   
    
    
}
