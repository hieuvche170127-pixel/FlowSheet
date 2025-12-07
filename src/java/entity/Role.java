/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
public class Role {
    // Trường RoleID tương ứng với IDENTITY(1,1) PRIMARY KEY
    
    
    private Integer roleId;

    // Trường RoleCode tương ứng với NVARCHAR(30) NOT NULL UNIQUE
    private String roleCode;

    // Trường RoleName tương ứng với NVARCHAR(100) NOT NULL
    private String roleName;
    
    public Role() {
    }

    public Role(Integer roleId, String roleCode, String roleName) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    
}
