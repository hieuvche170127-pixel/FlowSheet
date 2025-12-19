package dal;

import entity.ProjectTask;
import entity.TaskReview;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskReviewDAO extends DBContext {

    public boolean create(TaskReview review) {
        if (connection == null) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }

        String sql = """
            INSERT INTO TaskReview (TaskID, ReviewedBy, EstimateWorkPercentDone, ReviewComment)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getTaskId());
            ps.setInt(2, review.getReviewedBy());

            // entity setter already validates 0..100 :contentReference[oaicite:3]{index=3}
            if (review.getEstimateWorkPercentDone() == null) {
                ps.setDouble(3, 0.0);
            } else {
                ps.setDouble(3, review.getEstimateWorkPercentDone());
            }

            if (review.getReviewComment() == null || review.getReviewComment().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(4, review.getReviewComment());
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        review.setReviewId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Error creating TaskReview", ex);
        }
        return false;
    }

    public List<TaskReview> listByTaskId(int taskId) {
        List<TaskReview> list = new ArrayList<>();

        if (connection == null) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return list;
        }

        String sql = """
            SELECT ReviewID, TaskID, ReviewedBy, EstimateWorkPercentDone, ReviewComment, DateCreated
            FROM TaskReview
            WHERE TaskID = ?
            ORDER BY DateCreated DESC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskReview r = new TaskReview();
                    r.setReviewId(rs.getInt("ReviewID"));
                    r.setTaskId(rs.getInt("TaskID"));
                    r.setReviewedBy(rs.getInt("ReviewedBy"));
                    r.setEstimateWorkPercentDone(rs.getObject("EstimateWorkPercentDone", Double.class));
                    r.setReviewComment(rs.getString("ReviewComment"));
                    r.setDateCreated(rs.getTimestamp("DateCreated"));
                    list.add(r);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Error listing TaskReview by taskId", ex);
        }

        return list;
    }

    /**
     * ownership-enforced: only the reviewer can edit/delete
     */
    public TaskReview getByIdAndReviewer(int reviewId, int reviewerId) {
        if (connection == null) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return null;
        }

        String sql = """
            SELECT ReviewID, TaskID, ReviewedBy, EstimateWorkPercentDone, ReviewComment, DateCreated
            FROM TaskReview
            WHERE ReviewID = ? AND ReviewedBy = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.setInt(2, reviewerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaskReview r = new TaskReview();
                    r.setReviewId(rs.getInt("ReviewID"));
                    r.setTaskId(rs.getInt("TaskID"));
                    r.setReviewedBy(rs.getInt("ReviewedBy"));
                    r.setEstimateWorkPercentDone(rs.getObject("EstimateWorkPercentDone", Double.class));
                    r.setReviewComment(rs.getString("ReviewComment"));
                    r.setDateCreated(rs.getTimestamp("DateCreated"));
                    return r;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Error get TaskReview by id & reviewer", ex);
        }

        return null;
    }

    public boolean update(TaskReview review) {
        if (connection == null) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }

        String sql = """
            UPDATE TaskReview
            SET EstimateWorkPercentDone = ?, ReviewComment = ?
            WHERE ReviewID = ? AND ReviewedBy = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            if (review.getEstimateWorkPercentDone() == null) {
                ps.setDouble(1, 0.0);
            } else {
                ps.setDouble(1, review.getEstimateWorkPercentDone());
            }

            if (review.getReviewComment() == null || review.getReviewComment().trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(2, review.getReviewComment());
            }

            ps.setInt(3, review.getReviewId());
            ps.setInt(4, review.getReviewedBy());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Error updating TaskReview", ex);
        }

        return false;
    }

    public boolean delete(int reviewId, int reviewerId) {
        if (connection == null) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Database connection is null");
            return false;
        }

        String sql = "DELETE FROM TaskReview WHERE ReviewID = ? AND ReviewedBy = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.setInt(2, reviewerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "Error deleting TaskReview", ex);
        }

        return false;
    }

    public List<ProjectTask> listAllTasks() {
        List<ProjectTask> list = new ArrayList<>();
        String sql = "SELECT TaskID, TaskName FROM ProjectTask ORDER BY TaskID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProjectTask t = new ProjectTask();
                t.setTaskId(rs.getInt("TaskID"));
                t.setTaskName(rs.getString("TaskName"));
                list.add(t);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "listAllTasks", ex);
        }
        return list;
    }

    public List<TaskReview> listReviews(Integer taskId, String q, int reviewerId) {
        List<TaskReview> list = new ArrayList<>();
        String sql = """
        SELECT ReviewID, TaskID, ReviewedBy, EstimateWorkPercentDone, ReviewComment, DateCreated
        FROM TaskReview
        WHERE ReviewedBy = ?
          AND (? IS NULL OR TaskID = ?)
          AND (? IS NULL OR ReviewComment LIKE ?)
        ORDER BY DateCreated DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            ps.setInt(idx++, reviewerId);

            if (taskId == null || taskId <= 0) {
                ps.setObject(idx++, null);
                ps.setObject(idx++, null);
            } else {
                ps.setInt(idx++, taskId);
                ps.setInt(idx++, taskId);
            }

            if (q == null || q.isBlank()) {
                ps.setObject(idx++, null);
                ps.setObject(idx++, null);
            } else {
                ps.setString(idx++, q);
                ps.setString(idx++, "%" + q.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskReview r = new TaskReview();
                    r.setReviewId(rs.getInt("ReviewID"));
                    r.setTaskId(rs.getInt("TaskID"));
                    r.setReviewedBy(rs.getInt("ReviewedBy"));
                    r.setEstimateWorkPercentDone(rs.getObject("EstimateWorkPercentDone", Double.class));
                    r.setReviewComment(rs.getString("ReviewComment"));
                    r.setDateCreated(rs.getTimestamp("DateCreated"));
                    list.add(r);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "listReviews", ex);
        }
        return list;
    }

    // Return only tasks that have at least 1 review by this supervisor
    public List<ProjectTask> listReviewedTasksBySupervisor(int reviewerId) {
        List<ProjectTask> list = new ArrayList<>();

        String sql = """
        SELECT DISTINCT pt.TaskID, pt.TaskName
        FROM ProjectTask pt
        INNER JOIN TaskReview tr ON tr.TaskID = pt.TaskID
        WHERE tr.ReviewedBy = ?
        ORDER BY pt.TaskID DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reviewerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProjectTask t = new ProjectTask();
                    t.setTaskId(rs.getInt("TaskID"));
                    t.setTaskName(rs.getString("TaskName"));
                    list.add(t);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE,
                    "listReviewedTasksBySupervisor", ex);
        }

        return list;
    }

    public boolean isTeamLeader(int studentId) {
        String sql = """
        SELECT 1
        FROM TeamMember
        WHERE MemberID = ?
          AND IsLeader = 1
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "isTeamLeader", ex);
        }
        return false;
    }

    public List<TaskReview> listReviewsForStudent(Integer taskId, String q, int studentId) {
        List<TaskReview> list = new ArrayList<>();

        String sql = """
        SELECT tr.ReviewID, tr.TaskID, tr.ReviewedBy, tr.EstimateWorkPercentDone, tr.ReviewComment, tr.DateCreated
        FROM TaskReview tr
        WHERE tr.TaskID IN (
            SELECT DISTINCT te.TaskID
            FROM TimesheetEntry te
            WHERE te.UserID = ?
        )
          AND (? IS NULL OR tr.TaskID = ?)
          AND (? IS NULL OR tr.ReviewComment LIKE ?)
        ORDER BY tr.DateCreated DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            ps.setInt(idx++, studentId);

            if (taskId == null || taskId <= 0) {
                ps.setObject(idx++, null);
                ps.setObject(idx++, null);
            } else {
                ps.setInt(idx++, taskId);
                ps.setInt(idx++, taskId);
            }

            if (q == null || q.isBlank()) {
                ps.setObject(idx++, null);
                ps.setObject(idx++, null);
            } else {
                ps.setString(idx++, q);
                ps.setString(idx++, "%" + q.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskReview r = new TaskReview();
                    r.setReviewId(rs.getInt("ReviewID"));
                    r.setTaskId(rs.getInt("TaskID"));
                    r.setReviewedBy(rs.getInt("ReviewedBy"));
                    r.setEstimateWorkPercentDone(rs.getObject("EstimateWorkPercentDone", Double.class));
                    r.setReviewComment(rs.getString("ReviewComment"));
                    r.setDateCreated(rs.getTimestamp("DateCreated"));
                    list.add(r);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "listReviewsForStudent", ex);
        }

        return list;
    }

    public List<ProjectTask> listTasksForStudent(int studentId) {
        List<ProjectTask> list = new ArrayList<>();

        String sql = """
        SELECT DISTINCT pt.TaskID, pt.TaskName
        FROM ProjectTask pt
        INNER JOIN TimesheetEntry te ON te.TaskID = pt.TaskID
        WHERE te.UserID = ?
        ORDER BY pt.TaskID DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProjectTask t = new ProjectTask();
                    t.setTaskId(rs.getInt("TaskID"));
                    t.setTaskName(rs.getString("TaskName"));
                    list.add(t);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "listTasksForStudent", ex);
        }

        return list;
    }

    public java.util.Map<Integer, String> getUserFullNamesByIds(java.util.Set<Integer> ids) {
        java.util.Map<Integer, String> map = new java.util.HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return map;
        }

        // Build IN (?, ?, ?)
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                in.append(",");
            }
            in.append("?");
        }

        String sql = "SELECT UserID, FullName FROM UserAccount WHERE UserID IN (" + in + ")";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            for (Integer id : ids) {
                ps.setInt(idx++, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("UserID"), rs.getString("FullName"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskReviewDAO.class.getName()).log(Level.SEVERE, "getUserFullNamesByIds", ex);
        }
        return map;
    }

}
