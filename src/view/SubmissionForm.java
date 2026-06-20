package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubmissionForm extends JDialog {
    private int taskId;
    private int userId;
    private JTextArea submissionArea;
    
    public SubmissionForm(int taskId, int userId) {
        this.taskId = taskId;
        this.userId = userId;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Submit Task");
        setModal(true);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel instructionLabel = new JLabel("Enter your submission:");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(instructionLabel, BorderLayout.NORTH);
        
        submissionArea = new JTextArea();
        submissionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(submissionArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitTask());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    private void submitTask() {
        String submission = submissionArea.getText().trim();
        if (submission.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your submission!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = database.DBConnection.getConnection()) {
            String query = "INSERT INTO submissions (task_id, user_id, submission_text, submitted_at) " +
                          "VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, submission);
            pstmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            if (pstmt.executeUpdate() > 0) {
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