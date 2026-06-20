package model;

import java.sql.Date;

public class Attendance {
    private int id;
    private int userId;
    private Date attendanceDate;
    private String status;
    
    public Attendance() {}
    
    public Attendance(int id, int userId, Date attendanceDate, String status) {
        this.id = id;
        this.userId = userId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}