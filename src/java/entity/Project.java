package entity;

import java.sql.Timestamp;
import java.sql.Date;

public class Project {
    private int projectID;
    private String projectCode;
    private String projectName;
    private String description;
    private boolean isActive;
    private Timestamp createdAt;
    private Date startDate;
    private Date deadline;
    private String status;

    public Project() {
    }

    public Project(int projectID, String projectCode, String projectName, String description, boolean isActive, Timestamp createdAt, Date startDate, Date deadline, String status) {
        this.projectID = projectID;
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
