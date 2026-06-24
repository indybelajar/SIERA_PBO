package dao;

import model.Attendance;
import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    
    /**
     * Inserts a new attendance record or updates an existing one if the user and agenda already match.
     */
    public boolean saveOrUpdateAttendance(Attendance attendance) {
        String checkQuery = "SELECT id FROM attendance WHERE user_id = ? AND agenda = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            
            checkStmt.setLong(1, attendance.getUserId());
            checkStmt.setString(2, attendance.getAgenda());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Record exists, let's update it
                String updateQuery = "UPDATE attendance SET attendance_date = ?, status = ?, notes = ? WHERE user_id = ? AND agenda = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setDate(1, attendance.getAttendanceDate());
                    updateStmt.setString(2, attendance.getStatus());
                    updateStmt.setString(3, attendance.getNotes());
                    updateStmt.setLong(4, attendance.getUserId());
                    updateStmt.setString(5, attendance.getAgenda());
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                // Record does not exist, let's insert it
                String insertQuery = "INSERT INTO attendance (user_id, agenda, attendance_date, status, notes) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setLong(1, attendance.getUserId());
                    insertStmt.setString(2, attendance.getAgenda());
                    insertStmt.setDate(3, attendance.getAttendanceDate());
                    insertStmt.setString(4, attendance.getStatus());
                    insertStmt.setString(5, attendance.getNotes());
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Attendance> getAttendanceByUserId(long userId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE user_id = ? ORDER BY attendance_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setUserId(rs.getLong("user_id"));
                attendance.setAgenda(rs.getString("agenda"));
                attendance.setAttendanceDate(rs.getDate("attendance_date"));
                attendance.setStatus(rs.getString("status"));
                attendance.setNotes(rs.getString("notes"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }
    
    public List<Attendance> getAllAttendance() {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance ORDER BY attendance_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setUserId(rs.getLong("user_id"));
                attendance.setAgenda(rs.getString("agenda"));
                attendance.setAttendanceDate(rs.getDate("attendance_date"));
                attendance.setStatus(rs.getString("status"));
                attendance.setNotes(rs.getString("notes"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }
    
    public List<Attendance> getAttendanceByGroupAndAgenda(int groupId, String agenda) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT a.* FROM attendance a " +
                      "JOIN users u ON a.user_id = u.id " +
                      "WHERE u.group_id = ? AND a.agenda = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, groupId);
            pstmt.setString(2, agenda);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setUserId(rs.getLong("user_id"));
                attendance.setAgenda(rs.getString("agenda"));
                attendance.setAttendanceDate(rs.getDate("attendance_date"));
                attendance.setStatus(rs.getString("status"));
                attendance.setNotes(rs.getString("notes"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }
}