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
public class UserAccount {

    private Integer userId; // INT IDENTITY(1,1) PRIMARY KEY
    private String username; // NVARCHAR(50) NOT NULL UNIQUE
    private String passwordHash; // NVARCHAR(255) NOT NULL
    private String fullName; // NVARCHAR(100) NOT NULL
    private String email; // NVARCHAR(100) NULL
    private String phone; // NVARCHAR(20) NULL
    private Integer roleId; // INT NOT NULL (Foreign Key)
    private Boolean isActive; // BIT NOT NULL DEFAULT 1
    private LocalDateTime createdAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    private LocalDateTime updatedAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    public UserAccount() {
    }

    // ôi vl nó quên return type :)
    public void User(Integer userId, String username, String passwordHash, String fullName, String email, String phone, Integer roleId, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getters và Setters ---
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
