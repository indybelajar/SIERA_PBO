package model;

import java.sql.Date;

public class Task {
    private int id;
    private int groupId;
    private String title;
    private String description;
    private Date deadline;
    
    public Task() {}
    
    public Task(int id, int groupId, String title, String description, Date deadline) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
}