package model;

import java.sql.Timestamp;

public class TaskSubmission {
    private int id;
    private int taskId;
    private int userId;
    private String userName;
    private String submissionLink;
    private String status;
    private Timestamp submittedAt;
    
    public TaskSubmission() {}
    
    public TaskSubmission(int id, int taskId, int userId, String userName, String submissionLink, String status, Timestamp submittedAt) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.userName = userName;
        this.submissionLink = submissionLink;
        this.status = status;
        this.submittedAt = submittedAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getSubmissionLink() { return submissionLink; }
    public void setSubmissionLink(String submissionLink) { this.submissionLink = submissionLink; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }
}
