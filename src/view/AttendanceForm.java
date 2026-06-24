package view;

import dao.AttendanceDAO;
import dao.GroupDAO;
import model.Attendance;
import model.User;
import model.Group;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceForm extends JPanel {

    // ── Brand colors (same as MentorDashboard / BaseLayout) ──────────
    private static final Color BG_PAGE       = new Color(0xF4, 0xF6, 0xF8);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color BORDER_CLR    = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT_DARK     = new Color(0x1A, 0x1A, 0x2E);
    private static final Color TEXT_MUTED    = new Color(0x6B, 0x72, 0x80);
    private static final Color GREEN_PRIMARY = new Color(34,  166, 90);
    private static final Color GREEN_DARK    = new Color(23,  122, 66);
    private static final Color GREEN_LIGHT   = new Color(232, 245, 238);
    private static final Color RED_DOT       = new Color(0xEF, 0x44, 0x44);
    private static final Color YELLOW_BG     = new Color(0xFE, 0xF3, 0xC7);
    private static final Color YELLOW_FG     = new Color(0xB4, 0x78, 0x00);

    // ── State ─────────────────────────────────────────────────────────
    private long mentorId;
    private AttendanceDAO attendanceDAO;
    private GroupDAO groupDAO;

    private JTextField groupNameField;
    private JComboBox<String> agendaComboBox;
    private JTextField dateField;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    private Group mentorGroup;
    private List<User> mentees;

    // Stat labels (top cards)
    private JLabel totalMenteeLbl;
    private JLabel belumDinilaiLbl;
    private JLabel kehadiranLbl;
    private JLabel kehadiranSubLbl;

    // ── Constructor ───────────────────────────────────────────────────
    public AttendanceForm(long mentorId) {
        this.mentorId    = mentorId;
        this.attendanceDAO = new AttendanceDAO();
        this.groupDAO      = new GroupDAO();
        this.mentees       = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        add(createTopBar(),   BorderLayout.NORTH);
        JPanel body = createBody();
        add(body,     BorderLayout.CENTER);

        loadGroupAndMentees();
        loadAttendanceData();
        refreshStats();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int side = Math.max(28, (w - 920) / 2);
                body.setBorder(new EmptyBorder(24, side, 24, side));
                revalidate();
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  TOP BAR  (title + bell icon)
    // ══════════════════════════════════════════════════════════════════
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(16, 28, 16, 28)
        ));

        // Left: title + subtitle
        JPanel left = new JPanel(new BorderLayout(0, 3));
        left.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Attendance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        JLabel wave = new JLabel("\uD83D\uDCC5");  // 📅
        wave.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        titleRow.add(title);
        titleRow.add(wave);

        JLabel subtitle = new JLabel("Kelola kehadiran mentee dengan mudah dan terstruktur");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);

        left.add(titleRow, BorderLayout.NORTH);
        left.add(subtitle, BorderLayout.SOUTH);

        // Right: bell + badge
        JPanel bellWrap = new JPanel(null);
        bellWrap.setPreferredSize(new Dimension(38, 38));
        bellWrap.setOpaque(false);
        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        bell.setBounds(0, 4, 30, 30);
        JPanel badge = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED_DOT);
                g2.fillOval(0, 0, 10, 10);
                g2.dispose();
            }
        };
        badge.setBounds(18, 0, 10, 10);
        badge.setOpaque(false);
        bellWrap.add(bell);
        bellWrap.add(badge);

        bar.add(left,     BorderLayout.WEST);
        bar.add(bellWrap, BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════
    //  SCROLLABLE BODY
    // ══════════════════════════════════════════════════════════════════
    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Row 1: info cards (Group Name, Agenda, Date)
        body.add(createInfoCardsRow(), BorderLayout.NORTH);

        // Row 2: stat row + mentee table (center)
        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(createStatCardsRow(),  BorderLayout.NORTH);
        center.add(createMenteeCard(),    BorderLayout.CENTER);

        // Wrap center in scroll
        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setOpaque(false);
        scrollWrapper.add(center, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(scrollWrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        body.add(scroll, BorderLayout.CENTER);

        // Bottom: Save button
        body.add(createBottomBar(), BorderLayout.SOUTH);
        return body;
    }

    // ══════════════════════════════════════════════════════════════════
    //  INFO CARDS ROW  (Group Name | Agenda | Date)
    // ══════════════════════════════════════════════════════════════════
    private JPanel createInfoCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);

        // Group Name card
        groupNameField = new JTextField();
        groupNameField.setEditable(false);
        groupNameField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        groupNameField.setForeground(TEXT_DARK);
        groupNameField.setBorder(null);
        groupNameField.setOpaque(false);
        row.add(createInfoCard("\uD83D\uDC65", "Group Name", groupNameField)); // 👥

        // Agenda card
        String[] agendas = {"mentoring 1", "mentoring 2", "mentoring 3", "patribera day 1", "patribera day 2"};
        agendaComboBox = new JComboBox<>(agendas);
        agendaComboBox.setFont(new Font("Segoe UI", Font.BOLD, 15));
        agendaComboBox.setForeground(TEXT_DARK);
        agendaComboBox.setBackground(CARD_BG);
        agendaComboBox.setBorder(null);
        agendaComboBox.addActionListener(e -> { loadAttendanceData(); refreshStats(); });
        row.add(createInfoCard("\uD83D\uDCCB", "Agenda", agendaComboBox)); // 📋

        // Date card
        dateField = new JTextField(LocalDate.now().toString());
        dateField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dateField.setForeground(TEXT_DARK);
        dateField.setBorder(null);
        dateField.setOpaque(false);
        row.add(createInfoCard("\uD83D\uDCC5", "Date", dateField)); // 📅

        return row;
    }

    /** Builds one rounded info card with an icon label, header label, and a value component. */
    private JPanel createInfoCard(String emoji, String header, JComponent valueComp) {
        JPanel card = new RoundedCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Icon circle
        JPanel circle = buildIconCircle(emoji, 36);

        // Text area
        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.setOpaque(false);
        JLabel headerLbl = new JLabel(header);
        headerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        headerLbl.setForeground(TEXT_MUTED);
        text.add(headerLbl,  BorderLayout.NORTH);
        text.add(valueComp,  BorderLayout.SOUTH);

        card.add(circle, BorderLayout.WEST);
        card.add(text,   BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  STAT CARDS ROW  (Total Mentee | Belum Dinilai | Kehadiran %)
    // ══════════════════════════════════════════════════════════════════
    private JPanel createStatCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);

        // Total Mentee
        totalMenteeLbl = new JLabel("0");
        totalMenteeLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        totalMenteeLbl.setForeground(GREEN_PRIMARY);
        row.add(createStatCard("\uD83D\uDC65", "Total Mentee", "Lihat detail", totalMenteeLbl, false));

        // Belum Dinilai
        belumDinilaiLbl = new JLabel("0");
        belumDinilaiLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        belumDinilaiLbl.setForeground(GREEN_PRIMARY);
        row.add(createStatCard("\u23F1", "Belum Dinilai", "Lihat detail", belumDinilaiLbl, false));

        // Kehadiran %
        kehadiranLbl    = new JLabel("0%");
        kehadiranLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        kehadiranLbl.setForeground(GREEN_PRIMARY);
        kehadiranSubLbl = new JLabel("PATRIBERA D.A.");
        row.add(createStatCard("\u2714", "Kehadiran", null, kehadiranLbl, true));

        return row;
    }

    private JPanel createStatCard(String emoji, String title, String subtitle,
                                   JLabel valueLbl, boolean showArrow) {
        JPanel card = new RoundedCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header: icon + title + optional arrow
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JPanel circle = buildIconCircle(emoji, 34);

        JPanel titles = new JPanel(new BorderLayout(0, 2));
        titles.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        titles.add(titleLbl, BorderLayout.NORTH);
        if (subtitle != null) {
            JLabel subLbl = new JLabel(subtitle);
            subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            subLbl.setForeground(TEXT_MUTED);
            titles.add(subLbl, BorderLayout.SOUTH);
        }

        left.add(circle);
        left.add(titles);

        if (showArrow) {
            JLabel arrow = new JLabel("\u2197");
            arrow.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            arrow.setForeground(TEXT_MUTED);
            header.add(arrow, BorderLayout.EAST);
        }
        header.add(left, BorderLayout.CENTER);

        card.add(header,   BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  MENTEE TABLE CARD
    // ══════════════════════════════════════════════════════════════════
    private JPanel createMenteeCard() {
        JPanel card = new RoundedCard();
        card.setLayout(new BorderLayout(0, 0));

        // Card header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.setBorder(new EmptyBorder(18, 20, 14, 20));

        JPanel headerLeft = new JPanel(new BorderLayout(0, 3));
        headerLeft.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        JLabel icon = new JLabel("\uD83D\uDC65");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel titleLbl = new JLabel("Mentees");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(TEXT_DARK);
        titleRow.add(icon);
        titleRow.add(titleLbl);

        JLabel subLbl = new JLabel("Daftar kehadiran mentee pada agenda PATRIBERA");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(TEXT_MUTED);
        headerLeft.add(titleRow, BorderLayout.NORTH);
        headerLeft.add(subLbl,   BorderLayout.SOUTH);

        cardHeader.add(headerLeft, BorderLayout.WEST);

        // Separator
        JPanel sep = new JPanel();
        sep.setBackground(BORDER_CLR);
        sep.setPreferredSize(new Dimension(0, 1));

        // Table (Kolom Aksi Dihapus)
        String[] columns = {"No.", "Nama", "NIM", "Status", "Catatan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 4;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(52);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // PENGATURAN GRID PADAT
        attendanceTable.setOpaque(true);
        attendanceTable.setBackground(CARD_BG);
        attendanceTable.setForeground(TEXT_DARK);
        attendanceTable.setShowGrid(true);
        attendanceTable.setGridColor(BORDER_CLR);
        attendanceTable.setIntercellSpacing(new Dimension(1, 1));
        
        attendanceTable.setSelectionBackground(GREEN_LIGHT);
        attendanceTable.setSelectionForeground(TEXT_DARK);
        attendanceTable.setFillsViewportHeight(true);

        // Header style
        JTableHeader th = attendanceTable.getTableHeader();
        th.setPreferredSize(new Dimension(0, 45));
        th.setReorderingAllowed(false);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setOpaque(true);
                lbl.setBackground(new Color(0xF9, 0xFA, 0xFB));
                lbl.setForeground(TEXT_MUTED);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        for (int i = 0; i < attendanceTable.getColumnModel().getColumnCount(); i++) {
            attendanceTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Column widths yang direkalibrasi
        int[] widths = {50, 250, 100, 150, 250};
        for (int i = 0; i < widths.length; i++) {
            attendanceTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        attendanceTable.getColumnModel().getColumn(0).setMaxWidth(60);
        attendanceTable.getColumnModel().getColumn(2).setMaxWidth(120);

        // Status combo editor with styled badge
        String[] statusOptions = {"hadir", "izin", "sakit", "tanpa keterangan"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof String) {
                    String str = (String) value;
                    if ("hadir".equals(str)) setText("Hadir");
                    else if ("izin".equals(str)) setText("Izin");
                    else if ("sakit".equals(str)) setText("Sakit");
                    else if ("tanpa keterangan".equals(str)) setText("Tanpa Keterangan");
                }
                return this;
            }
        });
        attendanceTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusCombo));

        // Standard Renderer untuk memastikan padding yang rapi di sel biasa
        DefaultTableCellRenderer standardRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSel, focus, r, c);
                lbl.setOpaque(true);
                if (isSel) {
                    lbl.setBackground(table.getSelectionBackground());
                    lbl.setForeground(table.getSelectionForeground());
                } else {
                    lbl.setBackground(CARD_BG);
                    lbl.setForeground(TEXT_DARK);
                }
                lbl.setBorder(new EmptyBorder(0, 15, 0, 15));
                if (c == 0 || c == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        };

        attendanceTable.getColumnModel().getColumn(0).setCellRenderer(standardRenderer);
        attendanceTable.getColumnModel().getColumn(2).setCellRenderer(standardRenderer);
        attendanceTable.getColumnModel().getColumn(4).setCellRenderer(standardRenderer);

        // Nama renderer (with avatar circle)
        attendanceTable.getColumnModel().getColumn(1).setCellRenderer(new AvatarNameRenderer());

        // Status badge renderer
        attendanceTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setBackground(CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Make scrollPane fill the card properly
        card.setLayout(new BorderLayout());
        card.add(cardHeader, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  BOTTOM BAR  (Save button)
    // ══════════════════════════════════════════════════════════════════
    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton saveBtn = buildGreenButton("Save");
        saveBtn.setIcon(new SvgIcon(SvgIcon.Type.SAVE, 16, Color.WHITE));
        saveBtn.setIconTextGap(8);
        saveBtn.setPreferredSize(new Dimension(130, 40));
        saveBtn.addActionListener(e -> saveAttendance());
        bar.add(saveBtn);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════
    //  DATA METHODS
    // ══════════════════════════════════════════════════════════════════
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
        if (mentorGroup == null || mentees.isEmpty()) return;
        tableModel.setRowCount(0);
        String selectedAgenda = (String) agendaComboBox.getSelectedItem();

        List<Attendance> existingList =
            attendanceDAO.getAttendanceByGroupAndAgenda(mentorGroup.getId(), selectedAgenda);

        java.util.Map<Long, Attendance> map = new java.util.HashMap<>();
        for (Attendance att : existingList) map.put(att.getUserId(), att);

        int no = 1;
        for (User mentee : mentees) {
            Attendance att    = map.get(mentee.getId());
            String status     = (att != null) ? att.getStatus() : "hadir";
            String notes      = (att != null && att.getNotes() != null) ? att.getNotes() : "";
            // Penyisipan string "✏" dihapus agar tidak melebihi batas kolom array
            tableModel.addRow(new Object[]{no++, mentee.getName(), mentee.getId(), status, notes});
        }
    }

    private void refreshStats() {
        int total = mentees.size();
        totalMenteeLbl.setText(String.valueOf(total));

        // Count "belum dinilai" = rows with default/empty status loaded fresh
        int belum = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String notes = (String) tableModel.getValueAt(i, 4);
            if (notes == null || notes.isBlank()) belum++;
        }
        belumDinilaiLbl.setText(String.valueOf(belum));

        // Kehadiran % = hadir / total
        int hadir = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ("hadir".equalsIgnoreCase((String) tableModel.getValueAt(i, 3))) hadir++;
        }
        int pct = total == 0 ? 0 : (int) Math.round(hadir * 100.0 / total);
        kehadiranLbl.setText(pct + "%");
    }

    private void saveAttendance() {
        String selectedAgenda = (String) agendaComboBox.getSelectedItem();
        String dateStr = dateField.getText().trim();

        if (dateStr.isEmpty()) {
            ModernDialog.showError(this, "Masukkan tanggal!", "Error");
            return;
        }

        Date date;
        try {
            date = Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            ModernDialog.showError(this, "Format tanggal salah! Gunakan YYYY-MM-DD", "Error");
            return;
        }

        // Commit any active table cell edits
        if (attendanceTable.isEditing()) attendanceTable.getCellEditor().stopCellEditing();

        boolean success = true;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            long   menteeId = (long) tableModel.getValueAt(i, 2);
            String status   = (String) tableModel.getValueAt(i, 3);
            String notes    = (String) tableModel.getValueAt(i, 4);
            if (notes == null) notes = "";

            Attendance att = new Attendance(0, menteeId, selectedAgenda, date, status, notes);
            if (!attendanceDAO.saveOrUpdateAttendance(att)) success = false;
        }

        if (success) {
            ModernDialog.showInfo(this, "Kehadiran berhasil disimpan!", "Sukses");
            loadAttendanceData();
            refreshStats();
        } else {
            ModernDialog.showError(this, "Beberapa data gagal disimpan.", "Error");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildIconCircle(String emoji, int size) {
        JPanel circle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(size, size));
        circle.setMaximumSize(new Dimension(size, size));
        JLabel lbl = new JLabel(emoji);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size / 2));
        circle.add(lbl);
        return circle;
    }

    private JButton buildGreenButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? GREEN_DARK :
                           getModel().isRollover() ? new Color(28, 148, 78) : GREEN_PRIMARY;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════
    //  INNER CLASSES
    // ══════════════════════════════════════════════════════════════════

    /** Rounded white card with subtle border — matches MentorDashboard style. */
    private static class RoundedCard extends JPanel {
        RoundedCard() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(BORDER_CLR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Renders Status column as colored pill badge. */
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int col) {

            String status = value == null ? "" : value.toString().trim().toLowerCase();
            Color bg, fg;
            String display;
            String prefix;

            switch (status) {
                case "hadir":
                    bg = new Color(220, 252, 231);
                    fg = new Color(22, 163, 74);
                    display = "Hadir";
                    prefix  = "";
                    break;
                case "izin":
                    bg = YELLOW_BG;
                    fg = YELLOW_FG;
                    display = "Izin";
                    prefix  = "";
                    break;
                case "sakit":
                    bg = YELLOW_BG;
                    fg = YELLOW_FG;
                    display = "Sakit";
                    prefix  = "";
                    break;
                default:
                    bg = new Color(254, 226, 226);
                    fg = new Color(220, 38, 38);
                    display = "Tanpa Ket.";
                    prefix  = "";
                    break;
            }

            JLabel badge = new JLabel(prefix + display, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badge.setForeground(fg);
            badge.setOpaque(false);
            badge.setBorder(new EmptyBorder(6, 12, 6, 12));

            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            wrapper.setBackground(selected ? table.getSelectionBackground() : CARD_BG);
            wrapper.add(badge);
            return wrapper;
        }
     }
 
     /** Renders Nama column with a green initial avatar circle. */
     private static class AvatarNameRenderer extends DefaultTableCellRenderer {
         @Override
         public Component getTableCellRendererComponent(JTable table, Object value,
                 boolean selected, boolean focus, int row, int col) {
 
             String name = value == null ? "" : value.toString();
             String initial = name.isEmpty() ? "?" : String.valueOf(Character.toUpperCase(name.charAt(0)));
 
             JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
             panel.setBackground(selected ? table.getSelectionBackground() : CARD_BG);
 
             // Avatar circle
             JPanel avatar = new JPanel(new GridBagLayout()) {
                 protected void paintComponent(Graphics g) {
                     Graphics2D g2 = (Graphics2D) g.create();
                     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                     g2.setColor(GREEN_LIGHT);
                     g2.fillOval(0, 0, getWidth(), getHeight());
                     g2.dispose();
                     super.paintComponent(g);
                 }
             };
             avatar.setOpaque(false);
             avatar.setPreferredSize(new Dimension(32, 32));
             JLabel initLbl = new JLabel(initial);
             initLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
             initLbl.setForeground(GREEN_PRIMARY);
             avatar.add(initLbl);
 
             JLabel nameLbl = new JLabel(name);
             nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
             nameLbl.setForeground(selected ? table.getSelectionForeground() : TEXT_DARK);
 
             panel.add(avatar);
             panel.add(nameLbl);
             return panel;
         }
     }
}