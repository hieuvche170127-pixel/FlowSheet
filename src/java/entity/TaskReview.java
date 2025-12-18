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
public class TaskReview {

    private int reviewId;
    private int taskId;
    private int reviewedBy; // UserID của người review

    // Mapping DECIMAL(5, 2) -> Double
    private Double estimateWorkPercentDone;

    private String reviewComment;
    private Timestamp dateCreated;

    // --- Constructor Rỗng ---
    public TaskReview() {
    }

    // --- Constructor Đầy Đủ ---
    public TaskReview(int reviewId, int taskId, int reviewedBy, Double estimateWorkPercentDone, String reviewComment, Timestamp dateCreated) {
        this.reviewId = reviewId;
        this.taskId = taskId;
        this.reviewedBy = reviewedBy;
        setEstimateWorkPercentDone(estimateWorkPercentDone);
        this.reviewComment = reviewComment;
        this.dateCreated = dateCreated;
    }

    // --- Getters and Setters ---
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(int reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Double getEstimateWorkPercentDone() {
        return estimateWorkPercentDone;
    }

    /**
     * Setter có validate: Chỉ chấp nhận giá trị từ 0 đến 100. Nếu nhập sai sẽ
     * báo lỗi hoặc tự điều chỉnh (ở đây mình chọn báo lỗi).
     */
    public void setEstimateWorkPercentDone(Double estimateWorkPercentDone) {
        if (estimateWorkPercentDone != null) {
            if (estimateWorkPercentDone < 0 || estimateWorkPercentDone > 100) {
                throw new IllegalArgumentException("Tiến độ công việc phải từ 0% đến 100%. Giá trị nhập: " + estimateWorkPercentDone);
            }
        }
        // Nếu null thì vẫn cho phép (để default database lo hoặc tùy logic java)
        this.estimateWorkPercentDone = estimateWorkPercentDone;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

  
}
