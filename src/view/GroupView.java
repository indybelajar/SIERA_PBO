package view;

import dao.GroupDAO;
import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GroupView extends JPanel {
    private int groupId;
    private User currentUser;
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    
    private JLabel groupNameLabel;
    private JPanel mentorPanel;
    private JLabel mentorNameLabel;
    private JLabel mentorJurusanLabel;
    private JButton hubungiMentorButton;
    
    private JTable memberTable;
    private DefaultTableModel memberTableModel;
    
    private User mentorUser;
    private List<User> menteesList;
    
    public GroupView(int groupId, User currentUser) {
        this.groupId = groupId;
        this.currentUser = currentUser;
        this.groupDAO = new GroupDAO();
        this.userDAO = new UserDAO();
        this.menteesList = new ArrayList<>();
        
        initComponents();
        loadGroupData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // --- 1. TITLE PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        groupNameLabel = new JLabel("KELOMPOK");
        groupNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(groupNameLabel, BorderLayout.WEST);
        
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.LIGHT_GRAY);
        titlePanel.add(sep, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);
        
        // --- 2. CENTER CONTENT PANEL ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Mentor Section Header
        JPanel mentorHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mentorHeaderPanel.setOpaque(false);
        JLabel mentorHeaderLabel = new JLabel("Mentor");
        mentorHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mentorHeaderLabel.setForeground(new Color(15, 23, 42));
        mentorHeaderPanel.add(mentorHeaderLabel);
        centerPanel.add(mentorHeaderPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        
        // Mentor Box Panel (gray card)
        mentorPanel = new JPanel(new BorderLayout(15, 10));
        mentorPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        mentorPanel.setBackground(new Color(230, 230, 230)); // light grey
        mentorPanel.setMaximumSize(new Dimension(850, 75));
        mentorPanel.setPreferredSize(new Dimension(850, 75));
        mentorPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mentorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToMentorProfile();
            }
        });
        
        JPanel mentorTextPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        mentorTextPanel.setOpaque(false);
        
        mentorNameLabel = new JLabel("Belum ada Mentor");
        mentorNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mentorNameLabel.setForeground(new Color(15, 23, 42));
        
        mentorJurusanLabel = new JLabel("-");
        mentorJurusanLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mentorJurusanLabel.setForeground(Color.GRAY);
        
        mentorTextPanel.add(mentorNameLabel);
        mentorTextPanel.add(mentorJurusanLabel);
        mentorPanel.add(mentorTextPanel, BorderLayout.CENTER);
        
        // If current user is a Mentee, add "Hubungi Mentor" button
        if ("mentee".equalsIgnoreCase(currentUser.getRole())) {
            hubungiMentorButton = new JButton("Hubungi Mentor");
            hubungiMentorButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            hubungiMentorButton.setFocusPainted(false);
            hubungiMentorButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            hubungiMentorButton.addActionListener(e -> navigateToMentorProfile());
            
            JPanel btnWrapper = new JPanel(new GridBagLayout());
            btnWrapper.setOpaque(false);
            btnWrapper.add(hubungiMentorButton);
            mentorPanel.add(btnWrapper, BorderLayout.EAST);
        }
        
        centerPanel.add(mentorPanel);
        centerPanel.add(Box.createVerticalStrut(25));
        
        // Mentees Section Header
        JPanel menteesHeaderPanel = new JPanel(new BorderLayout());
        menteesHeaderPanel.setOpaque(false);
        JLabel menteesHeaderLabel = new JLabel("Mentees");
        menteesHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        menteesHeaderLabel.setForeground(new Color(15, 23, 42));
        menteesHeaderPanel.add(menteesHeaderLabel, BorderLayout.WEST);
        
        JSeparator sectionSep = new JSeparator();
        sectionSep.setForeground(Color.LIGHT_GRAY);
        menteesHeaderPanel.add(sectionSep, BorderLayout.SOUTH);
        centerPanel.add(menteesHeaderPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Mentees Table
        // Column mapping based on user role
        String[] columns;
        if ("mentor".equalsIgnoreCase(currentUser.getRole())) {
            columns = new String[]{"No.", "Nama", "NIM", "Jurusan", "Email", "Kontak"};
        } else {
            columns = new String[]{"No.", "Nama", "NIM", "Jurusan", "Email"};
        }
        
        memberTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        memberTable = new JTable(memberTableModel);
        memberTable.setRowHeight(30);
        memberTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        memberTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        memberTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Double-click or single-click row listener to navigate to profile
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow >= 0) {
                    navigateToMenteeProfile(selectedRow);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        centerPanel.add(scrollPane);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void loadGroupData() {
        String groupName = groupDAO.getGroupNameById(groupId);
        groupNameLabel.setText(groupName.toUpperCase());
        
        memberTableModel.setRowCount(0);
        menteesList.clear();
        mentorUser = null;
        
        List<User> members = groupDAO.getGroupMembers(groupId);
        for (User member : members) {
            if ("mentor".equalsIgnoreCase(member.getRole())) {
                mentorUser = member;
            } else {
                menteesList.add(member);
            }
        }
        
        // Populate Mentor Box
        if (mentorUser != null) {
            mentorNameLabel.setText(mentorUser.getName());
            String jurusan = (mentorUser.getJurusan() == null || mentorUser.getJurusan().trim().isEmpty())
                             ? "Jurusan belum diisi" : mentorUser.getJurusan();
            mentorJurusanLabel.setText(jurusan);
        } else {
            mentorNameLabel.setText("Belum ada Mentor");
            mentorJurusanLabel.setText("-");
        }
        
        // Populate Mentees Table
        int no = 1;
        for (User mentee : menteesList) {
            String jurusan = (mentee.getJurusan() == null || mentee.getJurusan().trim().isEmpty())
                             ? "-" : mentee.getJurusan();
            String kontak = (mentee.getKontak() == null || mentee.getKontak().trim().isEmpty())
                            ? "-" : mentee.getKontak();
            
            if ("mentor".equalsIgnoreCase(currentUser.getRole())) {
                memberTableModel.addRow(new Object[]{
                    no++,
                    mentee.getName(),
                    mentee.getId(), // NIM is represented by id
                    jurusan,
                    mentee.getEmail(),
                    kontak
                });
            } else {
                memberTableModel.addRow(new Object[]{
                    no++,
                    mentee.getName(),
                    mentee.getId(), // NIM is represented by id
                    jurusan,
                    mentee.getEmail()
                });
            }
        }
    }
    
    private void navigateToMentorProfile() {
        if (mentorUser == null) return;
        
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof BaseLayout) {
            BaseLayout baseLayout = (BaseLayout) window;
            // Retrieve full user profile
            User fullMentor = userDAO.getUserById(mentorUser.getId());
            if (fullMentor != null) {
                // Keep the fields we loaded in GroupDAO
                fullMentor.setJurusan(mentorUser.getJurusan());
                fullMentor.setKontak(mentorUser.getKontak());
                
                // Show profile (read-only, with back button to Groups)
                baseLayout.showTemporaryPage("Profile_" + fullMentor.getId(), 
                    new ProfileView(fullMentor, false, "Groups"));
            }
        }
    }
    
    private void navigateToMenteeProfile(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= menteesList.size()) return;
        
        User mentee = menteesList.get(rowIndex);
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof BaseLayout) {
            BaseLayout baseLayout = (BaseLayout) window;
            
            User fullMentee = userDAO.getUserById(mentee.getId());
            if (fullMentee != null) {
                fullMentee.setJurusan(mentee.getJurusan());
                fullMentee.setKontak(mentee.getKontak());
                
                baseLayout.showTemporaryPage("Profile_" + fullMentee.getId(), 
                    new ProfileView(fullMentee, false, "Groups"));
            }
        }
    }
}