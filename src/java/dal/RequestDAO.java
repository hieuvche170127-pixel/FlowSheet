package dal;

import java.sql.*;
import entity.LeaveRequest;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO extends DBContext{

    public List<LeaveRequest> getLeaveRequests(Integer userId, Integer roleId, String statusFilter) {
        List<LeaveRequest> list = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT l.LeaveID, l.UserID, u.FullName, l.FromDate, l.ToDate, ");
        sql.append("l.Reason, l.Status, l.DurationDays, l.AppliedAt ");
        sql.append("FROM LeaveRequest l ");
        sql.append("JOIN UserAccount u ON l.UserID = u.UserID ");
        sql.append("WHERE 1=1 ");

        if (roleId == 1) {
            sql.append("AND l.UserID = ? ");
            sql.append("AND l.Status != 'WITHDRAWN' "); 
        } 
        else if (roleId == 2) {
            if ("PENDING".equals(statusFilter)) {
                sql.append("AND l.Status = 'PENDING' ");
            }
        }

        sql.append("ORDER BY l.AppliedAt DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            if (roleId == 1) {
                ps.setInt(1, userId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LeaveRequest dto = new LeaveRequest();
                dto.setLeaveId(rs.getInt("LeaveID"));
                dto.setUserId(rs.getInt("UserID"));
                dto.setRequesterName(rs.getString("FullName"));
                
                Date sqlFrom = rs.getDate("FromDate");
                if (sqlFrom != null) dto.setFromDate(sqlFrom.toLocalDate());
                
                Date sqlTo = rs.getDate("ToDate");
                if (sqlTo != null) dto.setToDate(sqlTo.toLocalDate());
                
                dto.setReason(rs.getString("Reason"));
                dto.setStatus(rs.getString("Status"));
                dto.setDurationDays(rs.getInt("DurationDays"));
                
                Timestamp ts = rs.getTimestamp("AppliedAt");
                if (ts != null) dto.setAppliedAt(ts.toLocalDateTime());
                
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void createLeaveRequest(LeaveRequest req) {
        String sql = "INSERT INTO LeaveRequest (UserID, FromDate, ToDate, Reason, DurationDays, Status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, req.getUserId());
            ps.setDate(2, Date.valueOf(req.getFromDate()));
            ps.setDate(3, Date.valueOf(req.getToDate()));
            ps.setNString(4, req.getReason());
            ps.setInt(5, req.getDurationDays());
            
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateRequestStatus(int leaveId, String newStatus, int approverId, Object comment) {
        String sql = "UPDATE LeaveRequest SET Status = ?, ReviewedAt = GETDATE() ";
        
        if (approverId > 0) {
            sql += ", ApproverID = ?, ApproverComment = ? ";
        }
        if ("WITHDRAWN".equals(newStatus)) {
            sql += ", WithdrawnAt = GETDATE() ";
        }
        
        sql += "WHERE LeaveID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setNString(1, newStatus);
            
            int idx = 2;
            if (approverId > 0) {
                ps.setInt(idx++, approverId);
                ps.setNString(idx++, (String) comment);
            }
            
            ps.setInt(idx, leaveId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
