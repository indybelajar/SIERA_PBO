package view;

import dao.TaskDAO;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TaskView extends JPanel {
    private int userId;
    private TaskDAO taskDAO;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    
    public TaskView(int userId) {
        this.userId = userId;
        this.taskDAO = new TaskDAO();
        initComponents();
        loadTasks();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("My Tasks"));
        
        String[] columns = {"ID", "Title", "Description", "Deadline", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only action column is editable
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        taskTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
        tablePanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private void loadTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = taskDAO.getAllTasks();
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                "Submit"
            });
        }
    }
    
    // Button renderer for the action column
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    // Button editor for the action column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int taskId;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton("Submit");
            button.addActionListener(e -> {
                new SubmissionForm(taskId, userId).setVisible(true);
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            taskId = (int) table.getValueAt(row, 0);
            return button;
        }
    }
}