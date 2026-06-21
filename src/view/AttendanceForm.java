package view;

import dao.AttendanceDAO;
import dao.GroupDAO;
import model.Attendance;
import model.User;
import model.Group;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceForm extends JPanel {
    private int mentorId;
    private AttendanceDAO attendanceDAO;
    private GroupDAO groupDAO;
    
    private JTextField groupNameField;
    private JComboBox<String> agendaComboBox;
    private JTextField dateField;
    
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    
    private Group mentorGroup;
    private List<User> mentees;
    
    public AttendanceForm(int mentorId) {
        this.mentorId = mentorId;
        this.attendanceDAO = new AttendanceDAO();
        this.groupDAO = new GroupDAO();
        this.mentees = new ArrayList<>();
        
        initComponents();
        loadGroupAndMentees();
        loadAttendanceData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // --- 1. TITLE PANEL ---
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel titleLabel = new JLabel("ATTENDANCE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // --- 2. CENTER CONTENT PANEL ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Group Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel groupNameLabel = new JLabel("Group Name");
        groupNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(groupNameLabel, gbc);
        
        groupNameField = new JTextField(35);
        groupNameField.setEditable(false);
        groupNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(groupNameField, gbc);
        
        // Agenda
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel agendaLabel = new JLabel("Agenda");
        agendaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(agendaLabel, gbc);
        
        String[] agendas = {"mentoring 1", "mentoring 2", "mentoring 3", "patribera day 1", "patribera day 2"};
        agendaComboBox = new JComboBox<>(agendas);
        agendaComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        agendaComboBox.addActionListener(e -> loadAttendanceData());
        gbc.gridx = 1;
        formPanel.add(agendaComboBox, gbc);
        
        // Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(dateLabel, gbc);
        
        dateField = new JTextField(35);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setText(LocalDate.now().toString());
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalStrut(25));
        
        // Section Header: Mentees
        JPanel sectionHeaderPanel = new JPanel(new BorderLayout());
        JLabel menteesLabel = new JLabel("Mentees");
        menteesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionHeaderPanel.add(menteesLabel, BorderLayout.WEST);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.LIGHT_GRAY);
        sectionHeaderPanel.add(separator, BorderLayout.SOUTH);
        
        centerPanel.add(sectionHeaderPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Mentees Table
        String[] columns = {"No.", "Nama", "NIM", "Status", "Catatan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };
        
        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(30);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        attendanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        String[] statusOptions = {"hadir", "sakit", "izin", "tanpa keterangan"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        attendanceTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusCombo));
        
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        centerPanel.add(scrollPane);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // --- 3. BOTTOM PANEL (Save Button) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(100, 36));
        saveButton.addActionListener(e -> saveAttendance());
        bottomPanel.add(saveButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadGroupAndMentees() {
        mentorGroup = groupDAO.getGroupByUserId(mentorId);
        if (mentorGroup != null) {
            groupNameField.setText(mentorGroup.getGroupName());
            mentees.clear();
            for (User member : mentorGroup.getMembers()) {
                if ("mentee".equals(member.getRole())) {
                    mentees.add(member);
                }
            }
        }
    }
    
    private void loadAttendanceData() {
        if (mentorGroup == null || mentees.isEmpty()) {
            return;
        }
        
        tableModel.setRowCount(0);
        String selectedAgenda = (String) agendaComboBox.getSelectedItem();
        
        // Get existing attendance for this group and agenda
        List<Attendance> existingList = attendanceDAO.getAttendanceByGroupAndAgenda(mentorGroup.getId(), selectedAgenda);
        
        // Map user_id to attendance
        java.util.Map<Integer, Attendance> attendanceMap = new java.util.HashMap<>();
        for (Attendance att : existingList) {
            attendanceMap.put(att.getUserId(), att);
        }
        
        // Populate table
        int no = 1;
        for (User mentee : mentees) {
            Attendance att = attendanceMap.get(mentee.getId());
            String status = (att != null) ? att.getStatus() : "hadir";
            String notes = (att != null) ? att.getNotes() : "";
            
            tableModel.addRow(new Object[]{
                no++,
                mentee.getName(),
                mentee.getId(), // NIM is represented by id
                status,
                notes
            });
        }
    }
    
    private void saveAttendance() {
        String selectedAgenda = (String) agendaComboBox.getSelectedItem();
        String dateStr = dateField.getText().trim();
        
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Date date;
        try {
            date = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = true;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int menteeId = (int) tableModel.getValueAt(i, 2);
            String status = (String) tableModel.getValueAt(i, 3);
            String notes = (String) tableModel.getValueAt(i, 4);
            if (notes == null) notes = "";
            
            Attendance attendance = new Attendance(0, menteeId, selectedAgenda, date, status, notes);
            if (!attendanceDAO.saveOrUpdateAttendance(attendance)) {
                success = false;
            }
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Attendance saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAttendanceData();
        } else {
            JOptionPane.showMessageDialog(this, "Some attendance records failed to save.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}