package dao;  // ← HARUS dao, BUKAN model!

import model.Attendance;  // ← Import dari model
import database.DBConnection;  // ← Import dari database

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    
    public boolean addAttendance(Attendance attendance) {
        String query = "INSERT INTO attendance (user_id, attendance_date, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, attendance.getUserId());
            pstmt.setDate(2, attendance.getAttendanceDate());
            pstmt.setString(3, attendance.getStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Attendance> getAttendanceByUserId(int userId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE user_id = ? ORDER BY attendance_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setUserId(rs.getInt("user_id"));
                attendance.setAttendanceDate(rs.getDate("attendance_date"));
                attendance.setStatus(rs.getString("status"));
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
                attendance.setUserId(rs.getInt("user_id"));
                attendance.setAttendanceDate(rs.getDate("attendance_date"));
                attendance.setStatus(rs.getString("status"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }
}