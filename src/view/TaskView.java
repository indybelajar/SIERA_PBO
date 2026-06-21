package view;

import dao.TaskDAO;
import model.Task;
import model.TaskSubmission;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TaskView extends JPanel {
    private int userId;
    private int groupId;
    private TaskDAO taskDAO;
    private JPanel cardsPanel;
    
    public TaskView(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
        this.taskDAO = new TaskDAO();
        initComponents();
        loadTasks();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // --- Title Panel ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("TASK MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        JSeparator titleSep = new JSeparator();
        titleSep.setForeground(Color.LIGHT_GRAY);
        titlePanel.add(titleSep, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);
        
        // --- Scroll Panel for Cards ---
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadTasks() {
        cardsPanel.removeAll();
        List<Task> tasks = taskDAO.getTasksByGroupId(groupId);
        
        if (tasks.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            JLabel emptyLabel = new JLabel("Belum ada tugas untuk kelompok Anda.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyPanel.add(emptyLabel);
            cardsPanel.add(emptyPanel);
        } else {
            for (Task task : tasks) {
                JPanel card = createTaskCard(task);
                cardsPanel.add(card);
                cardsPanel.add(Box.createVerticalStrut(15));
            }
        }
        
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
    
    private JPanel createTaskCard(Task task) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(Color.WHITE);
        
        // Find user submission status for this task
        TaskSubmission submission = taskDAO.getSubmissionByTaskAndUser(task.getId(), userId);
        String status = (submission != null) ? submission.getStatus() : "Pending";
        
        // Left Panel: Title, Description, Deadline
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(15, 23, 42)); // Slate-900
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(6));
        
        JLabel descHeader = new JLabel("Deskripsi:");
        descHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descHeader.setForeground(Color.DARK_GRAY);
        leftPanel.add(descHeader);
        
        JTextArea descArea = new JTextArea(task.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(Color.DARK_GRAY);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setBackground(new Color(0,0,0,0));
        leftPanel.add(descArea);
        leftPanel.add(Box.createVerticalStrut(10));
        
        // Deadline label (e.g. 29 Mei 2026, 23.59)
        String deadlineStr = formatDateTime(task.getDeadline().toString());
        JLabel deadlineLabel = new JLabel(deadlineStr);
        deadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deadlineLabel.setForeground(Color.GRAY);
        deadlineLabel.setOpaque(true);
        deadlineLabel.setBackground(new Color(241, 245, 249)); // light slate capsule
        deadlineLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        leftPanel.add(deadlineLabel);
        
        // Right Panel: Action Button / Badge
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        
        if ("Accepted".equalsIgnoreCase(status)) {
            // "Diterima" Badge (disabled look)
            JLabel badge = new JLabel("  Diterima  ");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badge.setBackground(new Color(240, 253, 244)); // soft green
            badge.setForeground(new Color(22, 163, 74));
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(22, 163, 74), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            rightPanel.add(badge);
        } else if ("Submitted".equalsIgnoreCase(status)) {
            // "Terkirim" Badge (disabled look)
            JLabel badge = new JLabel("  Terkirim  ");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badge.setBackground(new Color(241, 245, 249)); // light slate
            badge.setForeground(Color.GRAY);
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            rightPanel.add(badge);
        } else {
            // "Add Submission" Button
            JButton addSubBtn = new JButton("Add Submission");
            addSubBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            addSubBtn.setBackground(new Color(25, 118, 210));
            addSubBtn.setForeground(Color.WHITE);
            addSubBtn.setFocusPainted(false);
            addSubBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addSubBtn.addActionListener(e -> {
                SubmissionForm form = new SubmissionForm(task.getId(), userId);
                form.setVisible(true);
                loadTasks(); // refresh view
            });
            rightPanel.add(addSubBtn);
        }
        
        card.add(leftPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        
        // Dynamically adjust height to accommodate wrap text in description
        // Let's set a reasonable maximum/preferred height
        card.setPreferredSize(new Dimension(800, 160));
        card.setMaximumSize(new Dimension(800, 160));
        
        return card;
    }
    
    private String formatDateTime(String dateStr) {
        // Date is in YYYY-MM-DD or YYYY-MM-DD HH:MM:SS format
        try {
            String timePart = "23.59";
            String datePart = dateStr;
            
            if (dateStr.contains(" ")) {
                String[] t = dateStr.split(" ");
                datePart = t[0];
                if (t[1].length() >= 5) {
                    timePart = t[1].substring(0, 5).replace(":", ".");
                }
            }
            
            String[] parts = datePart.split("-");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[2]);
                int month = Integer.parseInt(parts[1]);
                String year = parts[0];
                String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                                   "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                if (month >= 1 && month <= 12) {
                    return day + " " + months[month - 1] + " " + year + ", " + timePart;
                }
            }
        } catch (Exception e) {
            // fallback
        }
        return dateStr;
    }
}