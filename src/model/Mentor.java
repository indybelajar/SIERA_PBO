package model;

public class Mentor extends User {
    public Mentor() {
        super();
    }
    
    public Mentor(long id, String name, String email, String password) {
        super(id, name, email, password, "mentor");
    }
    
    @Override
    public void showDashboard() {
        System.out.println("Displaying Mentor Dashboard");
    }
    
    public void createTask() {
        System.out.println("Creating new task");
    }
    
    public void inputAttendance() {
        System.out.println("Inputting attendance");
    }
}