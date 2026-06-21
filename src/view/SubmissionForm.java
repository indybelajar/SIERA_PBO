package view;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SubmissionForm extends JDialog {
    private int taskId;
    private int userId;
    private JTextField linkField;
    
    public SubmissionForm(int taskId, int userId) {
        this.taskId = taskId;
        this.userId = userId;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Submit Task");
        setModal(true);
        setSize(400, 160);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Instruction Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel instructionLabel = new JLabel("Enter your submission link (e.g. Google Drive):");
        instructionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(instructionLabel, gbc);
        
        // Link Input Field
        gbc.gridy = 1;
        linkField = new JTextField(25);
        linkField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        linkField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 6, 5, 6)
        ));
        panel.add(linkField, gbc);
        
        // Buttons
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        submitButton.addActionListener(e -> submitTask());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);
        
        add(panel);
    }
    
    private void submitTask() {
        String link = linkField.getText().trim();
        if (link.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your submission link!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to database.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if record already exists in task_submissions
            String checkQuery = "SELECT id FROM task_submissions WHERE task_id = ? AND user_id = ?";
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, taskId);
                checkStmt.setInt(2, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next();
                }
            }
            
            boolean success;
            if (exists) {
                // Update existing submission
                String updateQuery = "UPDATE task_submissions SET submission_link = ?, status = 'Submitted' WHERE task_id = ? AND user_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, link);
                    updateStmt.setInt(2, taskId);
                    updateStmt.setInt(3, userId);
                    success = updateStmt.executeUpdate() > 0;
                }
            } else {
                // Insert new submission
                String insertQuery = "INSERT INTO task_submissions (task_id, user_id, submission_link, status) VALUES (?, ?, ?, 'Submitted')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, taskId);
                    insertStmt.setInt(2, userId);
                    insertStmt.setString(3, link);
                    success = insertStmt.executeUpdate() > 0;
                }
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Task submitted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit task!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}