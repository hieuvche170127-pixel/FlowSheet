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

    /**
     * Lấy thông tin thành viên trong nhóm dựa trên UserID và TeamID.
     *
     * @param userID ID của người dùng.
     * @param teamID ID của nhóm.
     * @return TeamMember chứa thông tin thành viên (mapping RoleID sang String
     * role). Logic: RoleID = 4 -> "Team Member", còn lại -> "Team Leader".
     */
    public TeamMember getTeamMemberByUserIDAndTeamId(int userID, int teamID) {
        TeamMember teammem = null;

        // Chỉ lấy những cột cần thiết
        String query = "SELECT TeamID, UserID, RoleID FROM TeamMember WHERE UserID = ? AND TeamID = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userID);
            ps.setInt(2, teamID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    teammem = new TeamMember();

                    // 1. Map ID cơ bản
                    teammem.setTeamId(rs.getInt("TeamID"));
                    teammem.setUserId(rs.getInt("UserID"));

                    // 2. Xử lý Logic Role (Int -> String) theo yêu cầu
                    int dbRoleId = rs.getInt("RoleID");

                    if (dbRoleId == 4) {
                        teammem.setRole("Team Member");
                    } else {
                        // Trường hợp còn lại (RoleID = 5 hoặc khác 4) coi là Leader
                        teammem.setRole("Team Leader");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teammem;
    }

    public void deleteByTeam(int teamId) throws SQLException {
        String sql = "DELETE FROM TeamMember WHERE TeamID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.executeUpdate();
        }
    }

    public boolean kickMember(int teamId, int userId) throws SQLException {
        String sql = "DELETE FROM TeamMember WHERE TeamID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean changeRole(int teamId, int userId, int newRoleId) throws SQLException {
        // NewSQL chặn RoleID chỉ được 4 hoặc 5
        if (newRoleId != 4 && newRoleId != 5) {
            throw new IllegalArgumentException("RoleID must be 4 (Team Member) or 5 (Team Leader)");
        }

        String sql = "UPDATE TeamMember SET RoleID = ? WHERE TeamID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newRoleId);
            ps.setInt(2, teamId);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public Integer getTeamRoleId(int teamId, int userId) throws SQLException {
        String sql = "SELECT RoleID FROM TeamMember WHERE TeamID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("RoleID") : null;
            }
        }
    }

    public Integer getLeaderUserId(int teamId) throws SQLException {
        String sql = "SELECT UserID FROM TeamMember WHERE TeamID = ? AND RoleID = 5";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("UserID") : null;
            }
        }
    }

// Promote a user to leader and ensure only 1 leader exists (transaction)
    public boolean makeLeaderExclusive(int teamId, int newLeaderUserId) throws SQLException {
        String demoteOld = "UPDATE TeamMember SET RoleID = 4 WHERE TeamID = ? AND RoleID = 5";
        String promoteNew = "UPDATE TeamMember SET RoleID = 5 WHERE TeamID = ? AND UserID = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps1 = connection.prepareStatement(demoteOld)) {
                ps1.setInt(1, teamId);
                ps1.executeUpdate();
            }

            int promoted;
            try (PreparedStatement ps2 = connection.prepareStatement(promoteNew)) {
                ps2.setInt(1, teamId);
                ps2.setInt(2, newLeaderUserId);
                promoted = ps2.executeUpdate();
            }

            connection.commit();
            return promoted > 0;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
