package view;

import model.Mentee;
import javax.swing.*;
import java.awt.*;

public class MenteeDashboard extends BaseLayout {
    private Mentee mentee;
    
    public MenteeDashboard(Mentee mentee) {
        super("Mentee Dashboard - " + mentee.getName(), mentee.getName(), "Mentee");
        this.mentee = mentee;
        
        // Add menu items to the sidebar
        addMenuItem("Dashboard", "🏠", createDashboardPanel());
        addMenuItem("Groups", "👥", new GroupView(mentee.getGroupId(), mentee));
        addMenuItem("Tasks", "📝", new TaskView(mentee.getId(), mentee.getGroupId()));
        addMenuItem("Attendance", "📅", new AttendanceView(mentee.getId()));
        addMenuItem("Profile", "👤", new ProfileView(mentee, true, null));
        
        // Initialize layout and show default tab
        initializeLayout();
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Welcome Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome, Mentee " + mentee.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel descLabel = new JLabel("Keep track of your tasks and check your attendance stats.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards container
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);
        
        String[][] stats = {
            {"Total Tasks", "8"},
            {"Submitted Tasks", "5"},
            {"Pending Tasks", "3"},
            {"Attendance Rate", "85%"}
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