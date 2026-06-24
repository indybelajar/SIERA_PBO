package view;

import dao.TaskDAO;
import model.Task;
import model.TaskSubmission;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskForm extends JPanel {

    // ── Brand colors (mirror BaseLayout / MentorDashboard) ───────────────────
    private static final Color GREEN_PRIMARY  = new Color(34, 166,  90);
    private static final Color GREEN_DARK     = new Color(23, 122,  66);
    private static final Color GREEN_LIGHT    = new Color(232, 245, 238);
    private static final Color GREEN_TEXT     = new Color(22, 101,  52);
    private static final Color BG_PAGE        = new Color(244, 246, 248);
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color TEXT_DARK      = new Color(17,  24,  39);
    private static final Color TEXT_MUTED     = new Color(107, 114, 128);
    private static final Color BORDER_CLR     = new Color(229, 231, 235);
    private static final Color INPUT_BG       = new Color(249, 250, 251);

    // Status badge colors
    private static final Color STATUS_GREEN_BG   = new Color(220, 252, 231);
    private static final Color STATUS_GREEN_FG   = new Color(21,  128,  61);
    private static final Color STATUS_YELLOW_BG  = new Color(254, 243, 199);
    private static final Color STATUS_YELLOW_FG  = new Color(146,  64,  14);
    private static final Color STATUS_RED_BG     = new Color(254, 226, 226);
    private static final Color STATUS_RED_FG     = new Color(185,  28,  28);
    private static final Color STATUS_GRAY_BG    = new Color(243, 244, 246);
    private static final Color STATUS_GRAY_FG    = new Color(107, 114, 128);

    private int groupId;
    private TaskDAO taskDAO;

    // Form fields
    private JTextField titleField;
    private JTextArea  descriptionArea;
    private JTextField deadlineField;

    // Monitoring
    private JComboBox<TaskWrapper> taskSelector;
    private JTable        submissionTable;
    private DefaultTableModel tableModel;
    private boolean isUpdatingTable = false;
    private boolean isCreateFormExpanded = false;

    // ────────────────────────────────────────────────────────────────────────
    public TaskForm(int groupId) {
        this.groupId = groupId;
        this.taskDAO  = new TaskDAO();
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        setOpaque(true);
        initComponents();
        loadGroupTasks();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UI BUILD
    // ════════════════════════════════════════════════════════════════════════
    private void initComponents() {

        // ── Top bar (title + bell) ────────────────────────────────────────────
        add(buildTopBar(), BorderLayout.NORTH);

        // ── Scrollable body ───────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(24, 28, 24, 28));

        body.add(buildCreateCard());
        body.add(Box.createVerticalStrut(20));
        body.add(buildMonitorCard());
        body.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_PAGE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        scroll.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = scroll.getWidth();
                int side = Math.max(28, (w - 920) / 2);
                body.setBorder(new EmptyBorder(24, side, 24, side));
                body.revalidate();
            }
        });
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(16, 28, 16, 28)));

        // Left: icon + title + subtitle
        JPanel left = new JPanel(new BorderLayout(12, 0));
        left.setOpaque(false);

        // Green task icon circle
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(44, 44));
        JLabel iconLbl = new JLabel("\uD83D\uDCCB"); // 📋
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconCircle.add(iconLbl);

        JPanel textBlock = new JPanel(new BorderLayout(0, 4));
        textBlock.setOpaque(false);
        JLabel titleLbl = new JLabel("Task Management");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(TEXT_DARK);
        JLabel subLbl = new JLabel("Buat dan kelola tugas untuk mentee dalam program PATRIBERA.");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(TEXT_MUTED);
        textBlock.add(titleLbl, BorderLayout.NORTH);
        textBlock.add(subLbl,   BorderLayout.SOUTH);

        left.add(iconCircle, BorderLayout.WEST);
        left.add(textBlock,  BorderLayout.CENTER);

        // Right: bell + badge
        JPanel bellWrap = new JPanel(null);
        bellWrap.setOpaque(false);
        bellWrap.setPreferredSize(new Dimension(38, 38));
        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        bell.setBounds(0, 4, 30, 30);
        JPanel redDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 68, 68));
                g2.fillOval(0, 0, 10, 10);
                g2.dispose();
            }
        };
        redDot.setBounds(18, 0, 10, 10);
        redDot.setOpaque(false);
        bellWrap.add(bell); bellWrap.add(redDot);

        bar.add(left,     BorderLayout.WEST);
        bar.add(bellWrap, BorderLayout.EAST);
        return bar;
    }

    // ── Card: Buat Tugas Baru ─────────────────────────────────────────────────
    private JPanel buildCreateCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 16));

        // Card header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);
        JPanel hIcon = sectionIconCircle("\uD83D\uDCCB"); // 📋
        JLabel hLbl = new JLabel("Buat Tugas Baru");
        hLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hLbl.setForeground(TEXT_DARK);
        leftHeader.add(hIcon); leftHeader.add(hLbl);

        JLabel toggleChevron = new JLabel("▶  ");
        toggleChevron.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toggleChevron.setForeground(TEXT_MUTED);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(toggleChevron, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setVisible(false); // Collapsed by default
        
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1;

        // Judul
        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        form.add(fieldLabel("Judul"), g);
        titleField = styledTextField("Masukkan judul tugas");
        g.gridy = 1; g.insets = new Insets(0, 0, 14, 0);
        form.add(titleField, g);

        // Deskripsi
        g.gridy = 2; g.insets = new Insets(0, 0, 4, 0);
        form.add(fieldLabel("Deskripsi"), g);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setForeground(TEXT_DARK);
        descriptionArea.setBackground(CARD_BG);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(8, 10, 8, 10)));
        // Placeholder
        descriptionArea.setText("Masukkan deskripsi tugas");
        descriptionArea.setForeground(new Color(156, 163, 175));
        descriptionArea.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (descriptionArea.getText().equals("Masukkan deskripsi tugas")) {
                    descriptionArea.setText("");
                    descriptionArea.setForeground(TEXT_DARK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (descriptionArea.getText().trim().isEmpty()) {
                    descriptionArea.setText("Masukkan deskripsi tugas");
                    descriptionArea.setForeground(new Color(156, 163, 175));
                }
            }
        });
        g.gridy = 3; g.insets = new Insets(0, 0, 14, 0);
        form.add(descriptionArea, g);

        // Deadline row
        g.gridy = 4; g.insets = new Insets(0, 0, 4, 0);
        form.add(fieldLabel("Deadline"), g);

        // Deadline field with calendar icon
        JPanel deadlineRow = new JPanel(new BorderLayout(0, 0));
        deadlineRow.setOpaque(false);

        JPanel deadlineInputWrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g2d) {
                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        deadlineInputWrapper.setOpaque(false);
        deadlineInputWrapper.setPreferredSize(new Dimension(220, 40));
        deadlineInputWrapper.setMaximumSize(new Dimension(280, 40));

        JLabel calIcon = new JLabel();
        calIcon.setIcon(new SvgIcon(SvgIcon.Type.DEADLINE, 16, TEXT_MUTED));
        calIcon.setBorder(new EmptyBorder(0, 10, 0, 4));

        deadlineField = new JTextField();
        deadlineField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deadlineField.setForeground(TEXT_DARK);
        deadlineField.setBorder(null);
        deadlineField.setOpaque(false);
        deadlineField.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " 23:59");

        deadlineInputWrapper.add(calIcon,      BorderLayout.WEST);
        deadlineInputWrapper.add(deadlineField, BorderLayout.CENTER);

        // "+" Buat Tugas button
        JButton createBtn = greenButton("+ Buat Tugas");
        createBtn.addActionListener(e -> createTask());

        JPanel deadlineAndBtn = new JPanel(new BorderLayout(0, 0));
        deadlineAndBtn.setOpaque(false);
        deadlineAndBtn.add(deadlineInputWrapper, BorderLayout.WEST);

        JPanel btnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRight.setOpaque(false);
        btnRight.add(createBtn);
        deadlineAndBtn.add(btnRight, BorderLayout.EAST);

        g.gridy = 5; g.insets = new Insets(0, 0, 0, 0);
        form.add(deadlineAndBtn, g);

        card.add(form, BorderLayout.CENTER);

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isCreateFormExpanded = !isCreateFormExpanded;
                form.setVisible(isCreateFormExpanded);
                toggleChevron.setText(isCreateFormExpanded ? "▼  " : "▶  ");
                card.revalidate();
                card.repaint();
            }
        });

        return card;
    }

    // ── Card: Tugas Mentee ────────────────────────────────────────────────────
    private JPanel buildMonitorCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 16));

        // Card header
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLeft.setOpaque(false);
        JPanel hIcon = sectionIconCircle("\uD83D\uDC65"); // 👥
        JLabel hLbl = new JLabel("Tugas Mentee");
        hLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hLbl.setForeground(TEXT_DARK);
        headerLeft.add(hIcon); headerLeft.add(hLbl);

        JLabel hSub = new JLabel("Daftar tugas dan status pengumpulan oleh mentee.");
        hSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hSub.setForeground(TEXT_MUTED);

        JPanel headerBlock = new JPanel(new BorderLayout(0, 4));
        headerBlock.setOpaque(false);
        headerBlock.add(headerLeft, BorderLayout.NORTH);
        headerBlock.add(hSub, BorderLayout.SOUTH);
        card.add(headerBlock, BorderLayout.NORTH);

        // Pilih Tugas row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        JLabel filterLbl = new JLabel("Pilih Tugas");
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterLbl.setForeground(TEXT_DARK);

        // Override UIManager agar popup list JComboBox tidak gelap
        UIManager.put("ComboBox.background",            CARD_BG);
        UIManager.put("ComboBox.foreground",            TEXT_DARK);
        UIManager.put("ComboBox.selectionBackground",   GREEN_LIGHT);
        UIManager.put("ComboBox.selectionForeground",   GREEN_TEXT);
        UIManager.put("ComboBox.disabledBackground",    CARD_BG);
        UIManager.put("List.background",                CARD_BG);
        UIManager.put("List.foreground",                TEXT_DARK);
        UIManager.put("List.selectionBackground",       GREEN_LIGHT);
        UIManager.put("List.selectionForeground",       GREEN_TEXT);

        taskSelector = new JComboBox<>();
        taskSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taskSelector.setBackground(CARD_BG);
        taskSelector.setForeground(TEXT_DARK);
        taskSelector.setPreferredSize(new Dimension(240, 36));
        taskSelector.setMaximumSize(new Dimension(320, 36));
        taskSelector.setOpaque(true);
        // Paksa background putih di semua Look & Feel
        taskSelector.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 4, 0, 0)));
        try {
            for (javax.swing.plaf.ComponentUI ui : new javax.swing.plaf.ComponentUI[]{}) {}
            taskSelector.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override protected JButton createArrowButton() {
                    JButton arrow = new JButton("\u25BE"); // ▾
                    arrow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    arrow.setBackground(GREEN_PRIMARY);
                    arrow.setForeground(Color.WHITE);
                    arrow.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    arrow.setFocusPainted(false);
                    arrow.setContentAreaFilled(true);
                    return arrow;
                }
            });
        } catch (Exception ignored) {}
        // Custom renderer — background putih, selected = hijau muda
        taskSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(new EmptyBorder(7, 12, 7, 12));
                if (isSelected) {
                    lbl.setBackground(GREEN_LIGHT);
                    lbl.setForeground(GREEN_TEXT);
                } else {
                    lbl.setBackground(CARD_BG);
                    lbl.setForeground(TEXT_DARK);
                }
                return lbl;
            }
        });
        taskSelector.addActionListener(e -> loadSubmissions());

        filterRow.add(filterLbl);
        filterRow.add(taskSelector);

        // Table
        String[] columns = {"No.", "Mentee", "NIM", "Submission", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 4; // only Status editable
            }
            @Override public Class<?> getColumnClass(int col) {
                return String.class;
            }
        };

        submissionTable = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(249, 250, 251));
                }
                return c;
            }
        };
        submissionTable.setRowHeight(52);
        submissionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        submissionTable.setShowGrid(true);
        submissionTable.setGridColor(BORDER_CLR);
        submissionTable.setIntercellSpacing(new Dimension(0, 1));
        submissionTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        submissionTable.getTableHeader().setReorderingAllowed(false);
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setOpaque(true);
                lbl.setBackground(new Color(249, 250, 251));
                lbl.setForeground(TEXT_MUTED);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, BORDER_CLR), new EmptyBorder(0, 15, 0, 15)
                ));
                if (c == 0 || c == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        };
        for (int i = 0; i < submissionTable.getColumnModel().getColumnCount(); i++) {
            submissionTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        submissionTable.setSelectionBackground(new Color(220, 252, 231));
        submissionTable.setSelectionForeground(TEXT_DARK);

        // Column widths - 5 columns (no Aksi)
        submissionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        submissionTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        submissionTable.getColumnModel().getColumn(0).setMaxWidth(60);
        submissionTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        submissionTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        submissionTable.getColumnModel().getColumn(2).setMaxWidth(120);
        submissionTable.getColumnModel().getColumn(3).setPreferredWidth(260);
        submissionTable.getColumnModel().getColumn(4).setPreferredWidth(140);

        // Center renderer untuk kolom No. dan NIM
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JPanel cell = new JPanel(new GridBagLayout());
                cell.setBackground(sel ? t.getSelectionBackground() : (row % 2 == 0 ? CARD_BG : new Color(249, 250, 251)));
                JLabel lbl = new JLabel(val == null ? "" : val.toString());
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setForeground(sel ? t.getSelectionForeground() : TEXT_DARK);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                cell.add(lbl);
                return cell;
            }
        };
        submissionTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        submissionTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Status dropdown editor
        String[] statusOpts = {"Terkirim", "Diterima", "Ditolak"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOpts);
        statusCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(6, 10, 6, 10));
                if (value != null) {
                    String status = value.toString();
                    Color bg, fg;
                    switch (status) {
                        case "Diterima" -> { bg = STATUS_GREEN_BG; fg = STATUS_GREEN_FG; }
                        case "Terkirim" -> { bg = STATUS_GRAY_BG; fg = STATUS_GRAY_FG; }
                        case "Ditolak"  -> { bg = STATUS_RED_BG; fg = STATUS_RED_FG; }
                        default         -> { bg = STATUS_GRAY_BG; fg = STATUS_GRAY_FG; }
                    }
                    if (isSelected) {
                        label.setBackground(bg.darker());
                        label.setForeground(Color.WHITE);
                    } else {
                        label.setBackground(bg);
                        label.setForeground(fg);
                    }
                }
                return label;
            }
        });
        submissionTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusCombo));

        // Status badge renderer
        submissionTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        // Submission link renderer (blue link + Google icon)
        submissionTable.getColumnModel().getColumn(3).setCellRenderer(new LinkRenderer());

        // Mentee name renderer (with avatar circle)
        submissionTable.getColumnModel().getColumn(1).setCellRenderer(new MenteeNameRenderer());

        // Table model listener for status change
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && !isUpdatingTable) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 4) saveRowStatus(row);
            }
        });

        JScrollPane tableScroll = new JScrollPane(submissionTable);
        tableScroll.setBorder(new LineBorder(BORDER_CLR, 1, true));
        tableScroll.getViewport().setBackground(CARD_BG);
        tableScroll.setBackground(CARD_BG);
        // Area kosong di bawah baris terakhir juga harus putih
        submissionTable.setFillsViewportHeight(true);
        submissionTable.setBackground(CARD_BG);
        // Corner kanan atas (antara header dan scrollbar) putih
        tableScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
            createFillerPanel(new Color(249, 250, 251)));
        tableScroll.getVerticalScrollBar().setBackground(BG_PAGE);
        tableScroll.setPreferredSize(new Dimension(0, 220));

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(filterRow,   BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);

        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  BUSINESS LOGIC (unchanged from original)
    // ════════════════════════════════════════════════════════════════════════
    private void loadGroupTasks() {
        taskSelector.removeAllItems();
        List<Task> tasks = taskDAO.getTasksByGroupId(groupId);
        for (Task task : tasks) taskSelector.addItem(new TaskWrapper(task));
        loadSubmissions();
    }

    private void loadSubmissions() {
        TaskWrapper wrapper = (TaskWrapper) taskSelector.getSelectedItem();
        isUpdatingTable = true;
        tableModel.setRowCount(0);
        if (wrapper == null) { isUpdatingTable = false; return; }

        List<TaskSubmission> list = taskDAO.getSubmissionsByTaskId(wrapper.getTask().getId());
        int no = 1;
        for (TaskSubmission sub : list) {
            String link = (sub.getSubmissionLink() == null || sub.getSubmissionLink().trim().isEmpty())
                          ? "-" : sub.getSubmissionLink();
            tableModel.addRow(new Object[]{
                no++,
                sub.getUserName(),
                sub.getUserId(),
                link,
                mapStatusToDisplay(sub.getStatus())
            });
        }
        isUpdatingTable = false;
    }

    private void createTask() {
        String title = titleField.getText().trim();
        String desc  = descriptionArea.getText().trim();
        String dl    = deadlineField.getText().trim();

        if (title.isEmpty() || desc.isEmpty() || dl.isEmpty()
                || desc.equals("Masukkan deskripsi tugas")) {
            ModernDialog.showError(this, "Harap isi semua kolom!", "Error");
            return;
        }

        // Accept dd/MM/yyyy HH:mm or YYYY-MM-DD HH:MM:SS
        Timestamp deadline;
        try {
            String normalized = dl;
            if (dl.matches("\\d{2}/\\d{2}/\\d{4}.*")) {
                String[] parts = dl.split(" ");
                String[] d = parts[0].split("/");
                normalized = d[2] + "-" + d[1] + "-" + d[0] + (parts.length > 1 ? " " + parts[1] + ":00" : " 23:59:00");
            } else if (normalized.length() == 10) {
                normalized += " 23:59:00";
            }
            deadline = java.sql.Timestamp.valueOf(normalized);
        } catch (Exception ex) {
            ModernDialog.showError(this, "Format tanggal salah! Gunakan DD/MM/YYYY HH:mm", "Error");
            return;
        }

        Task task = new Task(0, groupId, title, desc, deadline);
        if (taskDAO.createTask(task)) {
            ModernDialog.showInfo(this, "Tugas berhasil dibuat!", "Berhasil");
            titleField.setText("");
            descriptionArea.setText("Masukkan deskripsi tugas");
            descriptionArea.setForeground(new Color(156, 163, 175));
            deadlineField.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " 23:59");
            loadGroupTasks();
        } else {
            ModernDialog.showError(this, "Gagal membuat tugas!", "Error");
        }
    }

    private void saveRowStatus(int row) {
        TaskWrapper wrapper = (TaskWrapper) taskSelector.getSelectedItem();
        if (wrapper == null) return;
        int taskId   = wrapper.getTask().getId();
        long menteeId = (long) tableModel.getValueAt(row, 2);
        String db    = mapDisplayToStatus((String) tableModel.getValueAt(row, 4));
        if (!taskDAO.updateSubmissionStatus(taskId, menteeId, db)) {
            ModernDialog.showError(this, "Gagal mengupdate status!", "Error");
        }
    }

    private String mapStatusToDisplay(String s) {
        if (s == null) return "Pending";
        return switch (s) {
            case "Submitted" -> "Terkirim";
            case "Accepted"  -> "Diterima";
            case "Rejected"  -> "Ditolak";
            default          -> "Pending";
        };
    }

    private String mapDisplayToStatus(String s) {
        if (s == null) return "Pending";
        return switch (s) {
            case "Terkirim" -> "Submitted";
            case "Diterima" -> "Accepted";
            case "Ditolak"  -> "Rejected";
            default         -> "Pending";
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CUSTOM CELL RENDERERS
    // ════════════════════════════════════════════════════════════════════════

    /** Colored pill badge for Status column */
    private class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JPanel pill = new JPanel(new GridBagLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g2.dispose();
                }
            };
            pill.setOpaque(false);

            String status = val == null ? "Pending" : val.toString();
            Color bg, fg;
            switch (status) {
                case "Diterima" -> { bg = STATUS_GREEN_BG;  fg = STATUS_GREEN_FG; }
                case "Terkirim" -> { bg = STATUS_GRAY_BG;   fg = STATUS_GRAY_FG; }
                case "Ditolak"  -> { bg = STATUS_RED_BG;    fg = STATUS_RED_FG; }
                default         -> { bg = STATUS_GRAY_BG;   fg = STATUS_GRAY_FG; }
            }

            pill.setBackground(bg);
            JLabel lbl = new JLabel(status);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(fg);
            pill.setBorder(new EmptyBorder(4, 10, 4, 10));
            pill.add(lbl);

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(sel ? t.getSelectionBackground() : (row % 2 == 0 ? CARD_BG : new Color(249, 250, 251)));
            wrapper.add(pill);
            return wrapper;
        }
    }

    /** Blue link with Google Drive icon for Submission column */
    private class LinkRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            // Outer: GridBagLayout agar center vertikal
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? t.getSelectionBackground() : (row % 2 == 0 ? CARD_BG : new Color(249, 250, 251)));

            String link = val == null ? "-" : val.toString();

            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            inner.setOpaque(false);

            if (!link.equals("-") && link.length() > 0) {
                JLabel icon = new JLabel("\uD83D\uDCC4"); // 📄
                icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                icon.setForeground(new Color(66, 133, 244));
                JLabel lbl = new JLabel(link.length() > 24 ? link.substring(0, 22) + "..." : link);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setForeground(sel ? t.getSelectionForeground() : new Color(59, 130, 246));
                inner.add(icon); inner.add(lbl);
            } else {
                JLabel dash = new JLabel("—");
                dash.setForeground(sel ? t.getSelectionForeground() : TEXT_MUTED);
                dash.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                inner.add(dash);
            }

            cell.add(inner);
            return cell;
        }
    }

    /** Name only, left-aligned for Mentee column */
    private class MenteeNameRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(sel ? t.getSelectionBackground() : (row % 2 == 0 ? CARD_BG : new Color(249, 250, 251)));

            String name = val == null ? "" : val.toString();
            JLabel lbl = new JLabel(name);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(sel ? t.getSelectionForeground() : TEXT_DARK);
            lbl.setHorizontalAlignment(SwingConstants.LEFT);

            cell.add(lbl, BorderLayout.CENTER);
            cell.setBorder(new EmptyBorder(0, 16, 0, 16));
            return cell;
        }
    }

    /** ⋮ dots button for Aksi column */
    private static class AksiRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
            // Pakai JButton agar tampil seperti tombol kecil
            JButton dotsBtn = new JButton("•••");
            dotsBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            dotsBtn.setForeground(new Color(107, 114, 128));
            dotsBtn.setBackground(new Color(243, 244, 246));
            dotsBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(2, 6, 2, 6)));
            dotsBtn.setFocusPainted(false);
            dotsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cell.add(dotsBtn);
            return cell;
        }
    }

    private static class AksiEditor extends DefaultCellEditor {
        private final JPanel cell;
        private final TaskForm parent;

        AksiEditor(JCheckBox cb, TaskForm parent) {
            super(cb);
            this.parent = parent;
            cell = new JPanel(new GridBagLayout());
            cell.setBackground(Color.WHITE);
            JButton dotsBtn = new JButton("•••");
            dotsBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            dotsBtn.setForeground(new Color(107, 114, 128));
            dotsBtn.setBackground(new Color(243, 244, 246));
            dotsBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(2, 6, 2, 6)));
            dotsBtn.setFocusPainted(false);
            cell.add(dotsBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
            // Show a quick popup with status options
            SwingUtilities.invokeLater(() -> {
                JPopupMenu popup = new JPopupMenu();
                for (String opt : new String[]{"Pending","Terkirim","Diterima","Ditolak"}) {
                    JMenuItem item = new JMenuItem(opt);
                    item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    item.addActionListener(e -> {
                        parent.tableModel.setValueAt(opt, row, 4);
                        stopCellEditing();
                    });
                    popup.add(item);
                }
                popup.show(t, t.getCellRect(row, col, true).x,
                              t.getCellRect(row, col, true).y + t.getRowHeight());
            });
            return cell;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  WIDGET HELPERS  (consistent with MentorDashboard style)
    // ════════════════════════════════════════════════════════════════════════

    private JPanel roundCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        return p;
    }

    private JPanel sectionIconCircle(String emoji) {
        JPanel ic = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ic.setOpaque(false);
        ic.setPreferredSize(new Dimension(32, 32));
        JLabel lbl = new JLabel(emoji);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        lbl.setForeground(GREEN_TEXT);
        ic.add(lbl);
        return ic;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(new Color(156, 163, 175));
        f.setBackground(CARD_BG);
        f.setCaretColor(GREEN_PRIMARY);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(9, 10, 9, 10)));
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT_DARK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().trim().isEmpty()) { f.setText(placeholder); f.setForeground(new Color(156, 163, 175)); }
            }
        });
        return f;
    }

    private JButton greenButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? GREEN_DARK : GREEN_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    /** Solid-color filler panel — dipakai untuk corner JScrollPane */
    private JPanel createFillerPanel(Color color) {
        JPanel p = new JPanel();
        p.setBackground(color);
        p.setOpaque(true);
        return p;
    }

    // ── Task wrapper for combo ────────────────────────────────────────────────
    private static class TaskWrapper {
        private final Task task;
        TaskWrapper(Task t) { this.task = t; }
        Task getTask() { return task; }
        @Override public String toString() {
            return task.getTitle();
        }
    }
}