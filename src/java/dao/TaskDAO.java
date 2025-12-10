package dao;

import dal.DBContext;
import entity.ProjectTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskDAO extends DBContext{
//
//    public List<ProjectTask> getAllTasksByUserId(int userId) {
//        List<TaskAssignee> list = new ArrayList<>();
//        
//        String sql = """
//            SELECT T.TaskID, T.TaskName, T.TaskCode, T.Status, 
//                   P.ProjectName, P.ProjectID
//            FROM ProjectTask T
//            JOIN TaskAssignee TA ON T.TaskID = TA.TaskID
//            LEFT JOIN Project P ON T.ProjectID = P.ProjectID
//            WHERE TM.UserID = ? AND T.IsActive = 1
//            ORDER BY P.ProjectName, T.TaskName
//        """;
//        
//        try {
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.setInt(1, userId);
//            
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                TaskAssignee t = new TaskAssignee();
//                t.setTaskId(rs.getInt("TaskID"));
//                t.setTaskName(rs.getString("TaskName"));
//                t.setTaskCode(rs.getString("TaskCode"));
//                t.setStatus(rs.getString("Status"));
//                
//                String pjName = rs.getString("ProjectName");
//                t.setProjectName(pjName != null ? pjName : "Cá nhân");
//                t.setProjectId(rs.getInt("ProjectID"));
//                
//                list.add(t);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(TaskDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return list;
//    }
    
}
