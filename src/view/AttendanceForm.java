package view;

import dao.AttendanceDAO;
import dao.UserDAO;
import model.Attendance;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AttendanceForm extends JPanel {
    private AttendanceDAO attendanceDAO;
    private UserDAO userDAO;
    private JComboBox<String> userComboBox;
    private JComboBox<String> statusComboBox;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    
    public AttendanceForm() {
        attendanceDAO = new AttendanceDAO();
        userDAO = new UserDAO();
        initComponents();
        loadAttendance();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel for form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Input Attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // User selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mentee:"), gbc);
        
        userComboBox = new JComboBox<>();
        loadUsers();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(userComboBox, gbc);
        
        // Status selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Status:"), gbc);
        
        String[] statuses = {"Hadir", "Izin", "Alpha"};
        statusComboBox = new JComboBox<>(statuses);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(statusComboBox, gbc);
        
        // Submit button
        JButton submitButton = new JButton("Add Attendance");
        submitButton.addActionListener(e -> addAttendance());
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(submitButton, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Attendance Records"));
        
        String[] columns = {"ID", "User ID", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(tableModel);
        tablePanel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private void loadUsers() {
        userComboBox.removeAllItems();
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            if ("mentee".equals(user.getRole())) {
                userComboBox.addItem(user.getId() + " - " + user.getName());
            }
        }
    }
    
    private void addAttendance() {
        String selectedUser = (String) userComboBox.getSelectedItem();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "No mentee selected!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int userId = Integer.parseInt(selectedUser.split(" - ")[0]);
        String status = (String) statusComboBox.getSelectedItem();
        Date today = Date.valueOf(LocalDate.now());
        
        Attendance attendance = new Attendance(0, userId, today, status);
        if (attendanceDAO.addAttendance(attendance)) {
            JOptionPane.showMessageDialog(this, "Attendance added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAttendance();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add attendance!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAttendance() {
        tableModel.setRowCount(0);
        List<Attendance> attendanceList = attendanceDAO.getAllAttendance();
        for (Attendance att : attendanceList) {
            tableModel.addRow(new Object[]{
                att.getId(),
                att.getUserId(),
                att.getAttendanceDate(),
                att.getStatus()
            });
        }
    }
}