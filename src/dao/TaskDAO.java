package dao;

import model.Task;
import model.TaskSubmission;
import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    
    public boolean createTask(Task task) {
        String query = "INSERT INTO tasks (group_id, title, description, deadline) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            
            conn.setAutoCommit(false); // Start transaction
            
            int taskId = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, task.getGroupId());
                pstmt.setString(2, task.getTitle());
                pstmt.setString(3, task.getDescription());
                pstmt.setTimestamp(4, new java.sql.Timestamp(task.getDeadline().getTime()));
                
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            taskId = rs.getInt(1);
                            task.setId(taskId);
                        }
                    }
                }
            }
            
            if (taskId > 0) {
                // Get all mentees in the group
                String menteeQuery = "SELECT id FROM users WHERE group_id = ? AND role = 'mentee'";
                List<Integer> menteeIds = new ArrayList<>();
                try (PreparedStatement menteeStmt = conn.prepareStatement(menteeQuery)) {
                    menteeStmt.setInt(1, task.getGroupId());
                    try (ResultSet rs = menteeStmt.executeQuery()) {
                        while (rs.next()) {
                            menteeIds.add(rs.getInt("id"));
                        }
                    }
                }
                
                // Create a 'Pending' submission entry for each mentee
                String subQuery = "INSERT INTO task_submissions (task_id, user_id, status) VALUES (?, ?, 'Pending')";
                try (PreparedStatement subStmt = conn.prepareStatement(subQuery)) {
                    for (int menteeId : menteeIds) {
                        subStmt.setInt(1, taskId);
                        subStmt.setInt(2, menteeId);
                        subStmt.addBatch();
                    }
                    subStmt.executeBatch();
                }
            }
            
            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on failure
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
    
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks ORDER BY deadline ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setGroupId(rs.getInt("group_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setDeadline(rs.getDate("deadline"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    public List<Task> getTasksByGroupId(int groupId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE group_id = ? ORDER BY deadline ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setGroupId(rs.getInt("group_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setDeadline(rs.getDate("deadline"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    public List<TaskSubmission> getSubmissionsByTaskId(int taskId) {
        List<TaskSubmission> list = new ArrayList<>();
        String query = "SELECT ts.id, ts.task_id, ts.user_id, u.name as user_name, ts.submission_link, ts.status, ts.submitted_at " +
                      "FROM task_submissions ts " +
                      "JOIN users u ON ts.user_id = u.id " +
                      "WHERE ts.task_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(new TaskSubmission(
                    rs.getInt("id"),
                    rs.getInt("task_id"),
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("submission_link"),
                    rs.getString("status"),
                    rs.getTimestamp("submitted_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean updateSubmissionStatus(int taskId, int userId, String status) {
        String query = "UPDATE task_submissions SET status = ? WHERE task_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, taskId);
            pstmt.setInt(3, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public TaskSubmission getSubmissionByTaskAndUser(int taskId, int userId) {
        String query = "SELECT ts.id, ts.task_id, ts.user_id, u.name as user_name, ts.submission_link, ts.status, ts.submitted_at " +
                      "FROM task_submissions ts " +
                      "JOIN users u ON ts.user_id = u.id " +
                      "WHERE ts.task_id = ? AND ts.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new TaskSubmission(
                    rs.getInt("id"),
                    rs.getInt("task_id"),
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("submission_link"),
                    rs.getString("status"),
                    rs.getTimestamp("submitted_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}