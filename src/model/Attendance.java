package model;

import java.sql.Date;

public class Attendance {
    private int id;
    private int userId;
    private String agenda;
    private Date attendanceDate;
    private String status;
    private String notes;
    
    public Attendance() {}
    
    public Attendance(int id, int userId, String agenda, Date attendanceDate, String status, String notes) {
        this.id = id;
        this.userId = userId;
        this.agenda = agenda;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.notes = notes;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getAgenda() { return agenda; }
    public void setAgenda(String agenda) { this.agenda = agenda; }
    
    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}