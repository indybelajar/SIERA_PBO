package view;

import dao.UserProfileDAO;
import model.User;
import model.UserProfile;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ProfileView extends JPanel {

    // ── Brand colors ──────────────────────────────────────────────────
    private static final Color BG_PAGE       = new Color(0xF4, 0xF6, 0xF8);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color BORDER_CLR    = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT_DARK     = new Color(0x1A, 0x1A, 0x2E);
    private static final Color TEXT_MUTED    = new Color(0x6B, 0x72, 0x80);
    private static final Color GREEN_PRIMARY = new Color(34,  166, 90);
    private static final Color GREEN_DARK    = new Color(23,  122, 66);
    private static final Color GREEN_LIGHT   = new Color(232, 245, 238);
    private static final Color RED_DOT       = new Color(0xEF, 0x44, 0x44);

    // ── State ─────────────────────────────────────────────────────────
    private User user;
    private UserProfileDAO profileDAO;
    private UserProfile userProfile;
    private boolean canEdit;
    private String backPage;
    private boolean isEditMode = false;

    // ── UI fields (editable) ─────────────────────────────────────────
    private JTextField jurusanField;
    private JTextField fakultasField;
    private JTextField bioField;
    private JTextField instagramField;
    private JTextField tiktokField;
    private JTextField xField;
    private JTextField linkedinField;
    private JButton editBtn;
    private JPanel bottomBarPanel;
    private JPanel bodyPanel;

    // ── Constructor ───────────────────────────────────────────────────
    public ProfileView(User user, boolean canEdit, String backPage) {
        this.user       = user;
        this.canEdit    = canEdit;
        this.backPage   = backPage;
        this.profileDAO = new UserProfileDAO();

        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        add(createTopBar(),  BorderLayout.NORTH);
        bodyPanel = createBody();
        add(bodyPanel,    BorderLayout.CENTER);
        bottomBarPanel = createBottomBar();
        add(bottomBarPanel, BorderLayout.SOUTH);

        loadProfileData();
        setEditMode(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int side = Math.max(28, (w - 920) / 2);
                bodyPanel.setBorder(new EmptyBorder(24, side, 0, side));
                bottomBarPanel.setBorder(new EmptyBorder(12, side, 12, side));
                revalidate();
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  TOP BAR
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

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Profil Saya");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        JLabel icon = new JLabel("\uD83D\uDC64"); // 👤
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        titleRow.add(title);
        titleRow.add(icon);

        JLabel subtitle = new JLabel("Kelola informasi profil dan akun Anda.");
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
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(24, 28, 0, 28));

        // Wrap everything in a scroll pane
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        inner.add(createHeroCard());
        inner.add(Box.createVerticalStrut(16));

        // Two-column row: Informasi Dasar (left) | Media Sosial (right)
        JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
        twoCol.setOpaque(false);
        twoCol.add(createInfoDasarCard());
        twoCol.add(createMediaSosialCard());
        twoCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, twoCol.getPreferredSize().height));
        inner.add(twoCol);
        inner.add(Box.createVerticalStrut(16));

        inner.add(createDetailAkunCard());
        inner.add(Box.createVerticalStrut(8));
        inner.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        body.add(scroll, BorderLayout.CENTER);
        return body;
    }

    // ══════════════════════════════════════════════════════════════════
    //  HERO CARD  (avatar + name + role badge + jurusan + fakultas)
    // ══════════════════════════════════════════════════════════════════
    private JPanel createHeroCard() {
        RoundedCard card = new RoundedCard();
        card.setLayout(new BorderLayout(20, 0));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // ---- Avatar area ----
        JPanel avatarArea = new JPanel(null);
        avatarArea.setOpaque(false);
        avatarArea.setPreferredSize(new Dimension(100, 100));

        // Big circle avatar
        JPanel avatarCircle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(200, 230, 210));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarCircle.setOpaque(false);
        avatarCircle.setBounds(5, 5, 90, 90);
        String initial = user.getName().isEmpty() ? "?" :
                         String.valueOf(Character.toUpperCase(user.getName().charAt(0)));
        JLabel initLbl = new JLabel(initial);
        initLbl.setFont(new Font("Segoe UI", Font.BOLD, 34));
        initLbl.setForeground(GREEN_PRIMARY);
        avatarCircle.add(initLbl);

        avatarArea.add(avatarCircle);

        // ---- Info panel ----
        JPanel info = new JPanel(new BorderLayout(0, 8));
        info.setOpaque(false);

        // Name
        JLabel nameLbl = new JLabel(user.getName());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLbl.setForeground(TEXT_DARK);

        // Role badge
        JLabel roleBadge = new JLabel(user.getRole().toUpperCase()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleBadge.setForeground(GREEN_PRIMARY);
        roleBadge.setOpaque(false);
        roleBadge.setBorder(new EmptyBorder(3, 10, 3, 10));

        // Wrap badge with FlowLayout so it doesn't stretch
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(roleBadge);

        // Jurusan + Fakultas sub-info
        JPanel subInfo = new JPanel();
        subInfo.setLayout(new BoxLayout(subInfo, BoxLayout.Y_AXIS));
        subInfo.setOpaque(false);
        subInfo.add(buildIconTextRow("\uD83C\uDF93", "S1 Sistem Informasi")); // 🎓 placeholder — overwritten in loadProfileData
        subInfo.add(Box.createVerticalStrut(4));
        subInfo.add(buildIconTextRow("\uD83C\uDFDB", "Fakultas Ilmu Komputer")); // 🏛

        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setOpaque(false);
        top.add(nameLbl,  BorderLayout.NORTH);
        top.add(badgeRow, BorderLayout.CENTER);
        top.add(subInfo,  BorderLayout.SOUTH);

        info.add(top, BorderLayout.CENTER);

        // ---- Edit Profil button (top-right) ----
        editBtn = buildOutlineButton("  Edit Profil");
        editBtn.setIcon(new SvgIcon(SvgIcon.Type.USER, 14, GREEN_PRIMARY));
        editBtn.setIconTextGap(8);
        editBtn.addActionListener(e -> setEditMode(true));

        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        btnPanel.add(editBtn, gbc);

        card.add(avatarArea, BorderLayout.WEST);
        card.add(info,       BorderLayout.CENTER);
        card.add(btnPanel,   BorderLayout.EAST);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  INFORMASI DASAR CARD
    // ══════════════════════════════════════════════════════════════════
    private JPanel createInfoDasarCard() {
        RoundedCard card = new RoundedCard();
        card.setLayout(new BorderLayout(0, 0));

        card.add(createCardHeader("\uD83D\uDC64", "Informasi Dasar"), BorderLayout.NORTH); // 👤

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);
        rows.setBorder(new EmptyBorder(0, 20, 16, 20));

        jurusanField  = new JTextField();
        fakultasField = new JTextField();
        bioField      = new JTextField();

        rows.add(buildFieldRow("\uD83C\uDF93", "Jurusan",  jurusanField));   // 🎓
        rows.add(buildDivider());
        rows.add(buildFieldRow("\uD83C\uDFDB", "Fakultas", fakultasField));  // 🏛
        rows.add(buildDivider());
        rows.add(buildFieldRow("\uD83D\uDCAC", "Bio",      bioField));       // 💬

        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  MEDIA SOSIAL CARD
    // ══════════════════════════════════════════════════════════════════
    private JPanel createMediaSosialCard() {
        RoundedCard card = new RoundedCard();
        card.setLayout(new BorderLayout(0, 0));

        card.add(createCardHeader("\uD83D\uDD17", "Media Sosial"), BorderLayout.NORTH); // 🔗

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);
        rows.setBorder(new EmptyBorder(0, 20, 16, 20));

        instagramField = new JTextField();
        tiktokField    = new JTextField();
        xField         = new JTextField();
        linkedinField  = new JTextField();

        rows.add(buildSocialRow(new SvgIcon(SvgIcon.Type.INSTAGRAM, 14, GREEN_PRIMARY), "Instagram",  instagramField));
        rows.add(buildDivider());
        rows.add(buildSocialRow(new SvgIcon(SvgIcon.Type.TIKTOK, 14, GREEN_PRIMARY), "TikTok",     tiktokField));
        rows.add(buildDivider());
        rows.add(buildSocialRow(new SvgIcon(SvgIcon.Type.X, 14, GREEN_PRIMARY), "X (Twitter)", xField));
        rows.add(buildDivider());
        rows.add(buildSocialRow(new SvgIcon(SvgIcon.Type.LINKEDIN, 14, GREEN_PRIMARY), "LinkedIn",   linkedinField));

        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  DETAIL AKUN CARD  (email | peran | akun dibuat)
    // ══════════════════════════════════════════════════════════════════
    private JPanel createDetailAkunCard() {
        RoundedCard card = new RoundedCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        card.add(createCardHeader("\uD83D\uDEE1", "Detail Akun"), BorderLayout.NORTH); // 🛡

        JPanel row = new JPanel(new GridLayout(1, 3, 0, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 20, 16, 20));

        // E-mail
        row.add(buildAccountDetail("\u2709", "E-mail",
            user.getEmail() != null ? user.getEmail() : "-")); // ✉

        // Peran
        row.add(buildAccountDetail("\uD83D\uDC64", "Peran",
            capitalize(user.getRole()))); // 👤

        // Akun Dibuat (today as placeholder since User model has no createdAt)
        String tgl = LocalDate.now().format(
            DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id")));
        row.add(buildAccountDetail("\uD83D\uDCC5", "Akun Dibuat", tgl)); // 📅

        card.add(row, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  BOTTOM BAR  (Save Changes)
    // ══════════════════════════════════════════════════════════════════
    private JPanel createBottomBar() {
        bottomBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        bottomBarPanel.setOpaque(false);

        JButton cancelBtn = buildOutlineButton("Batal");
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.addActionListener(e -> {
            setEditMode(false);
            loadProfileData(); // revert
        });

        JButton saveBtn = buildGreenButton("Save Changes");
        saveBtn.setIcon(new SvgIcon(SvgIcon.Type.SAVE, 16, Color.WHITE));
        saveBtn.setIconTextGap(8);
        saveBtn.setPreferredSize(new Dimension(160, 40));
        saveBtn.addActionListener(e -> saveProfile());

        bottomBarPanel.add(cancelBtn);
        bottomBarPanel.add(saveBtn);
        return bottomBarPanel;
    }

    // ══════════════════════════════════════════════════════════════════
    //  DATA METHODS
    // ══════════════════════════════════════════════════════════════════
    private void loadProfileData() {
        userProfile = profileDAO.getProfileByUserId(user.getId());
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUserId(user.getId());
        }
        setText(jurusanField,  userProfile.getJurusan());
        setText(fakultasField, userProfile.getFakultas());
        setText(bioField,      userProfile.getBio());
        setText(instagramField, userProfile.getInstagramHandle());
        setText(tiktokField,    userProfile.getTiktokHandle());
        setText(xField,         userProfile.getXHandle());
        setText(linkedinField,  userProfile.getLinkedinUrl());
    }

    private void setEditMode(boolean edit) {
        isEditMode = edit;
        JTextField[] fields = {jurusanField, fakultasField, bioField,
                               instagramField, tiktokField, xField, linkedinField};
        for (JTextField f : fields) {
            if (f == null) continue;
            f.setEditable(edit);
            f.setBackground(edit ? Color.WHITE : new Color(0xF9, 0xFA, 0xFB));
            f.setBorder(edit
                ? new CompoundBorder(new LineBorder(GREEN_PRIMARY, 1, true), new EmptyBorder(4, 8, 4, 8))
                : BorderFactory.createEmptyBorder(4, 0, 4, 0));
        }
        if (editBtn != null) {
            editBtn.setVisible(!edit);
        }
        if (bottomBarPanel != null) {
            bottomBarPanel.setVisible(edit);
            revalidate();
            repaint();
        }
    }

    private void saveProfile() {
        if (!isEditMode) {
            ModernDialog.showInfo(this,
                "Klik 'Edit Profil' terlebih dahulu untuk mengubah data.", "Info");
            return;
        }
        userProfile.setJurusan(jurusanField.getText().trim());
        userProfile.setFakultas(fakultasField.getText().trim());
        userProfile.setBio(bioField.getText().trim());
        userProfile.setInstagramHandle(instagramField.getText().trim());
        userProfile.setTiktokHandle(tiktokField.getText().trim());
        userProfile.setXHandle(xField.getText().trim());
        userProfile.setLinkedinUrl(linkedinField.getText().trim());

        if (profileDAO.saveOrUpdateProfile(userProfile)) {
            setEditMode(false);
            loadProfileData();
            ModernDialog.showInfo(this, "Profil berhasil disimpan!", "Sukses");
        } else {
            ModernDialog.showError(this, "Gagal menyimpan profil.", "Error");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  COMPONENT BUILDERS
    // ══════════════════════════════════════════════════════════════════

    /** Card section header: icon circle + bold title */
    private JPanel createCardHeader(String emoji, String title) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 20, 12, 20));

        JPanel circle = buildIconCircle(emoji, 30);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_DARK);

        header.add(circle);
        header.add(lbl);
        return header;
    }

    /** Row: icon | label | editable field (for Informasi Dasar) */
    private JPanel buildFieldRow(String emoji, String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(180, 30));

        JPanel circle = buildIconCircle(emoji, 28);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_MUTED);
        left.add(circle);
        left.add(lbl);

        styleReadonlyField(field);

        row.add(left,  BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /** Row: icon | platform label | editable field | external link icon (for Media Sosial) */
    private JPanel buildSocialRow(Icon icon, String platform, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(160, 30));

        JPanel circle = buildIconCircle(icon, 28);
        JLabel lbl = new JLabel(platform);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_MUTED);
        left.add(circle);
        left.add(lbl);

        styleReadonlyField(field);

        // External link icon (right)
        JLabel extIcon = new JLabel("\u2197"); // ↗
        extIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        extIcon.setForeground(TEXT_MUTED);
        extIcon.setBorder(new EmptyBorder(0, 8, 0, 0));
        extIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel right = new JPanel(new BorderLayout(6, 0));
        right.setOpaque(false);
        right.add(field,   BorderLayout.CENTER);
        right.add(extIcon, BorderLayout.EAST);

        row.add(left,  BorderLayout.WEST);
        row.add(right, BorderLayout.CENTER);
        return row;
    }

    /** One slot in Detail Akun (icon + label + value) */
    private JPanel buildAccountDetail(String emoji, String label, String value) {
        JPanel col = new JPanel(new BorderLayout(10, 0));
        col.setOpaque(false);
        col.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconWrap.setOpaque(false);
        JPanel circle = buildIconCircle(emoji, 30);
        iconWrap.add(circle);

        JPanel text = new JPanel(new BorderLayout(0, 3));
        text.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MUTED);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(TEXT_DARK);
        text.add(lbl, BorderLayout.NORTH);
        text.add(val, BorderLayout.SOUTH);

        col.add(iconWrap, BorderLayout.WEST);
        col.add(text,     BorderLayout.CENTER);
        return col;
    }

    /** Small icon + text pair (used in hero card sub-info) */
    private JPanel buildIconTextRow(String emoji, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        JLabel iconLbl = new JLabel(emoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLbl.setForeground(TEXT_MUTED);
        row.add(iconLbl);
        row.add(textLbl);
        return row;
    }

    /** 1px horizontal divider */
    private JPanel buildDivider() {
        JPanel d = new JPanel();
        d.setBackground(BORDER_CLR);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(0, 1));
        return d;
    }

    /** Icon circle helper */
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

    private JPanel buildIconCircle(Icon icon, int size) {
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
        JLabel lbl = new JLabel(icon);
        circle.add(lbl);
        return circle;
    }

    /** Read-only style for text field */
    private void styleReadonlyField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(0xF9, 0xFA, 0xFB));
        field.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        field.setEditable(false);
    }

    /** Green filled button */
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

    /** Outline (ghost) button */
    private JButton buildOutlineButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? GREEN_LIGHT : CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(GREEN_PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(GREEN_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILS
    // ══════════════════════════════════════════════════════════════════
    private void setText(JTextField field, String value) {
        if (field != null) field.setText(value != null ? value : "");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    // ══════════════════════════════════════════════════════════════════
    //  ROUNDED CARD
    // ══════════════════════════════════════════════════════════════════
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
}