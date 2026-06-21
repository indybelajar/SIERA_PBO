package view;

import dao.TaskDAO;
import model.Task;
import model.TaskSubmission;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskForm extends JPanel {
    private int groupId;
    private TaskDAO taskDAO;
    
    // Create Task Fields
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField deadlineField;
    
    // Monitoring Fields
    private JComboBox<TaskWrapper> taskSelector;
    private JTable submissionTable;
    private DefaultTableModel tableModel;
    
    private boolean isUpdatingTable = false;
    
    public TaskForm(int groupId) {
        this.groupId = groupId;
        this.taskDAO = new TaskDAO();
        
        initComponents();
        loadGroupTasks();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // --- 1. TITLE PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("TASK MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.LIGHT_GRAY);
        titlePanel.add(sep, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);
        
        // --- 2. CENTER CONTENT PANEL ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Form Panel: Create Task
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Judul
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel judulLabel = new JLabel("Judul");
        judulLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(judulLabel, gbc);
        
        titleField = new JTextField(40);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);
        
        // Deskripsi
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel descLabel = new JLabel("Deskripsi");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(descLabel, gbc);
        
        descriptionField = new JTextField(40);
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(descriptionField, gbc);
        
        // Deadline
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel deadlineLabel = new JLabel("Deadline");
        deadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(deadlineLabel, gbc);
        
        deadlineField = new JTextField(40);
        deadlineField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deadlineField.setText(LocalDate.now().plusDays(7).toString() + " 23:59:00");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(deadlineField, gbc);
        
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Button: Create Task
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        JButton createButton = new JButton("+ Create Task");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createButton.setPreferredSize(new Dimension(120, 32));
        createButton.addActionListener(e -> createTask());
        btnPanel.add(createButton);
        centerPanel.add(btnPanel);
        
        centerPanel.add(Box.createVerticalStrut(30));
        
        // Section Header: Mentee's Tasks
        JPanel sectionHeaderPanel = new JPanel(new BorderLayout());
        sectionHeaderPanel.setOpaque(false);
        JLabel sectionLabel = new JLabel("Mentee's Tasks");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionHeaderPanel.add(sectionLabel, BorderLayout.WEST);
        
        JSeparator sectionSep = new JSeparator();
        sectionSep.setForeground(Color.LIGHT_GRAY);
        sectionHeaderPanel.add(sectionSep, BorderLayout.SOUTH);
        centerPanel.add(sectionHeaderPanel);
        
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Filter Panel (Dropdown Task)
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setOpaque(false);
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 0, 5, 15);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.weightx = 0.0;
        JLabel taskLabel = new JLabel("Task");
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(taskLabel, fgbc);
        
        taskSelector = new JComboBox<>();
        taskSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskSelector.addActionListener(e -> loadSubmissions());
        fgbc.gridx = 1;
        fgbc.weightx = 1.0;
        filterPanel.add(taskSelector, fgbc);
        
        centerPanel.add(filterPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Submissions Table
        String[] columns = {"No.", "Name", "NIM", "Submission", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only Status column (4) is editable
                return column == 4;
            }
        };
        
        submissionTable = new JTable(tableModel);
        submissionTable.setRowHeight(30);
        submissionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        submissionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Map Status Combo Options (in Indonesian)
        String[] statusDisplay = {"Pending", "Terkirim", "Diterima", "Ditolak"};
        JComboBox<String> statusCombo = new JComboBox<>(statusDisplay);
        submissionTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusCombo));
        
        // Listener for changes in Status column
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && !isUpdatingTable) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 4) {
                    saveRowStatus(row);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(submissionTable);
        centerPanel.add(scrollPane);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void loadGroupTasks() {
        taskSelector.removeAllItems();
        List<Task> tasks = taskDAO.getTasksByGroupId(groupId);
        for (Task task : tasks) {
            taskSelector.addItem(new TaskWrapper(task));
        }
        
        loadSubmissions();
    }
    
    private void loadSubmissions() {
        TaskWrapper wrapper = (TaskWrapper) taskSelector.getSelectedItem();
        if (wrapper == null) {
            isUpdatingTable = true;
            tableModel.setRowCount(0);
            isUpdatingTable = false;
            return;
        }
        
        isUpdatingTable = true;
        tableModel.setRowCount(0);
        
        List<TaskSubmission> list = taskDAO.getSubmissionsByTaskId(wrapper.getTask().getId());
        
        int no = 1;
        for (TaskSubmission sub : list) {
            String displayStatus = mapStatusToDisplay(sub.getStatus());
            String submissionLink = (sub.getSubmissionLink() == null || sub.getSubmissionLink().trim().isEmpty()) 
                                    ? "-" : sub.getSubmissionLink();
            
            tableModel.addRow(new Object[]{
                no++,
                sub.getUserName(),
                sub.getUserId(), // NIM is represented by id
                submissionLink,
                displayStatus
            });
        }
        
        isUpdatingTable = false;
    }
    
    private void createTask() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String deadlineStr = deadlineField.getText().trim();
        
        if (title.isEmpty() || description.isEmpty() || deadlineStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Date deadline;
        try {
            // Support either YYYY-MM-DD or YYYY-MM-DD HH:MM:SS
            if (deadlineStr.length() == 10) {
                deadlineStr += " 23:59:00";
            }
            java.sql.Timestamp ts = java.sql.Timestamp.valueOf(deadlineStr);
            deadline = new Date(ts.getTime());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah! Gunakan YYYY-MM-DD atau YYYY-MM-DD HH:MM:SS", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Task task = new Task(0, groupId, title, description, deadline);
        if (taskDAO.createTask(task)) {
            JOptionPane.showMessageDialog(this, "Task created successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            titleField.setText("");
            descriptionField.setText("");
            deadlineField.setText(LocalDate.now().plusDays(7).toString() + " 23:59:00");
            
            loadGroupTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create task!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveRowStatus(int row) {
        TaskWrapper wrapper = (TaskWrapper) taskSelector.getSelectedItem();
        if (wrapper == null) return;
        
        int taskId = wrapper.getTask().getId();
        int menteeId = (int) tableModel.getValueAt(row, 2);
        String displayStatus = (String) tableModel.getValueAt(row, 4);
        String dbStatus = mapDisplayToStatus(displayStatus);
        
        if (taskDAO.updateSubmissionStatus(taskId, menteeId, dbStatus)) {
            // Silently updated
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate status tugas di database.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String mapStatusToDisplay(String dbStatus) {
        if (dbStatus == null) return "Pending";
        switch (dbStatus) {
            case "Submitted":
                return "Terkirim";
            case "Accepted":
                return "Diterima";
            case "Rejected":
                return "Ditolak";
            default:
                return "Pending";
        }
    }
    
    private String mapDisplayToStatus(String displayStatus) {
        if (displayStatus == null) return "Pending";
        switch (displayStatus) {
            case "Terkirim":
                return "Submitted";
            case "Diterima":
                return "Accepted";
            case "Ditolak":
                return "Rejected";
            default:
                return "Pending";
        }
    }
    
    // Wrapper to display Task Title in JComboBox
    private static class TaskWrapper {
        private final Task task;
        public TaskWrapper(Task task) { this.task = task; }
        public Task getTask() { return task; }
        @Override
        public String toString() { return task.getTitle(); }
    }
}