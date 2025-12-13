package dal;

import dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entity.TeamMember;
import java.security.Timestamp;


public class TeamMemberDAO extends DBContext {

    public List<TeamMember> findAll() throws SQLException {
        List<TeamMember> list = new ArrayList<>();
        String sql = "SELECT * FROM TeamMember";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql); // use inherited 'connection'
            rs = ps.executeQuery();
            while (rs.next()) {
                TeamMember tm = new TeamMember();
                tm.setTeamId(rs.getInt("TeamID"));
                tm.setUserId(rs.getInt("UserID"));
                tm.setRole(rs.getString("RoleInTeam"));

                java.sql.Timestamp ts = rs.getTimestamp("JoinedAt");
                tm.setJoinedAt(ts); // ts will be null safely if DB value is null
                
                list.add(tm);
            }
        } finally {
            // Close only PS and RS — NOT the connection (shared by DBContext)
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return list;
    }
}
