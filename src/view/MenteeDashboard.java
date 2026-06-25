package view;

import dao.AttendanceDAO;
import dao.TaskDAO;
import model.Mentee;
import model.Attendance;
import model.Task;
import model.TaskSubmission;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenteeDashboard extends BaseLayout {
    private Mentee mentee;
    private JLabel featuredTitleLabel;
    private JLabel featuredSubLabel;
    private JPanel announcementListContainer;
    private JLabel announcementTitleLabel;
    private static final Color RED_DOT = new Color(0xEF, 0x44, 0x44);

    private static final Color BG_PAGE    = new Color(0xF4, 0xF6, 0xF8);
    private static final Color TEXT_DARK  = new Color(0x1A, 0x1A, 0x2E);
    private static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xE5, 0xE7, 0xEB);

    public MenteeDashboard(Mentee mentee) {
        super("Siera - Dashboard", mentee.getName(), "Mentee", mentee.getGroupId());
        this.mentee = mentee;

        addMenuItem("Dashboard",  "🏠", createDashboardPanel());
        addMenuItem("Groups",     "👥", new GroupView(mentee.getGroupId(), mentee));
        addMenuItem("Tasks",      "📝", new TaskView(mentee.getId(), mentee.getGroupId()));
        addMenuItem("Attendance", "📅", new AttendanceView(mentee.getId()));
        addMenuItem("Profile",    "👤", new ProfileView(mentee, true, null));

        initializeLayout();
    }

    private JPanel createDashboardPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PAGE);

        root.add(createTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(24, 28, 24, 28));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        // Populate initially
        populateDashboard(body);

        scroll.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = scroll.getWidth();
                int side = Math.max(28, (w - 920) / 2);
                body.setBorder(new EmptyBorder(24, side, 24, side));
                body.revalidate();
            }
        });

        // Refresh panel content when visible
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                populateDashboard(body);
            }
        });

        return root;
    }

    private void populateDashboard(JPanel body) {
        body.removeAll();

        // Stat cards
        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setOpaque(false);
        statsWrapper.setBorder(new EmptyBorder(0, 0, 4, 0));
        statsWrapper.add(createStatCardsRow(), BorderLayout.CENTER);
        body.add(statsWrapper, BorderLayout.NORTH);

        // Middle: tugas terbaru + info kehadiran + pengumuman
        JPanel middle = new JPanel(new BorderLayout(20, 0));
        middle.setOpaque(false);

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        leftCol.add(createFeaturedAnnouncement());
        leftCol.add(Box.createRigidArea(new Dimension(0, 16)));
        leftCol.add(createAnnouncementList());
        leftCol.add(Box.createRigidArea(new Dimension(0, 16)));
        leftCol.add(createRecentTasksCard());

        middle.add(leftCol, BorderLayout.CENTER);

        JPanel attendanceColumn = new JPanel(new BorderLayout());
        attendanceColumn.setOpaque(false);
        attendanceColumn.setPreferredSize(new Dimension(270, 0));
        attendanceColumn.add(createAttendanceSummaryCard(), BorderLayout.NORTH);
        middle.add(attendanceColumn, BorderLayout.EAST);
        body.add(middle, BorderLayout.CENTER);

        body.revalidate();
        body.repaint();
    }

    // ── Top bar ───────────────────────────────────────────────────────
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(16, 28, 16, 28)
        ));

        JPanel left = new JPanel(new BorderLayout(0, 3));
        left.setOpaque(false);
        JLabel hello = new JLabel("Hello, " + mentee.getName() + " ");
        hello.setFont(new Font("Segoe UI", Font.BOLD, 22));
        hello.setForeground(TEXT_DARK);
        hello.setIcon(new SvgIcon(SvgIcon.Type.GREETING, 20, new Color(34, 166, 90)));
        hello.setHorizontalTextPosition(SwingConstants.LEFT);
        hello.setIconTextGap(6);
        JLabel sub = new JLabel("Siap untuk PATRIBERA 2026?");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        left.add(hello, BorderLayout.NORTH);
        left.add(sub,   BorderLayout.SOUTH);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    // ── 4 Stat cards ─────────────────────────────────────────────────
    private JPanel createStatCardsRow() {
        // Ambil data dari DAO
        int totalTasks     = 0;
        int submittedTasks = 0;
        int pendingTasks   = 0;
        int attendanceRate = 0;

        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> semuaTugas = taskDAO.getTasksByGroupId(mentee.getGroupId());
            totalTasks = semuaTugas.size();

            // hitung submitted: cek tiap tugas, apakah mentee ini udah submit
            for (Task task : semuaTugas) {
                TaskSubmission sub = taskDAO.getSubmissionByTaskAndUser(task.getId(), mentee.getId());
                if (sub != null && isTaskCompleted(sub.getStatus())) {
                    submittedTasks++;
                }
            }
            pendingTasks = totalTasks - submittedTasks;

            // attendance rate: hadir / total * 100
            AttendanceDAO attDAO = new AttendanceDAO();
            List<Attendance> attList = attDAO.getAttendanceByUserId(mentee.getId());
            if (!attList.isEmpty()) {
                long hadirCount = attList.stream()
                    .filter(a -> a.getStatus().equalsIgnoreCase("Hadir"))
                    .count();
                attendanceRate = (int) (hadirCount * 100 / attList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
}

        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        row.add(createStatCard("Total Tugas",    "Semua tugas kelompok", String.valueOf(totalTasks),          "Tugas diberikan mentor",  "📝", false));
        row.add(createStatCard("Dikumpulkan",    "Tugas selesai",        String.valueOf(submittedTasks),      "Tugas sudah dikumpulkan", "✅", false));
        row.add(createStatCard("Belum Kumpul",   "Perlu diselesaikan",   String.valueOf(pendingTasks),        "Tugas pending",           "⏳", false));
        row.add(createStatCard("Kehadiran",      "Riwayat kehadiranmu",  attendanceRate + "%",                "Persentase kehadiran",    "📅", true));

        return row;
    }

    private JPanel createStatCard(String title, String subtitle, String value,
                                   String desc, String icon, boolean showBar) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header: icon + titles + arrow
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(34, 34));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconCircle.add(iconLbl);

        JPanel titles = new JPanel(new BorderLayout(0, 1));
        titles.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        JLabel subtitleLbl = new JLabel(subtitle);
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLbl.setForeground(TEXT_MUTED);
        titles.add(titleLbl,    BorderLayout.NORTH);
        titles.add(subtitleLbl, BorderLayout.SOUTH);

        left.add(iconCircle);
        left.add(titles);

        JLabel arrow = new JLabel("↗");
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        arrow.setForeground(TEXT_MUTED);
        header.add(left,  BorderLayout.CENTER);
        header.add(arrow, BorderLayout.EAST);

        // Value
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLbl.setForeground(GREEN_PRIMARY);

        // Bottom
        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.setOpaque(false);
        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_MUTED);
        bottom.add(descLbl, BorderLayout.NORTH);

        if (showBar && value.endsWith("%")) {
            int pct = Integer.parseInt(value.replace("%", ""));
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(pct);
            bar.setStringPainted(false);
            bar.setPreferredSize(new Dimension(0, 7));
            bar.setBackground(new Color(0xE5, 0xE7, 0xEB));
            bar.setForeground(GREEN_PRIMARY);
            bar.setBorderPainted(false);
            bottom.add(bar, BorderLayout.SOUTH);
        }

        card.add(header,   BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        card.add(bottom,   BorderLayout.SOUTH);
        return card;
    }

    // ── Recent Tasks card ─────────────────────────────────────────────
    private JPanel createRecentTasksCard() {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);

        // Header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.setBorder(new EmptyBorder(16, 18, 10, 18));
        JLabel titleLbl = new JLabel("Tugas Terbaru");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_DARK);
        cardHeader.add(titleLbl, BorderLayout.WEST);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks = taskDAO.getTasksByGroupId(mentee.getGroupId());

            if (tasks.isEmpty()) {
                listPanel.add(createEmptyRow("Belum ada tugas untuk kelompokmu."));
            } else {
                int limit = Math.min(3, tasks.size());
                for (int i = 0; i < limit; i++) {
                    Task task = tasks.get(i);
                    TaskSubmission submission = taskDAO.getSubmissionByTaskAndUser(task.getId(), mentee.getId());
                    String status = submission == null ? "Pending" : submission.getStatus();

                    listPanel.add(createTaskRow(
                        task.getTitle(),
                        "Deadline: " + formatDate(task.getDeadline().toString()),
                        status
                    ));

                    if (i < limit - 1) {
                        JPanel sep = new JPanel();
                        sep.setBackground(BORDER_CLR);
                        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                        listPanel.add(sep);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listPanel.add(createEmptyRow("Data tugas belum bisa dimuat."));
        }

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setOpaque(false);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        JLabel seeAll = new JLabel("Lihat semua tugas");
        seeAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seeAll.setForeground(GREEN_PRIMARY);
        seeAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        seeAll.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { switchPage("Tasks"); }
        });
        footer.add(seeAll);

        wrapper.add(cardHeader, BorderLayout.NORTH);
        wrapper.add(listPanel,  BorderLayout.CENTER);
        wrapper.add(footer,     BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createTaskRow(String title, String deadline, String status) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(TEXT_DARK);
        JLabel deadlineLbl = new JLabel(deadline);
        deadlineLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deadlineLbl.setForeground(TEXT_MUTED);
        textPanel.add(titleLbl,    BorderLayout.NORTH);
        textPanel.add(deadlineLbl, BorderLayout.SOUTH);

        // Badge status
        boolean selesai = isTaskCompleted(status);
        JLabel badge = new JLabel(getTaskStatusText(status)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(selesai ? new Color(220, 252, 231) : new Color(254, 243, 199));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(selesai ? new Color(22, 163, 74) : new Color(180, 120, 0));
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));

        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);

        row.add(textPanel,  BorderLayout.CENTER);
        row.add(badgeWrap,  BorderLayout.EAST);
        return row;
    }

    // ── Attendance summary card ───────────────────────────────────────
    private JPanel createAttendanceSummaryCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(270, 232));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 10, 18));
        JLabel titleLbl = new JLabel("Ringkasan Kehadiran");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_DARK);
        header.add(titleLbl, BorderLayout.WEST);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        Map<String, Integer> counts = getAttendanceCounts();
        String[][] rows = {
            {"Hadir",       counts.get("Hadir") + " sesi",       "✅"},
            {"Tidak Hadir", counts.get("Tidak Hadir") + " sesi", "❌"},
            {"Izin/Sakit",  counts.get("Izin/Sakit") + " sesi",  "📋"},
        };

        for (String[] r : rows) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(8, 18, 8, 18));

            JLabel emojiLbl = new JLabel(r[2]);
            emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            JLabel nameLbl = new JLabel(r[0]);
            nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            nameLbl.setForeground(TEXT_DARK);

            JLabel valLbl = new JLabel(r[1]);
            valLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            valLbl.setForeground(GREEN_PRIMARY);

            row.add(emojiLbl, BorderLayout.WEST);
            row.add(nameLbl,  BorderLayout.CENTER);
            row.add(valLbl,   BorderLayout.EAST);
            list.add(row);
        }

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setOpaque(false);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        JLabel seeAll = new JLabel("Lihat riwayat kehadiran");
        seeAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seeAll.setForeground(GREEN_PRIMARY);
        seeAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        seeAll.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { switchPage("Attendance"); }
        });
        footer.add(seeAll);

        card.add(header, BorderLayout.NORTH);
        card.add(list,   BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createEmptyRow(String message) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        label.setForeground(TEXT_MUTED);
        row.add(label, BorderLayout.CENTER);
        return row;
    }

    private boolean isTaskCompleted(String status) {
        return "Submitted".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status);
    }

    private String getTaskStatusText(String status) {
        if ("Accepted".equalsIgnoreCase(status)) {
            return "Diterima";
        }
        if ("Submitted".equalsIgnoreCase(status)) {
            return "Dikumpulkan";
        }
        return "Belum";
    }

    private Map<String, Integer> getAttendanceCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Hadir", 0);
        counts.put("Tidak Hadir", 0);
        counts.put("Izin/Sakit", 0);

        try {
            AttendanceDAO attDAO = new AttendanceDAO();
            List<Attendance> attendanceList = attDAO.getAttendanceByUserId(mentee.getId());

            for (Attendance att : attendanceList) {
                String status = att.getStatus() == null ? "" : att.getStatus().trim().toLowerCase();
                if ("hadir".equals(status)) {
                    counts.put("Hadir", counts.get("Hadir") + 1);
                } else if ("izin".equals(status) || "sakit".equals(status)) {
                    counts.put("Izin/Sakit", counts.get("Izin/Sakit") + 1);
                } else {
                    counts.put("Tidak Hadir", counts.get("Tidak Hadir") + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return counts;
    }

    private String formatDate(String dateStr) {
        try {
            String dPart = dateStr;
            if (dateStr.contains(" ")) {
                dPart = dateStr.split(" ")[0];
            }
            String[] parts = dPart.split("-");
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

    // ── Featured announcement (green banner) ─────────────────────────
    private JPanel createFeaturedAnnouncement() {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel starCircle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_DARK);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        starCircle.setOpaque(false);
        starCircle.setPreferredSize(new Dimension(42, 42));
        JLabel starLbl = new JLabel("⭐");
        starLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        starCircle.add(starLbl);

        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.setOpaque(false);
        featuredTitleLabel = new JLabel("Memuat...");
        featuredTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        featuredTitleLabel.setForeground(Color.WHITE);
        featuredSubLabel = new JLabel("");
        featuredSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        featuredSubLabel.setForeground(new Color(0xC8, 0xF0, 0xD8));
        text.add(featuredTitleLabel, BorderLayout.NORTH);
        text.add(featuredSubLabel,  BorderLayout.SOUTH);

        JLabel chevron = new JLabel("›");
        chevron.setFont(new Font("Segoe UI", Font.BOLD, 26));
        chevron.setForeground(Color.WHITE);

        card.add(starCircle, BorderLayout.WEST);
        card.add(text,       BorderLayout.CENTER);
        card.add(chevron,    BorderLayout.EAST);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAllAnnouncementsDialog();
            }
        });

        refreshFeaturedAnnouncement();
        return card;
    }

    // ── Announcement list card ────────────────────────────────────────
    private JPanel createAnnouncementList() {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);

        // Card header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.setBorder(new EmptyBorder(16, 18, 10, 18));
        
        announcementTitleLabel = new JLabel("Pengumuman (0)");
        announcementTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        announcementTitleLabel.setForeground(TEXT_DARK);
        cardHeader.add(announcementTitleLabel, BorderLayout.WEST);

        announcementListContainer = new JPanel();
        announcementListContainer.setLayout(new BoxLayout(announcementListContainer, BoxLayout.Y_AXIS));
        announcementListContainer.setOpaque(false);

        // Footer link
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setOpaque(false);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        JLabel seeAll = new JLabel("Lihat semua pengumuman");
        seeAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seeAll.setForeground(GREEN_PRIMARY);
        seeAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        seeAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAllAnnouncementsDialog();
            }
        });
        footer.add(seeAll);

        wrapper.add(cardHeader, BorderLayout.NORTH);
        wrapper.add(announcementListContainer,  BorderLayout.CENTER);
        wrapper.add(footer,     BorderLayout.SOUTH);

        loadAnnouncements();
        return wrapper;
    }

    private void loadAnnouncements() {
        if (announcementListContainer == null) return;
        announcementListContainer.removeAll();
        List<model.Announcement> announcements = new dao.AnnouncementDAO().getAnnouncementsByGroupId(mentee.getGroupId());
        
        announcementTitleLabel.setText("Pengumuman (" + announcements.size() + ")");
        
        if (announcements.isEmpty()) {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyPanel.setOpaque(false);
            JLabel emptyLbl = new JLabel("Belum ada pengumuman.");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            emptyLbl.setForeground(TEXT_MUTED);
            emptyPanel.add(emptyLbl);
            announcementListContainer.add(emptyPanel);
        } else {
            // Show up to 4 announcements
            int limit = Math.min(4, announcements.size());
            for (int i = 0; i < limit; i++) {
                model.Announcement ann = announcements.get(i);
                announcementListContainer.add(createAnnouncementRow(ann.getTitle(), ann.getContent(), formatTimestamp(ann.getCreatedAt())));
                if (i < limit - 1) {
                    JPanel sep = new JPanel();
                    sep.setBackground(BORDER_CLR);
                    sep.setPreferredSize(new Dimension(0, 1));
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    announcementListContainer.add(sep);
                }
            }
        }
        announcementListContainer.revalidate();
        announcementListContainer.repaint();
    }

    private void refreshFeaturedAnnouncement() {
        if (featuredTitleLabel == null) return;
        List<model.Announcement> announcements = new dao.AnnouncementDAO().getAnnouncementsByGroupId(mentee.getGroupId());
        if (announcements.isEmpty()) {
            featuredTitleLabel.setText("Belum ada pengumuman");
            featuredSubLabel.setText("");
        } else {
            model.Announcement latest = announcements.get(0);
            featuredTitleLabel.setText(latest.getTitle());
            if (announcements.size() > 1) {
                featuredSubLabel.setText("+" + (announcements.size() - 1) + " pengumuman lainnya");
            } else {
                featuredSubLabel.setText("Pengumuman terbaru");
            }
        }
    }

    private String formatTimestamp(java.sql.Timestamp ts) {
        if (ts == null) return "";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM, HH.mm");
        return sdf.format(ts);
    }

    private JPanel createAnnouncementRow(String title, String body, String time) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 18, 12, 18));

        // Icon with red badge
        JPanel iconWrap = new JPanel(null);
        iconWrap.setPreferredSize(new Dimension(38, 38));
        iconWrap.setOpaque(false);

        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setBounds(0, 4, 32, 32);
        JLabel iconLbl = new JLabel("📢");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        iconCircle.add(iconLbl);

        JPanel redDot = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED_DOT);
                g2.fillOval(0, 0, 10, 10);
                g2.dispose();
            }
        };
        redDot.setBounds(22, 0, 10, 10);
        redDot.setOpaque(false);
        iconWrap.add(iconCircle);
        iconWrap.add(redDot);

        // Text
        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(TEXT_DARK);
        JLabel bodyLbl = new JLabel("<html><body style='width:280px'>" + body + "</body></html>");
        bodyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bodyLbl.setForeground(TEXT_MUTED);
        textPanel.add(titleLbl, BorderLayout.NORTH);
        textPanel.add(bodyLbl,  BorderLayout.SOUTH);

        // Right: time + chevron
        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setOpaque(false);
        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLbl.setForeground(TEXT_MUTED);
        JLabel chev = new JLabel("›");
        chev.setFont(new Font("Segoe UI", Font.BOLD, 20));
        chev.setForeground(TEXT_MUTED);
        rightPanel.add(timeLbl, BorderLayout.NORTH);
        rightPanel.add(chev,    BorderLayout.EAST);

        row.add(iconWrap,   BorderLayout.WEST);
        row.add(textPanel,  BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);
        return row;
    }

    private void showAllAnnouncementsDialog() {
        JDialog dialog = new JDialog((Window) SwingUtilities.getWindowAncestor(this), "Semua Pengumuman", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_PAGE);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(16, 24, 16, 24)
        ));
        JLabel title = new JLabel("📢  Semua Pengumuman");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        header.add(title, BorderLayout.WEST);
        dialog.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        List<model.Announcement> announcements = new dao.AnnouncementDAO().getAnnouncementsByGroupId(mentee.getGroupId());

        if (announcements.isEmpty()) {
            JLabel empty = new JLabel("Tidak ada pengumuman.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(TEXT_MUTED);
            listPanel.add(empty);
        } else {
            for (int i = 0; i < announcements.size(); i++) {
                model.Announcement ann = announcements.get(i);
                JPanel row = createAnnouncementRow(ann.getTitle(), ann.getContent(), formatTimestamp(ann.getCreatedAt()));
                listPanel.add(row);
                if (i < announcements.size() - 1) {
                    JPanel sep = new JPanel();
                    sep.setBackground(BORDER_CLR);
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    sep.setPreferredSize(new Dimension(0, 1));
                    listPanel.add(Box.createVerticalStrut(10));
                    listPanel.add(sep);
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(new Color(249, 250, 251));
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        
        JButton closeBtn = new JButton("Tutup") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setPreferredSize(new Dimension(80, 32));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        
        footer.add(closeBtn);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
