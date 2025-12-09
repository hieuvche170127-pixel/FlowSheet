package entity;

import java.sql.Timestamp;


public class ProjectMember {
    private int projectId;
    private int userId;
    private String roleInProject;
    private Timestamp joinedAt;
    
    private String userName; 
    private String userEmail;

    public ProjectMember() {
    }

    public ProjectMember(int projectId, int userId, String roleInProject) {
        this.projectId = projectId;
        this.userId = userId;
        this.roleInProject = roleInProject;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(String roleInProject) {
        this.roleInProject = roleInProject;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
}
