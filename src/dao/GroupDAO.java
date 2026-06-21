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
        String query = "SELECT u.*, p.jurusan, p.kontak FROM users u " +
                      "LEFT JOIN user_profiles p ON u.id = p.user_id " +
                      "WHERE u.group_id = ?";
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
                user.setGroupId(rs.getInt("group_id"));
                user.setJurusan(rs.getString("jurusan"));
                user.setKontak(rs.getString("kontak"));
                members.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    
    public Group getGroupByUserId(int userId) {
        String query = "SELECT g.* FROM groups g " +
                      "JOIN users u ON g.id = u.group_id " +
                      "WHERE u.id = ?";
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
    
    public String getGroupNameById(int groupId) {
        String query = "SELECT group_name FROM groups WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("group_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Group";
    }
}