/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilities;

import java.util.Date;
//imort java.sql.Date

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalTime;
import java.time.LocalDateTime;


import java.sql.Time;
import java.sql.Timestamp;



/**
 *
 * @author Admin
 */
public class DateTimeConverter {

    private DateTimeConverter() {
    }

    public static LocalDate convertDateToLocalDate(java.util.Date oldDate) {
        if (oldDate == null) {
            return null; // Xử lý trường hợp đầu vào là null
        }
        Instant instant = oldDate.toInstant();
        // 2. Định nghĩa Múi giờ (ZoneId)
        // Múi giờ là cần thiết để chuyển đổi Instant (UTC) sang một ngày cụ thể 
        // (vì ngày có thể khác nhau tùy múi giờ).
        // Ví dụ: ZoneId.systemDefault() lấy múi giờ mặc định của hệ thống đang chạy.
        ZoneId defaultZone = ZoneId.systemDefault();
        // Hoặc dùng: ZoneId.of("Asia/Ho_Chi_Minh");

        // 3. Chuyển Instant sang LocalDate
        LocalDate localDate = instant.atZone(defaultZone)
                .toLocalDate();
        return localDate;
    }
    
    public static LocalDate convertSqlDateToLocalDate(java.sql.Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        
        // Phương thức toLocalDate() là cách chính thức và an toàn nhất 
        // để chuyển đổi java.sql.Date sang LocalDate trong Java 8+.
        return sqlDate.toLocalDate();
    }
    
    public static LocalTime convertSqlTimeToLocalTime(Time sqlTime) {
        if (sqlTime == null) {
            return null;
        }
        
        // Phương thức toLocalTime() là cách chính thức và đơn giản nhất
        return sqlTime.toLocalTime();
    }
    
    public static LocalDateTime convertSqlTimestampToLocalDateTime(Timestamp sqlTimestamp){
        if(sqlTimestamp==null){
            return null;
        }
        
        return sqlTimestamp.toLocalDateTime();
    }
    
    
    

    public static void main(String[] args) {
        Date oldJavaUtilDate = new Date(); // Lấy thời điểm hiện tại

        LocalDate newJavaTimeLocalDate = DateTimeConverter.convertDateToLocalDate(oldJavaUtilDate);

        System.out.println("java.util.Date cũ: " + oldJavaUtilDate);
        System.out.println("java.time.LocalDate mới: " + newJavaTimeLocalDate);
    }
}
