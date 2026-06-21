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
        addMenuItem("Groups", "👥", new GroupView());
        addMenuItem("Tasks", "📝", new TaskForm(mentor.getId()));
        addMenuItem("Attendance", "📅", new AttendanceForm());
        addMenuItem("Profile", "👤", createProfilePanel());
        
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
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Profile Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(titleLabel, gbc);
        
        String[][] profileData = {
            {"Name", mentor.getName()},
            {"Email", mentor.getEmail()},
            {"Role", "Mentor"}
        };
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        for (int i = 0; i < profileData.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            JLabel label = new JLabel(profileData[i][0] + ":");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(Color.GRAY);
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            JLabel valueLabel = new JLabel(profileData[i][1]);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            valueLabel.setForeground(new Color(30, 41, 59));
            panel.add(valueLabel, gbc);
        }
        
        // Wrap in another panel to keep it centered
        JPanel container = new JPanel(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        container.add(panel);
        
        return container;
    }
}