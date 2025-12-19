/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;
import java.util.UUID; // Dùng cho UNIQUEIDENTIFIER
import java.util.Date; // <--- NHỚ IMPORT CÁI NÀY
/**
 *
 * @author Admin
 */
public class Invitation {
    // Primary Key (INT IDENTITY)
    private Integer invitationId; 

    // Required Fields
    private String email;          // NVARCHAR(200) NOT NULL
    private Integer roleId;        // INT NOT NULL - role for the invited user
    private Integer invitedById;   // INT NOT NULL - who invited
    private UUID token;            // UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID()
    private String status;         // NVARCHAR(20) NOT NULL DEFAULT 'PENDING'
    private LocalDateTime expiresAt; // DATETIME2 NOT NULL
    
    // Optional Fields (Phạm vi của lời mời)
    private Integer projectId;     // INT NULL
    private Integer teamId;        // INT NULL
    
    // System/Audit Fields
    private LocalDateTime createdAt;   // DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    private LocalDateTime acceptedAt;  // DATETIME2 NULL

    public Invitation() {
    }

    public Invitation(Integer invitationId, String email, Integer roleId, Integer invitedById, UUID token, String status, LocalDateTime expiresAt, Integer projectId, Integer teamId, LocalDateTime createdAt, LocalDateTime acceptedAt) {
        this.invitationId = invitationId;
        this.email = email;
        this.roleId = roleId;
        this.invitedById = invitedById;
        this.token = token;
        this.status = status;
        this.expiresAt = expiresAt;
        this.projectId = projectId;
        this.teamId = teamId;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
    }

    public Integer getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Integer invitationId) {
        this.invitationId = invitationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getInvitedById() {
        return invitedById;
    }

    public void setInvitedById(Integer invitedById) {
        this.invitedById = invitedById;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
    
    // các hàm cho jstl
      // 1. Hàm này sửa lỗi dòng 75
    public Date getCreatedAtAsDate() {
        return (createdAt == null) ? null : java.sql.Timestamp.valueOf(createdAt);
    }

    // 2. Hàm này sửa lỗi dòng tương tự tiếp theo
    public Date getExpiresAtAsDate() {
        return (expiresAt == null) ? null : java.sql.Timestamp.valueOf(expiresAt);
    }
    
    // 3. Hàm này dự phòng
    public Date getAcceptedAtAsDate() {
        return (acceptedAt == null) ? null : java.sql.Timestamp.valueOf(acceptedAt);
    }
    
    
}
