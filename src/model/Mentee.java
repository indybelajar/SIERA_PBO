package model;

public class Mentee extends User {
    public Mentee() {
        super();
    }
    
    public Mentee(int id, String name, String email, String password) {
        super(id, name, email, password, "mentee");
    }
    
    @Override
    public void showDashboard() {
        System.out.println("Displaying Mentee Dashboard");
    }
    
    public void viewTask() {
        System.out.println("Viewing tasks");
    }
    
    public void submitTask() {
        System.out.println("Submitting task");
    }
}