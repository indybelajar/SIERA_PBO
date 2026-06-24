package model;

import java.sql.Timestamp;

public class Announcement {
    private int id;
    private int groupId;
    private String title;
    private String content;
    private Timestamp createdAt;

    public Announcement() {}

    public Announcement(int id, int groupId, String title, String content, Timestamp createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
