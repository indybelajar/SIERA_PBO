package view;

import dao.AttendanceDAO;
import model.Attendance;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AttendanceView extends JPanel {
    private int userId;
    private AttendanceDAO attendanceDAO;
    private JPanel cardsPanel;
    
    public AttendanceView(int userId) {
        this.userId = userId;
        this.attendanceDAO = new AttendanceDAO();
        initComponents();
        loadAttendance();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // --- Title Panel ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("ATTENDANCE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Horizontal separator line under title
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
    
    private void loadAttendance() {
        cardsPanel.removeAll();
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByUserId(userId);
        
        if (attendanceList.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            JLabel emptyLabel = new JLabel("Belum ada data absensi.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyPanel.add(emptyLabel);
            cardsPanel.add(emptyPanel);
        } else {
            for (Attendance att : attendanceList) {
                JPanel card = createAttendanceCard(att);
                cardsPanel.add(card);
                cardsPanel.add(Box.createVerticalStrut(12));
            }
        }
        
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
    
    private JPanel createAttendanceCard(Attendance att) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(800, 90));
        card.setPreferredSize(new Dimension(800, 90));
        
        // Left Column: Agenda & Date
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        leftPanel.setOpaque(false);
        
        JLabel agendaLabel = new JLabel(capitalize(att.getAgenda()));
        agendaLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        agendaLabel.setForeground(new Color(30, 41, 59));
        
        JLabel dateLabel = new JLabel(formatDate(att.getAttendanceDate().toString()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        
        leftPanel.add(agendaLabel);
        leftPanel.add(dateLabel);
        
        // Right Column: Status
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);
        
        // Status Capsule Label
        JLabel statusLabel = new JLabel("  " + att.getStatus().toUpperCase() + "  ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Simple color mapping based on status
        Color statusBg;
        Color statusFg;
        switch (att.getStatus().toLowerCase()) {
            case "hadir":
                statusBg = new Color(240, 253, 244); // soft green
                statusFg = new Color(22, 163, 74);
                break;
            case "sakit":
            case "izin":
                statusBg = new Color(254, 249, 195); // soft yellow
                statusFg = new Color(202, 138, 4);
                break;
            default: // tanpa keterangan
                statusBg = new Color(254, 242, 242); // soft red
                statusFg = new Color(220, 38, 38);
                break;
        }
        statusLabel.setBackground(statusBg);
        statusLabel.setForeground(statusFg);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(statusFg, 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        
        rightPanel.add(statusLabel);
        
        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.EAST);
        
        // Notes (if any) placed at the bottom
        if (att.getNotes() != null && !att.getNotes().trim().isEmpty()) {
            JLabel notesLabel = new JLabel("Catatan: " + att.getNotes());
            notesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            notesLabel.setForeground(Color.GRAY);
            card.add(notesLabel, BorderLayout.SOUTH);
            
            // Adjust preferred height if there is a note
            card.setPreferredSize(new Dimension(800, 110));
            card.setMaximumSize(new Dimension(800, 110));
        }
        
        return card;
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
    
    private String formatDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[2]);
                int month = Integer.parseInt(parts[1]);
                String year = parts[0];
                String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                                   "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                if (month >= 1 && month <= 12) {
                    return day + " " + months[month - 1] + " " + year;
                }
            }
        } catch (Exception e) {
            // fallback
        }
        return dateStr;
    }
}