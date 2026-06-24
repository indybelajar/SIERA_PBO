package dao;

import model.User;
import model.Mentor;
import model.Mentee;
import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User login(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                int groupId = rs.getInt("group_id");
                
                User user;
                if ("mentor".equals(role)) {
                    user = new Mentor(id, name, email, password);
                } else {
                    user = new Mentee(id, name, email, password);
                }
                user.setGroupId(groupId);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean registerUser(User user, String groupName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            
            conn.setAutoCommit(false); // start transaction
            
            // Standardize group name
            String normalizedGroup = groupName.trim();
            if (normalizedGroup.matches("\\d+")) {
                normalizedGroup = "Kelompok " + normalizedGroup;
            }
            
            int groupId = 0;
            // Check if group exists
            String checkGroupQuery = "SELECT id FROM groups WHERE group_name = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkGroupQuery)) {
                checkStmt.setString(1, normalizedGroup);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        groupId = rs.getInt("id");
                    }
                }
            }
            
            // If group doesn't exist, create it
            if (groupId == 0) {
                String insertGroupQuery = "INSERT INTO groups (group_name) VALUES (?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertGroupQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, normalizedGroup);
                    insertStmt.executeUpdate();
                    try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            groupId = rs.getInt(1);
                        }
                    }
                }
            }
            
            // Insert user
            String email = user.getEmail();
            long userId = 0;
            try {
                String prefix = email.split("@")[0];
                userId = Long.parseLong(prefix);
                user.setId(userId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            String query = "INSERT INTO users (id, name, email, password, role, group_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setLong(1, user.getId());
                pstmt.setString(2, user.getName());
                pstmt.setString(3, user.getEmail());
                pstmt.setString(4, user.getPassword());
                pstmt.setString(5, user.getRole());
                pstmt.setInt(6, groupId);
                
                pstmt.executeUpdate();
            }
            
            if (userId > 0) {
                // Insert an empty profile row associated with this user
                String profileQuery = "INSERT INTO user_profiles (user_id) VALUES (?)";
                try (PreparedStatement profilePstmt = conn.prepareStatement(profileQuery)) {
                    profilePstmt.setLong(1, userId);
                    profilePstmt.executeUpdate();
                }
            }
            
            conn.commit(); // commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean isEmailRegistered(String email) {
        String query = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setGroupId(rs.getInt("group_id"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public User getUserById(long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setGroupId(rs.getInt("group_id"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}