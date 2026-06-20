package model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private int id;
    private String groupName;
    private List<User> members;
    
    public Group() {
        this.members = new ArrayList<>();
    }
    
    public Group(int id, String groupName) {
        this.id = id;
        this.groupName = groupName;
        this.members = new ArrayList<>();
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    
    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
    
    public void addMember(User user) {
        members.add(user);
    }
    
    public void removeMember(User user) {
        members.remove(user);
    }
}