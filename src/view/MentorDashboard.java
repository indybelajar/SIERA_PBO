package view;

import model.Mentor;
import javax.swing.*;
import java.awt.*;

public class MentorDashboard extends BaseLayout {
    private Mentor mentor;
    
    public MentorDashboard(Mentor mentor) {
        super("Mentor Dashboard - " + mentor.getName(), mentor.getName(), "Mentor");
        this.mentor = mentor;
        
        // Add menu items to the sidebar
        addMenuItem("Dashboard", "🏠", createDashboardPanel());
        addMenuItem("Groups", "👥", new GroupView(mentor.getGroupId(), mentor));
        addMenuItem("Tasks", "📝", new TaskForm(mentor.getGroupId()));
        addMenuItem("Attendance", "📅", new AttendanceForm(mentor.getId()));
        addMenuItem("Profile", "👤", new ProfileView(mentor, true, null));
        
        // Initialize layout and show default tab
        initializeLayout();
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Welcome Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome back, " + mentor.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel descLabel = new JLabel("Manage your groups, tasks, and attendance here.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards container
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);
        
        String[][] stats = {
            {"Total Mentees", "15"},
            {"Total Tasks", "8"},
            {"Today's Attendance", "12"},
            {"Pending Tasks", "3"}
        };
        
        for (String[] stat : stats) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            card.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel(stat[0]);
            titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            titleLabel.setForeground(Color.GRAY);
            
            JLabel valueLabel = new JLabel(stat[1]);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            valueLabel.setForeground(new Color(30, 41, 59));
            
            card.add(titleLabel, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            statsPanel.add(card);
        }
        
        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }
}