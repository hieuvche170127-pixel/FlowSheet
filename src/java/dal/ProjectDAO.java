package dal;

import dao.UserDAO;
import entity.Project;
import entity.Team;
import entity.UserAccount;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectDAO extends DBContext {

    public List<Project> getAllProjectsForTeam() {
        List<Project> list = new ArrayList<>();

        String sql = "SELECT * FROM Project WHERE IsActive = 1 AND Status IN ('OPEN', 'IN_PROGRESS')";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Project p = new Project();
                p.setProjectID(rs.getInt("ProjectID"));
                p.setProjectCode(rs.getString("ProjectCode"));
                p.setProjectName(rs.getString("ProjectName"));
                p.setStatus(rs.getString("Status"));

                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    //--------------- ADD/UPDATE -------------------
    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM Project";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Project p = new Project();
                p.setProjectID(rs.getInt("ProjectID"));
                p.setProjectCode(rs.getString("ProjectCode"));
                p.setProjectName(rs.getString("ProjectName"));
                p.setDescription(rs.getString("Description"));
                p.setIsActive(rs.getBoolean("IsActive"));
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));

                java.sql.Date start = rs.getDate("StartDate");
                p.setStartDate(start); // ts will be null safely if DB value is null

                java.sql.Date end = rs.getDate("Deadline");
                p.setStartDate(end); // ts will be null safely if DB value is null

                p.setStatus(rs.getString("Status"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<String> findProjectCodesByTeam(int teamId) {
        List<String> codes = new ArrayList<>();

        String sql = """
        SELECT p.ProjectCode
        FROM TeamProject tp
        JOIN Project p ON tp.ProjectID = p.ProjectID
        WHERE tp.TeamID = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codes.add(rs.getString("ProjectCode"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codes;
    }

    public List<Project> findProjectsByTeam(int teamId) throws SQLException {
        List<Project> list = new ArrayList<>();

        String sql
                = "SELECT p.* "
                + "FROM TeamProject tp "
                + "JOIN Project p ON tp.ProjectID = p.ProjectID "
                + "WHERE tp.TeamID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Project p = new Project();
                    p.setProjectID(rs.getInt("ProjectID"));
                    p.setProjectCode(rs.getString("ProjectCode"));
                    p.setProjectName(rs.getString("ProjectName"));
                    p.setDescription(rs.getString("Description"));
                    p.setIsActive(rs.getBoolean("IsActive"));
                    // set other fields if your entity has them (Status, CreatedAt, etc.)

                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<Project> searchProjects(Integer userId, Integer roleId, String keyword, String status) {
        List<Project> projectList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT p.ProjectID, p.ProjectCode, p.ProjectName, ");
        sql.append("p.Description, p.StartDate, p.Deadline, p.Status ");
        sql.append("FROM Project p ");
        
        boolean isAdmin = (roleId == 3 || roleId == 2);
        
        if (!isAdmin) {
            sql.append("JOIN TeamProject tp ON p.ProjectID = tp.ProjectID ");
            sql.append("JOIN TeamMember tm ON tp.TeamID = tm.TeamID ");
        }
        
        sql.append("WHERE 1=1 ");
        
        // Filter theo Status
        // Nếu status là "Active" (mặc định của Controller), ta lấy các trạng thái không phải COMPLETE/CANCEL
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
            if (status.equalsIgnoreCase("Active")) {
                sql.append("AND p.Status IN ('OPEN', 'IN_PROGRESS') ");
            } else {
                sql.append("AND p.Status = ? ");
            }
        }
        
        // Filter theo Keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.ProjectName LIKE ? OR p.ProjectCode LIKE ?) ");
        }
        
        // Filter theo User Permission (Nếu không phải Admin)
        if (!isAdmin) {
            sql.append("AND tm.UserID = ? ");
        }
        
        sql.append("ORDER BY p.CreatedAt DESC"); // Sắp xếp dự án mới nhất lên đầu

        try (
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            
            int index = 1;
            
            // Set params cho Status
            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("Active")) {
                ps.setString(index++, status);
            }
            
            // Set params cho Keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
            }
            
            // Set params cho UserID
            if (!isAdmin) {
                ps.setInt(index++, userId);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Project project = new Project();
                    project.setProjectID(rs.getInt("ProjectID"));
                    project.setProjectCode(rs.getString("ProjectCode"));
                    project.setProjectName(rs.getString("ProjectName"));
                    project.setDescription(rs.getString("Description"));
                    project.setStartDate(rs.getDate("StartDate"));
                    project.setDeadline(rs.getDate("Deadline"));
                    project.setStatus(rs.getString("Status"));
                    
                    projectList.add(project);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nên dùng Logger trong thực tế
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return projectList;
    }

    public Project getProjectById(int projectId) {
        String sql = "SELECT ProjectID, ProjectCode, ProjectName, Description, "
                + "StartDate, Deadline, Status, IsActive "
                + "FROM Project "
                + "WHERE ProjectID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Project p = new Project();
                    p.setProjectID(rs.getInt("ProjectID"));
                    p.setProjectCode(rs.getString("ProjectCode"));
                    p.setProjectName(rs.getString("ProjectName"));
                    p.setDescription(rs.getString("Description"));
                    p.setStartDate(rs.getDate("StartDate"));
                    p.setDeadline(rs.getDate("Deadline"));
                    p.setStatus(rs.getString("Status"));
                    p.setIsActive(rs.getBoolean("IsActive"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserAccount> getMembersInProject(int projectId) {
        List<UserAccount> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.Username, u.FullName, u.Email, pm.RoleInProject " +
                     "FROM UserAccount u " +
                     "JOIN ProjectMember pm ON u.UserID = pm.UserID " +
                     "WHERE pm.ProjectID = ?";
                     
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, projectId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserAccount u = new UserAccount();
                    u.setUserID(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setFullName(rs.getString("FullName"));
                    u.setEmail(rs.getString("Email"));
                    u.setRoleInProject(rs.getString("RoleInProject")); 
                    
                    list.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateProjectInfo(Project p) {
        String sql = "UPDATE Project SET ProjectName = ?, StartDate = ?, Deadline = ?, "
                   + "Status = ?, Description = ? WHERE ProjectID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, p.getProjectName());
            ps.setDate(2, p.getStartDate()); 
            ps.setDate(3, p.getDeadline());           
            ps.setString(4, p.getStatus());
            ps.setString(5, p.getDescription());
            ps.setInt(6, p.getProjectID());
            
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMemberToProject(int projectId, int userId, String role) {
        String roleToInsert = (role == null || role.isEmpty()) ? "Member" : role;

        String sql = "INSERT INTO ProjectMember (ProjectID, UserID, RoleInProject) VALUES (?, ?, ?)";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            ps.setString(3, roleToInsert);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("User " + userId + " đã tồn tại trong Project " + projectId + ". Bỏ qua.");
        }
    }

    public void updateMemberRole(int projectId, int uid, String newRole) {
        String sql = "UPDATE ProjectMember SET RoleInProject = ? WHERE ProjectID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, newRole);
            ps.setInt(2, projectId);
            ps.setInt(3, uid);
            
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeMemberFromProject(int projectId, int uid) {
        String sql = "DELETE FROM ProjectMember WHERE ProjectID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, projectId);
            ps.setInt(2, uid);
            
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deactiveProject(int projectId, boolean isActive) {
        String sql = "UPDATE Project SET IsActive = ? WHERE ProjectID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, isActive); // true = 1, false = 0
            ps.setInt(2, projectId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserAccount> getProjectMembers(int projectId) {
        List<UserAccount> list = new ArrayList<>();

        String sql = "SELECT u.UserID, u.Username, u.FullName, u.Email, pm.RoleInProject "
                + "FROM UserAccount u "
                + "JOIN ProjectMember pm ON u.UserID = pm.UserID "
                + "WHERE pm.ProjectID = ? AND u.IsActive = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserAccount u = new UserAccount();
                    u.setUserID(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setFullName(rs.getString("FullName"));
                    u.setEmail(rs.getString("Email"));
                    u.setRoleInProject(rs.getString("RoleInProject"));
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countProjects(Integer userId, Integer roleId, String keyword, String status) {
        int count = 0;
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT COUNT(DISTINCT p.ProjectID) ");
        sql.append("FROM Project p ");

        boolean isAdmin = (roleId == 3 || roleId == 2);
        if (!isAdmin) {
            sql.append("JOIN TeamProject tp ON p.ProjectID = tp.ProjectID ");
            sql.append("JOIN TeamMember tm ON tp.TeamID = tm.TeamID ");
        }

        sql.append("WHERE 1=1 ");

        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
            if (status.equalsIgnoreCase("Active")) {
                sql.append("AND p.Status IN ('OPEN', 'IN_PROGRESS') ");
            } else {
                sql.append("AND p.Status = ? ");
            }
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.ProjectName LIKE ? OR p.ProjectCode LIKE ?) ");
        }

        if (!isAdmin) {
            sql.append("AND tm.UserID = ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int index = 1;

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("Active")) {
                ps.setString(index++, status);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
            }

            if (!isAdmin) {
                ps.setInt(index++, userId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Project> searchProjectsWithPaging(Integer userId, Integer roleId, String keyword, String status, int pageIndex, int pageSize) {
        List<Project> projectList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT p.ProjectID, p.ProjectCode, p.ProjectName, ");
        sql.append("p.Description, p.StartDate, p.Deadline, p.Status, p.CreatedAt ");
        sql.append("FROM Project p ");

        boolean isAdmin = (roleId == 3 || roleId == 2);
        if (!isAdmin) {
            sql.append("JOIN TeamProject tp ON p.ProjectID = tp.ProjectID ");
            sql.append("JOIN TeamMember tm ON tp.TeamID = tm.TeamID ");
        }

        sql.append("WHERE 1=1 AND IsActive = 1");

        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
            if (status.equalsIgnoreCase("Active")) {
                sql.append("AND p.Status IN ('OPEN', 'IN_PROGRESS') ");
            } else {
                sql.append("AND p.Status = ? ");
            }
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.ProjectName LIKE ? OR p.ProjectCode LIKE ?) ");
        }

        if (!isAdmin) {
            sql.append("AND tm.UserID = ? ");
        }

        sql.append("ORDER BY p.CreatedAt DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int index = 1;

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("Active")) {
                ps.setString(index++, status);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                ps.setString(index++, searchPattern);
                ps.setString(index++, searchPattern);
            }

            if (!isAdmin) {
                ps.setInt(index++, userId);
            }
            int offset = (pageIndex - 1) * pageSize;
            ps.setInt(index++, offset);

            ps.setInt(index++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Project p = new Project();
                    p.setProjectID(rs.getInt("ProjectID"));
                    p.setProjectCode(rs.getString("ProjectCode"));
                    p.setProjectName(rs.getString("ProjectName"));
                    p.setDescription(rs.getString("Description"));
                    p.setStartDate(rs.getDate("StartDate"));
                    p.setDeadline(rs.getDate("Deadline"));
                    p.setStatus(rs.getString("Status"));

                    projectList.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projectList;
    }

    public List<Team> getAllTeamsForProject() {
        List<Team> list = new ArrayList<>();

        String sql = "SELECT TeamID, TeamName FROM Team";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Team t = new Team();
                t.setTeamID(rs.getInt("TeamID"));
                t.setTeamName(rs.getString("TeamName"));
                list.add(t);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<UserAccount> getAllActiveMembers() {
        List<UserAccount> list = new ArrayList<>();

        String sql = "SELECT UserID, FullName, Email FROM UserAccount WHERE IsActive = 1 AND RoleID = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserAccount u = new UserAccount();
                u.setUserID(rs.getInt("UserID"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                list.add(u);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int createProject(Project p) {
        String sql = "INSERT INTO Project (ProjectCode, ProjectName, Description, StartDate, Deadline, Status) VALUES (?, ?, ?, ?, ?, ?)";
        try (
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getProjectCode());
            ps.setString(2, p.getProjectName());
            ps.setString(3, p.getDescription());
            ps.setDate(4, p.getStartDate());
            ps.setDate(5, p.getDeadline());
            ps.setString(6, "OPEN"); // Mặc định status

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addTeamToProject(int projectId, int teamId) {
        String sql = "INSERT INTO TeamProject (ProjectID, TeamID) VALUES (?, ?)";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, teamId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void importMembersFromTeam(int projectId, int teamId) {
        String sql = "INSERT INTO ProjectMember (ProjectID, UserID, RoleInProject) "
                + "SELECT ?, UserID, RoleInTeam FROM TeamMember WHERE TeamID = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, teamId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // phần dưới là nghĩa code nhé :)))
      /**
     * Map dữ liệu từ ResultSet sang Project object
     *
     * @param rs - ResultSet từ query
     * @return Project object đã được map đầy đủ dữ liệu
     * @throws Exception
     */
    private Project mapProjectFromResultSet(ResultSet rs) throws Exception {
        Project project = null;
        try {
            project = new Project();

            // 1. Primary Key
            project.setProjectID(rs.getInt("projectID"));

            // 2. Basic Information
            project.setProjectCode(rs.getString("projectCode"));
            project.setProjectName(rs.getString("projectName"));
            project.setDescription(rs.getString("description"));

            // 3. Boolean Field
            project.setIsActive(rs.getBoolean("isActive"));

            // 4. Date Fields
            // startDate và deadline là java.sql.Date -> convert sang LocalDate nếu cần
            project.setStartDate(rs.getDate("startDate"));
            project.setDeadline(rs.getDate("deadline"));

            // 5. Timestamp Field
            project.setCreatedAt(rs.getTimestamp("createdAt"));

            // 6. Status Field
            project.setStatus(rs.getString("status"));

        } catch (Exception e) {
            throw e;
        }
        return project;
    }

    public ArrayList<Project> getAllProject() {
        ArrayList<Project> list = new ArrayList<>();
        try {
            String sql = "select * from project";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Project p = mapProjectFromResultSet(rs);
                list.add(p);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Project> getAllProjects() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM Project";

        try (PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Project p = new Project();
                p.setProjectID(rs.getInt("ProjectID"));
                p.setProjectCode(rs.getString("ProjectCode"));
                p.setProjectName(rs.getString("ProjectName"));
                p.setDescription(rs.getString("Description"));
                p.setIsActive(rs.getBoolean("IsActive"));
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));
                p.setStartDate(rs.getDate("StartDate"));
                p.setDeadline(rs.getDate("Deadline"));
                p.setStatus(rs.getString("Status"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public ArrayList<Project> getALlProjectUserWithIdInvolve(int userId) {
        ArrayList<Project> list = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT p.*\n"
                    + "FROM Project p\n"
                    + "INNER JOIN TeamProject tp ON p.ProjectID = tp.ProjectID\n"
                    + "INNER JOIN Team t ON tp.TeamID = t.TeamID\n"
                    + "INNER JOIN TeamMember tm ON t.TeamID = tm.TeamID\n"
                    + "WHERE tm.UserID = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Project p = mapProjectFromResultSet(rs);
                list.add(p);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

        public List<Project> getAllProjects() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM Project";

        try (PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Project p = new Project();
                p.setProjectID(rs.getInt("ProjectID"));
                p.setProjectCode(rs.getString("ProjectCode"));
                p.setProjectName(rs.getString("ProjectName"));
                p.setDescription(rs.getString("Description"));
                p.setIsActive(rs.getBoolean("IsActive"));
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));
                p.setStartDate(rs.getDate("StartDate"));
                p.setDeadline(rs.getDate("Deadline"));
                p.setStatus(rs.getString("Status"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProjectDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}
