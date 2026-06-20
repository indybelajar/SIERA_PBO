package model;

import java.sql.Date;

public class Task {
    private int id;
    private String title;
    private String description;
    private Date deadline;
    private int mentorId;
    
    public Task() {}
    
    public Task(int id, String title, String description, Date deadline, int mentorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.mentorId = mentorId;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    
    public int getMentorId() { return mentorId; }
    public void setMentorId(int mentorId) { this.mentorId = mentorId; }
}