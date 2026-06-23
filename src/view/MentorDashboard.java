package view;

import model.Mentor;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MentorDashboard extends BaseLayout {
    private Mentor mentor;

    // ── Brand colors (inherits GREEN_* from BaseLayout) ───────────────
    private static final Color BG_PAGE    = new Color(0xF4, 0xF6, 0xF8);
    private static final Color TEXT_DARK  = new Color(0x1A, 0x1A, 0x2E);
    private static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xE5, 0xE7, 0xEB);
    private static final Color RED_DOT    = new Color(0xEF, 0x44, 0x44);

    public MentorDashboard(Mentor mentor) {
        super("Siera - Dashboard", mentor.getName(), "Mentor");
        this.mentor = mentor;

        addMenuItem("Dashboard",  "🏠", createDashboardPanel());
        addMenuItem("Groups",     "👥", new GroupView(mentor.getGroupId(), mentor));
        addMenuItem("Tasks",      "📝", new TaskForm(mentor.getGroupId()));
        addMenuItem("Attendance", "📅", new AttendanceForm(mentor.getId()));
        addMenuItem("Profile",    "👤", new ProfileView(mentor, true, null));

        initializeLayout();
    }

    // ══════════════════════════════════════════════════════════════════
    //  DASHBOARD PANEL
    // ══════════════════════════════════════════════════════════════════
    private JPanel createDashboardPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PAGE);

        // Top bar
        root.add(createTopBar(), BorderLayout.NORTH);

        // Scrollable body
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Stat cards row
        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setOpaque(false);
        statsWrapper.setBorder(new EmptyBorder(0, 0, 4, 0));
        statsWrapper.add(createStatCardsRow(), BorderLayout.CENTER);
        body.add(statsWrapper, BorderLayout.NORTH);

        // Middle section
        JPanel middle = new JPanel(new BorderLayout(20, 0));
        middle.setOpaque(false);

        JPanel leftCol = new JPanel(new BorderLayout(0, 16));
        leftCol.setOpaque(false);
        leftCol.add(createFeaturedAnnouncement(), BorderLayout.NORTH);
        leftCol.add(createAnnouncementList(),     BorderLayout.CENTER);

        middle.add(leftCol,             BorderLayout.CENTER);
        middle.add(createRightColumn(), BorderLayout.EAST);
        body.add(middle, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        // Save button bar
        root.add(createBottomBar(), BorderLayout.SOUTH);
        return root;
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
        JLabel hello = new JLabel("Hello, " + mentor.getName() + " \uD83D\uDC4B");
        hello.setFont(new Font("Segoe UI", Font.BOLD, 22));
        hello.setForeground(TEXT_DARK);
        JLabel group = new JLabel("Kelompok 07");
        group.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        group.setForeground(TEXT_MUTED);
        left.add(hello, BorderLayout.NORTH);
        left.add(group, BorderLayout.SOUTH);

        // Bell + badge
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

        bar.add(left,    BorderLayout.WEST);
        bar.add(bellWrap, BorderLayout.EAST);
        return bar;
    }

    // ── 4 stat cards ─────────────────────────────────────────────────
    private JPanel createStatCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        row.add(createStatCard("Kehadiran",       "PATRIBERA D.A...",    "76%", "Persentase Kehadiran",  "\uD83D\uDC64", true));
        row.add(createStatCard("Belum Dinilai",   "Lihat detail",         "4",   "Tugas / Penilaian",     "\u23F1",       false));
        row.add(createStatCard("Total Mentee",    "Lihat detail",         "12",  "Total Mentee Aktif",    "\uD83D\uDC65", false));
        row.add(createStatCard("Progress Program","PATRIBERA UPNVJ 2026","80%", "Progres Keseluruhan",   "\uD83C\uDFAF", true));
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

        // Header: icon circle + titles + arrow
        JPanel header = new JPanel(new BorderLayout(0, 0));
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
        iconCircle.setPreferredSize(new Dimension(34, 34));
        iconCircle.setOpaque(false);
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

        JLabel arrow = new JLabel("\u2197");
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        arrow.setForeground(TEXT_MUTED);
        header.add(left,  BorderLayout.CENTER);
        header.add(arrow, BorderLayout.EAST);

        // Value
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLbl.setForeground(GREEN_PRIMARY);

        // Bottom: desc + optional progress
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
        JLabel starLbl = new JLabel("\u2B50");
        starLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        starCircle.add(starLbl);

        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.setOpaque(false);
        JLabel mainLbl = new JLabel("Do's and Don'ts Mentor PATRIBERA UPNVJ 2026");
        mainLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainLbl.setForeground(Color.WHITE);
        JLabel subLbl = new JLabel("+2 pengumuman lainnya");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(new Color(0xC8, 0xF0, 0xD8));
        text.add(mainLbl, BorderLayout.NORTH);
        text.add(subLbl,  BorderLayout.SOUTH);

        JLabel chevron = new JLabel("›");
        chevron.setFont(new Font("Segoe UI", Font.BOLD, 26));
        chevron.setForeground(Color.WHITE);

        card.add(starCircle, BorderLayout.WEST);
        card.add(text,       BorderLayout.CENTER);
        card.add(chevron,    BorderLayout.EAST);
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
        JLabel titleLbl = new JLabel("Pengumuman (8)");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_DARK);
        cardHeader.add(titleLbl, BorderLayout.WEST);

        // Items
        String[][] items = {
            {"Semangat untuk Hari Kedua,...",
             "Terima kasih atas dedikasi kalian memantau kelompok hingga sore ini. Tetap semangat dan jangan lupa istirahat yang cukup!",
             "Hari ini, 04.20"},
            {"Cek Kondisi Kesehatan Kelompok",
             "Mengingat cuaca yang cukup terik, pastikan mentee kalian cukup minum dan jaga kondisi kesehatan.",
             "Kemarin, 10.30"},
            {"Pantau Kelengkapan Atribut Mentee",
             "Bapak ada pengecekan atribut untuk kegiatan esok hari. Pastikan semua mentee sudah lengkap.",
             "15 Apr, 20.00"},
            {"Upload Foto Dokumentasi Kelompok",
             "Jangan lupa mengupload dokumentasi kegiatan hari ini di link yang sudah diberikan.",
             "12 Apr, 17.30"},
        };

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        for (int i = 0; i < items.length; i++) {
            listPanel.add(createAnnouncementRow(items[i][0], items[i][1], items[i][2]));
            if (i < items.length - 1) {
                JPanel sep = new JPanel();
                sep.setBackground(BORDER_CLR);
                sep.setPreferredSize(new Dimension(0, 1));
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                listPanel.add(sep);
            }
        }

        // Footer link
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setOpaque(false);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        JLabel seeAll = new JLabel("Lihat semua pengumuman");
        seeAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seeAll.setForeground(GREEN_PRIMARY);
        seeAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        footer.add(seeAll);

        wrapper.add(cardHeader, BorderLayout.NORTH);
        wrapper.add(listPanel,  BorderLayout.CENTER);
        wrapper.add(footer,     BorderLayout.SOUTH);
        return wrapper;
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
        JLabel iconLbl = new JLabel("\uD83D\uDCE2");
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

    // ── Right column ─────────────────────────────────────────────────
    private JPanel createRightColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setPreferredSize(new Dimension(290, 0));

        col.add(createImportantNotice());
        col.add(Box.createRigidArea(new Dimension(0, 16)));
        col.add(createAgendaPanel());
        return col;
    }

    private JPanel createImportantNotice() {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel titleLbl = new JLabel("Pengumuman Penting");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_DARK);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel bodyLbl = new JLabel("<html>Informasi penting dan panduan<br>wajib untuk seluruh mentor.</html>");
        bodyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bodyLbl.setForeground(TEXT_MUTED);

        JButton btn = makeGreenButton("Lihat Pengumuman");

        content.add(bodyLbl, BorderLayout.NORTH);
        content.add(btn,     BorderLayout.SOUTH);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(content,  BorderLayout.CENTER);
        return card;
    }

    private JPanel createAgendaPanel() {
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

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 18, 8, 18));
        JLabel titleLbl = new JLabel("Agenda Mendatang");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_DARK);
        header.add(titleLbl, BorderLayout.WEST);

        String[][] agendas = {
            {"Briefing Mentor Harian",  "16 Mei 2026 \u2022 08.00 \u2013 09.00 WIB"},
            {"Monitoring Kelompok",      "16 Mei 2026 \u2022 13.00 \u2013 15.00 WIB"},
            {"Evaluasi Hari ke-2",       "17 Mei 2026 \u2022 10.00 \u2013 11.30 WIB"},
        };

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        for (String[] a : agendas) {
            list.add(createAgendaRow(a[0], a[1]));
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setOpaque(false);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        JLabel seeAll = new JLabel("Lihat semua agenda");
        seeAll.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seeAll.setForeground(GREEN_PRIMARY);
        seeAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        footer.add(seeAll);

        card.add(header, BorderLayout.NORTH);
        card.add(list,   BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAgendaRow(String title, String time) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 18, 10, 18));

        JPanel iconBox = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setPreferredSize(new Dimension(32, 32));
        iconBox.setOpaque(false);
        JLabel calLbl = new JLabel("\uD83D\uDCC5");
        calLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        iconBox.add(calLbl);

        JPanel textPanel = new JPanel(new BorderLayout(0, 3));
        textPanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLbl.setForeground(TEXT_MUTED);
        textPanel.add(titleLbl, BorderLayout.NORTH);
        textPanel.add(timeLbl,  BorderLayout.SOUTH);

        row.add(iconBox,    BorderLayout.WEST);
        row.add(textPanel,  BorderLayout.CENTER);
        return row;
    }

    // ── Bottom Save bar ───────────────────────────────────────────────
    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bar.setBackground(CARD_BG);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        JButton save = makeGreenButton("\uD83D\uDCBE  Save");
        save.setPreferredSize(new Dimension(110, 38));
        bar.add(save);
        return bar;
    }

    // ── Green rounded button helper ───────────────────────────────────
    private JButton makeGreenButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
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
        btn.setPreferredSize(new Dimension(160, 36));
        return btn;
    }
}