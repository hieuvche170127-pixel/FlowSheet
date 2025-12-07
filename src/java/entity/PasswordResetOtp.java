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
public class PasswordResetOtp {

    private Integer otpId;

    // Required Fields
    private Integer userId;     // INT NOT NULL
    private String otpCode;     // NVARCHAR(10) NOT NULL
    private LocalDateTime expiresAt; // DATETIME2 NOT NULL
    private Boolean isUsed;     // BIT NOT NULL DEFAULT 0

    // System/Audit Field
    private LocalDateTime createdAt; // DATETIME2 NOT NULL DEFAULT SYSDATETIME()

    public PasswordResetOtp() {
    }

    public PasswordResetOtp(Integer otpId, Integer userId, String otpCode, LocalDateTime expiresAt, Boolean isUsed, LocalDateTime createdAt) {
        this.otpId = otpId;
        this.userId = userId;
        this.otpCode = otpCode;
        this.expiresAt = expiresAt;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
    }

    public Integer getOtpId() {
        return otpId;
    }

    public void setOtpId(Integer otpId) {
        this.otpId = otpId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    

}
