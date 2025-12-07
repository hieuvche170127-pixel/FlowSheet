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
public class TeamMember {

    private Integer teamMemberId;

    // Required Foreign Keys (INT NOT NULL)
    private Integer teamId;
    private Integer userId;

    // Optional Fields
    private String roleInTeam;     // NVARCHAR(50) NULL (e.g., 'Leader', 'Member')

    // System/Audit Field
    private LocalDateTime joinedAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    public TeamMember() {
    }

    public TeamMember(Integer teamMemberId, Integer teamId, Integer userId, String roleInTeam, LocalDateTime joinedAt) {
        this.teamMemberId = teamMemberId;
        this.teamId = teamId;
        this.userId = userId;
        this.roleInTeam = roleInTeam;
        this.joinedAt = joinedAt;
    }

    public Integer getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(Integer teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRoleInTeam() {
        return roleInTeam;
    }

    public void setRoleInTeam(String roleInTeam) {
        this.roleInTeam = roleInTeam;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    
}
