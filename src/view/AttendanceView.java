package view;

import dao.AttendanceDAO;
import model.Attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AttendanceView extends JPanel {
    private int userId;
    private AttendanceDAO attendanceDAO;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    
    public AttendanceView(int userId) {
        this.userId = userId;
        this.attendanceDAO = new AttendanceDAO();
        initComponents();
        loadAttendance();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("My Attendance"));
        
        String[] columns = {"ID", "Date", "Status"};
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
    
    private void loadAttendance() {
        tableModel.setRowCount(0);
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByUserId(userId);
        for (Attendance att : attendanceList) {
            tableModel.addRow(new Object[]{
                att.getId(),
                att.getAttendanceDate(),
                att.getStatus()
            });
        }
    }
}