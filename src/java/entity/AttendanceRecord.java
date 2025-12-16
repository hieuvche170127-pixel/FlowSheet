/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;
import java.sql.Date;
/**
 *
 * @author nghia ocho
 * class này dùng để lưu lại ai đi vào ngày nào
 * // giả sử đi làm ngày hôm qua thì add vào - thuần CRUD, hoàn toàn ko có cơ chế pending chờ duyệt
 * 
 */

public class AttendanceRecord {
    private Integer attendanceID; // Sử dụng Integer để có thể nhận giá trị NULL ban đầu
    private Integer userID;
    private Date attendanceDate; 

    public AttendanceRecord(Integer attendanceID, Integer userID, Date attendanceDate) {
        this.attendanceID = attendanceID;
        this.userID = userID;
        this.attendanceDate = attendanceDate;
    }

    public AttendanceRecord() {
    }

    public Integer getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(Integer attendanceID) {
        this.attendanceID = attendanceID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
    
    
}
