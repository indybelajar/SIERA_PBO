package dao;

import model.Group;
import model.User;
import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM groups";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("id"));
                group.setGroupName(rs.getString("group_name"));
                group.setMembers(getGroupMembers(group.getId()));
                groups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }
    
    public List<User> getGroupMembers(int groupId) {
        List<User> members = new ArrayList<>();
        String query = "SELECT u.* FROM users u " +
                      "JOIN group_members gm ON u.id = gm.user_id " +
                      "WHERE gm.group_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                members.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    
    public Group getGroupByUserId(int userId) {
        String query = "SELECT g.* FROM groups g " +
                      "JOIN group_members gm ON g.id = gm.group_id " +
                      "WHERE gm.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("id"));
                group.setGroupName(rs.getString("group_name"));
                group.setMembers(getGroupMembers(group.getId()));
                return group;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}