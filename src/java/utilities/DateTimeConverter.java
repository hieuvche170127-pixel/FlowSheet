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
    

}
