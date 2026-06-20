package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class TaskForm extends JPanel {
    private int mentorId;
    private TaskDAO taskDAO;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField deadlineField;
    
    public TaskForm(int mentorId) {
        this.mentorId = mentorId;
        this.taskDAO = new TaskDAO();
        initComponents();
        loadTasks();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel for form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Task"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        
        titleField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(titleField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(descScroll, gbc);
        
        // Deadline
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        
        deadlineField = new JTextField(20);
        deadlineField.setText(LocalDate.now().plusDays(7).toString());
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(deadlineField, gbc);
        
        // Create button
        JButton createButton = new JButton("Create Task");
        createButton.addActionListener(e -> createTask());
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(createButton, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Task List"));
        
        String[] columns = {"ID", "Title", "Description", "Deadline"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        tablePanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private void createTask() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String deadlineStr = deadlineField.getText().trim();
        
        if (title.isEmpty() || description.isEmpty() || deadlineStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Date deadline = Date.valueOf(deadlineStr);
            Task task = new Task(0, title, description, deadline, mentorId);
            
            if (taskDAO.createTask(task)) {
                JOptionPane.showMessageDialog(this, "Task created successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                titleField.setText("");
                descriptionArea.setText("");
                deadlineField.setText(LocalDate.now().plusDays(7).toString());
                loadTasks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create task!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = taskDAO.getTasksByMentorId(mentorId);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline()
            });
        }
    }
}