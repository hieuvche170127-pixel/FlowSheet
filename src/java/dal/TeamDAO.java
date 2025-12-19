package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import entity.Team;
import entity.TeamMember;
import entity.TeamProject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamDAO extends DBContext {

    public boolean createTeamTransaction(Team team, List<TeamMember> members, List<TeamProject> projects) {
        String sqlTeam = "INSERT INTO Team (TeamName, Description, CreatedBy) VALUES (?, ?, ?)";
        String sqlMember = "INSERT INTO TeamMember (TeamID, UserID, RoleInTeam) VALUES (?, ?, ?)";
        String sqlProject = "INSERT INTO TeamProject (TeamID, ProjectID) VALUES (?, ?)";

        try {
            connection.setAutoCommit(false);
            PreparedStatement psTeam = connection.prepareStatement(sqlTeam, Statement.RETURN_GENERATED_KEYS);
            psTeam.setString(1, team.getTeamName());
            psTeam.setString(2, team.getDescription());
            psTeam.setInt(3, team.getCreatedBy());

            if (psTeam.executeUpdate() == 0) {
                throw new SQLException("Creating team failed.");
            }
            int newTeamId = 0;
            try (ResultSet generatedKeys = psTeam.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newTeamId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating team failed, no ID obtained.");
                }
            }

            PreparedStatement psMember = connection.prepareStatement(sqlMember);
            for (TeamMember mem : members) {
                psMember.setInt(1, newTeamId);
                psMember.setInt(2, mem.getUserId());
                psMember.setString(3, mem.getRole());
                psMember.addBatch();
            }
            psMember.executeBatch();

            if (projects != null && !projects.isEmpty()) {
                PreparedStatement psProject = connection.prepareStatement(sqlProject);
                for (TeamProject tp : projects) {
                    psProject.setInt(1, newTeamId);
                    psProject.setInt(2, tp.getProjectId());
                    psProject.addBatch();
                }
                psProject.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            // 3. Nếu có lỗi -> Rollback
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            // 4. Trả lại trạng thái AutoCommit ban đầu (Quan trọng với DBContext kiểu này)
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                // Với kiểu DBContext này, ta thường KHÔNG đóng connection ở đây
                // vì có thể bạn muốn dùng tiếp DAO instance này.
                // Connection sẽ đóng khi Garbage Collector dọn dẹp hoặc bạn tự gọi close.
            } catch (SQLException ex) {
                Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // --------------------- ADD/UPDATE ----------------------------
    public List<Team> findAll() throws SQLException {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT * FROM Team";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Team t = new Team();
                t.setTeamID(rs.getInt("TeamID"));
                t.setTeamName(rs.getString("TeamName"));
                t.setDescription(rs.getString("Description"));
                t.setCreatedBy(rs.getInt("CreatedBy"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(t);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public Team findById(int teamId) throws SQLException {
        String sql = "SELECT * FROM Team WHERE TeamID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Team t = new Team();
                    t.setTeamID(rs.getInt("TeamID"));
                    t.setTeamName(rs.getString("TeamName"));
                    t.setDescription(rs.getString("Description"));
                    t.setCreatedBy(rs.getInt("CreatedBy"));
                    t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    /*
                java.sql.Timestamp createdTs = rs.getTimestamp("CreatedAt");
                t.setCreatedAt(createdATs != null ? createdTs.toLocalDateTime() : null);
                     */
                    return t;
                }
            } catch (SQLException ex) {
                Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    public List<Team> searchByTeamOrMember(String keyword) throws SQLException {
        List<Team> list = new ArrayList<>();
        String sql
                = "SELECT DISTINCT t.* "
                + "FROM Team t "
                + "LEFT JOIN TeamMember tm ON t.TeamID = tm.TeamID "
                + "LEFT JOIN UserAccount ua ON tm.UserID = ua.UserID "
                + "WHERE t.TeamName LIKE ? "
                + "   OR ua.Username LIKE ? "
                + "   OR ua.FullName LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Team t = new Team();
                    t.setTeamID(rs.getInt("TeamID"));
                    t.setTeamName(rs.getString("TeamName"));
                    t.setDescription(rs.getString("Description"));
                    t.setCreatedBy(rs.getInt("CreatedBy"));
                    t.setCreatedAt(rs.getTimestamp("CreatedAt"));

                    list.add(t);
                }
            }
        }
        return list;
    }

    public List<Team> searchByTeamOrMemberForUser(int userId, String keyword) throws SQLException {
        List<Team> list = new ArrayList<>();
        String sql
                = "SELECT DISTINCT t.* "
                + "FROM Team t "
                + "JOIN TeamMember tmMe ON t.TeamID = tmMe.TeamID AND tmMe.UserID = ? "
                + // restrict
                "LEFT JOIN TeamMember tm ON t.TeamID = tm.TeamID "
                + "LEFT JOIN UserAccount ua ON tm.UserID = ua.UserID "
                + "WHERE t.TeamName LIKE ? OR ua.Username LIKE ? OR ua.FullName LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setInt(1, userId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Team t = new Team();
                    t.setTeamID(rs.getInt("TeamID"));
                    t.setTeamName(rs.getString("TeamName"));
                    t.setDescription(rs.getString("Description"));
                    t.setCreatedBy(rs.getInt("CreatedBy"));
                    t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    list.add(t);
                }
            }
        }
        return list;
    }

    private Team mapTeamFromResultSet(ResultSet rs) throws SQLException {
        Team t = null;
        t = new Team();
        // 1. Ánh xạ các trường dữ liệu
        t.setTeamID(rs.getInt("teamID"));
        t.setTeamName(rs.getString("teamName"));
        t.setDescription(rs.getString("description"));
        t.setCreatedBy(rs.getInt("createdBy"));
        t.setCreatedAt(rs.getTimestamp("createdAt"));
        return t;
    }

    public ArrayList<Team> getAllTeamByUserId(int userID) {
        String query = "SELECT t.* FROM Team t\n"
                + "JOIN TeamMember tm ON t.TeamID = tm.TeamID\n"
                + "WHERE tm.UserID = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Team> teamList = new ArrayList<>();
        try {
            // Chuẩn bị và thực thi câu lệnh
            ps = connection.prepareStatement(query);
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            // Lặp qua ResultSet và ánh xạ dữ liệu
            while (rs.next()) {
                // Giả định bạn đã có phương thức mapTeamFromResultSet(ResultSet rs)
                Team team = mapTeamFromResultSet(rs);
                if (team != null) {
                    teamList.add(team);
                }
            }
        } catch (SQLException ex) {
            // Xử lý ngoại lệ SQL (in ra hoặc ghi log)
            Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Đóng ResultSet và PreparedStatement trong khối finally
            // Đảm bảo chúng được đóng ngay cả khi có lỗi xảy ra
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TeamDAO.class.getName()).log(Level.SEVERE, null, ex);

            }
        }

        return teamList;
    }

    public void deleteTeam(int teamId) throws SQLException {
        String sql = "DELETE FROM Team WHERE TeamID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.executeUpdate();
        }
    }

    public boolean updateTeamInfo(int teamId, String teamName, String description) throws SQLException {
        String sql = "UPDATE Team SET TeamName = ?, Description = ? WHERE TeamID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, teamName);
            ps.setString(2, description);
            ps.setInt(3, teamId);
            return ps.executeUpdate() > 0;
        }
    }

}
