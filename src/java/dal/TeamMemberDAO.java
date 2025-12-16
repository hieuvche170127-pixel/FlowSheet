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

    private TeamMember mapTeamMemberFromResultSet(ResultSet rs) throws SQLException {
        TeamMember teamMem = new TeamMember();
        teamMem.setTeamId(rs.getInt("teamId"));
        teamMem.setUserId(rs.getInt("userId"));
        teamMem.setRole(rs.getString("role"));
        teamMem.setJoinedAt(rs.getTimestamp("joinedAt"));
        return teamMem;
    }

    public ArrayList<TeamMember> getAllTeamMembersByTeamId(int teamID) {

        String query = "SELECT tm.* FROM TeamMember tm WHERE tm.TeamID = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<TeamMember> memberList = new ArrayList<>();

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, teamID); // Thiết lập giá trị cho dấu '?' thứ nhất

            rs = ps.executeQuery();

            // Lặp qua ResultSet và ánh xạ dữ liệu
            while (rs.next()) {
                // Sử dụng hàm ánh xạ (đã sửa, không có rs.next() bên trong)
                TeamMember teamMember = mapTeamMemberFromResultSet(rs);
                memberList.add(teamMember);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng ResultSet và PreparedStatement
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return memberList;
    }

    public boolean isMemberExistInTeam(int checkedUserId, int checkedTeamId) {
        boolean result = false;
        try {
            // Query đếm số dòng có TeamID và UserID khớp
            String sql = "SELECT COUNT(*) FROM TeamMember WHERE TeamID = ? AND UserID = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            // Lưu ý thứ tự set param nhé anh:
            // ? thứ 1 là TeamID
            ps.setInt(1, checkedTeamId);
            // ? thứ 2 là UserID
            ps.setInt(2, checkedUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1); // Lấy giá trị của cột COUNT(*)
                if (count > 0) {
                    result = true; // Đã tồn tại
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
