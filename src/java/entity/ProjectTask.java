/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.sql.Timestamp;

/**
 *
 * @author Admin
 */
public class ProjectTask {

    // 1. Khai báo các "Hard String" ở đây để dùng chung
    // public static final: nghĩa là hằng số, không đổi được
    public static final String STATUS_TODO = "TO_DO";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_SUSPENDED = "SUSPENDED";

    // Các trường dữ liệu
    private int taskId;
    private Integer projectId;
    private String taskName;
    private String description;
    private Timestamp deadline;
    private Double estimateHourToDo;
    private Timestamp createdAt;
    // Vẫn dùng String như bình thường
    private String status;
    
    // Optional display-only field from joins
    private String projectName;

    public ProjectTask() {
        // Mặc định khi tạo mới là TO_DO
        this.status = STATUS_TODO;
    }

    // Constructor đầy đủ
    public ProjectTask(int taskId, Integer projectId, String taskName, String description, Timestamp deadline, Double estimateHourToDo, Timestamp createdAt, String status) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.taskName = taskName;
        this.description = description;
        this.deadline = deadline;
        this.estimateHourToDo = estimateHourToDo;
        this.createdAt = createdAt;
        // Gọi hàm setter để nó tự kiểm tra tính hợp lệ
        setStatus(status);
    }

    // --- Getter & Setter (Phần quan trọng nhất) ---
    public String getStatus() {
        return status;
    }

    /**
     * Hàm này sẽ kiểm tra xem string truyền vào có đúng chuẩn SQL không. Nếu
     * sai sẽ báo lỗi ngay lập tức để bạn biết đường sửa.
     */
    public void setStatus(String status) {
        if (status == null) {
            // Tùy logic: có thể set mặc định hoặc báo lỗi. Ở đây mình set mặc định.
            this.status = STATUS_TODO;
            return;
        }

        // Chuyển về chữ hoa hết để so sánh cho chắc ăn (ví dụ 'to_do' vẫn nhận)
        String upperStatus = status.toUpperCase();

        if (upperStatus.equals(STATUS_TODO)
                || upperStatus.equals(STATUS_IN_PROGRESS)
                || upperStatus.equals(STATUS_SUSPENDED)) {

            this.status = upperStatus; // Đúng thì gán
        } else {
            // Sai thì ném lỗi ra console
            throw new IllegalArgumentException("Status không hợp lệ: '" + status + "'. Chỉ chấp nhận: TO_DO, IN_PROGRESS, SUSPENDED");
        }
    }

    // ... Các Getter/Setter khác giữ nguyên ...
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public Double getEstimateHourToDo() {
        return estimateHourToDo;
    }

    public void setEstimateHourToDo(Double estimateHourToDo) {
        this.estimateHourToDo = estimateHourToDo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
