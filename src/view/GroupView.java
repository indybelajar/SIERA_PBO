package view;

import dao.GroupDAO;
import model.Group;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GroupView extends JPanel {
    private GroupDAO groupDAO;
    private JTable groupTable;
    private JTable memberTable;
    private DefaultTableModel groupTableModel;
    private DefaultTableModel memberTableModel;
    
    public GroupView() {
        groupDAO = new GroupDAO();
        initComponents();
        loadGroups();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);
        
        // Groups panel
        JPanel groupsPanel = new JPanel(new BorderLayout());
        groupsPanel.setBorder(BorderFactory.createTitledBorder("Groups"));
        
        String[] groupColumns = {"ID", "Group Name"};
        groupTableModel = new DefaultTableModel(groupColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        groupTable = new JTable(groupTableModel);
        groupTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadGroupMembers();
            }
        });
        groupsPanel.add(new JScrollPane(groupTable), BorderLayout.CENTER);
        
        // Members panel
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder("Group Members"));
        
        String[] memberColumns = {"ID", "Name", "Email", "Role"};
        memberTableModel = new DefaultTableModel(memberColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(memberTableModel);
        membersPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        
        splitPane.setTopComponent(groupsPanel);
        splitPane.setBottomComponent(membersPanel);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void loadGroups() {
        groupTableModel.setRowCount(0);
        List<Group> groups = groupDAO.getAllGroups();
        for (Group group : groups) {
            groupTableModel.addRow(new Object[]{
                group.getId(),
                group.getGroupName()
            });
        }
    }
    
    private void loadGroupMembers() {
        memberTableModel.setRowCount(0);
        int selectedRow = groupTable.getSelectedRow();
        if (selectedRow >= 0) {
            int groupId = (int) groupTableModel.getValueAt(selectedRow, 0);
            List<User> members = groupDAO.getGroupMembers(groupId);
            for (User member : members) {
                memberTableModel.addRow(new Object[]{
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getRole()
                });
            }
        }
    }
}