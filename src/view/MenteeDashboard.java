package view;

import model.Mentee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenteeDashboard extends JFrame {
    private Mentee mentee;
    private JTabbedPane tabbedPane;
    
    public MenteeDashboard(Mentee mentee) {
        this.mentee = mentee;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Mentee Dashboard - " + mentee.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuFile = new JMenu("File");
        JMenuItem menuLogout = new JMenuItem("Logout");
        menuLogout.addActionListener(e -> logout());
        menuFile.add(menuLogout);
        menuBar.add(menuFile);
        
        setJMenuBar(menuBar);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, Mentee " + mentee.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Dashboard tab
        JPanel dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        
        // Tasks tab
        TaskView taskView = new TaskView(mentee.getId());
        tabbedPane.addTab("Tasks", taskView);
        
        // Attendance tab
        AttendanceView attendanceView = new AttendanceView(mentee.getId());
        tabbedPane.addTab("Attendance", attendanceView);
        
        // Profile tab
        JPanel profilePanel = createProfilePanel();
        tabbedPane.addTab("Profile", profilePanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Stats cards
        String[][] stats = {
            {"Total Tasks", "8"},
            {"Submitted Tasks", "5"},
            {"Pending Tasks", "3"},
            {"Attendance", "85%"}
        };
        
        for (String[] stat : stats) {
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel(stat[0]);
            titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel valueLabel = new JLabel(stat[1]);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            card.add(titleLabel, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            panel.add(card);
        }
        
        return panel;
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("Profile Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        String[][] profileData = {
            {"Name", mentee.getName()},
            {"Email", mentee.getEmail()},
            {"Role", "Mentee"}
        };
        
        gbc.gridwidth = 1;
        for (int i = 0; i < profileData.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            panel.add(new JLabel(profileData[i][0] + ":"), gbc);
            
            gbc.gridx = 1;
            JLabel valueLabel = new JLabel(profileData[i][1]);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(valueLabel, gbc);
        }
        
        return panel;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Logout", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            dispose();
        }
    }
}