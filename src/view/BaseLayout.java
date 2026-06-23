package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseLayout extends JFrame {
    private String userName;
    private String userRole;

    protected JPanel sidebarPanel;
    private JPanel menuContainer;
    protected JPanel contentPanel;
    private CardLayout cardLayout;

    private List<JButton> menuButtons;
    private Map<String, JPanel> pageMap;
    private String activePageLabel = "";

    // ── SIERA Brand Colors (semua nilai 0-255, tidak ada hex melebihi 0xFF=255) ──
    static final Color GREEN_PRIMARY      = new Color(34,  166, 90);
    static final Color GREEN_DARK         = new Color(23,  122, 66);
    static final Color GREEN_LIGHT        = new Color(232, 245, 238);
    static final Color SIDEBAR_BG         = Color.WHITE;
    static final Color SIDEBAR_HOVER      = new Color(245,247,248);
    static final Color TEXT_WHITE_MUTED   = new Color(107,114,128);
    static final Color MENU_TEXT_INACTIVE = new Color(26,26,46);

    // Warna dengan alpha — gunakan format (r, g, b, alpha) semua 0-255
    private static final Color DIVIDER_COLOR   = new Color(229,231,235);
    private static final Color ICON_BG_COLOR   = new Color(232,245,238);
    private static final Color ACTIVE_MENU_BG  = new Color(232,245,238);
    private static final Color AVATAR_RING     = new Color(232,245,238);
    private static final Color AVATAR_FACE     = new Color(255, 208, 144);
    private static final Color ROLE_BADGE_BG   = new Color(232,245,238);
    private static final Color ROLE_BADGE_TEXT = GREEN_PRIMARY;
    private static final Color LOGOUT_RED_BG   = new Color(255, 68,  68,  60);
    private static final Color LOGOUT_RED_TEXT = new Color(255, 170, 170);
    private static final Color LOGO_ICON_BG    = new Color(20,  110, 56);
    private static final Color TRANSPARENT     = new Color(0,   0,   0,  0);

    public BaseLayout(String frameTitle, String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;
        this.menuButtons = new ArrayList<>();
        this.pageMap = new LinkedHashMap<>();

        setTitle(frameTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 760);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());
        initSidebar();

        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(new Color(244, 246, 248));

        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════
    private void initSidebar() {
        sidebarPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setOpaque(false);

        // ── TOP SECTION: Logo + Profil ────────────────────────────────
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        // -- Logo block --
        JPanel logoBlock = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        logoBlock.setOpaque(false);
        logoBlock.setBorder(new EmptyBorder(22, 10, 18, 10));

        JPanel logoIcon = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(LOGO_ICON_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoIcon.setOpaque(false);
        logoIcon.setPreferredSize(new Dimension(44, 44));
        JLabel logoEmoji = new JLabel("\uD83C\uDFEB");
        logoEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        logoIcon.add(logoEmoji);

        JPanel logoTextPanel = new JPanel(new BorderLayout(0, 1));
        logoTextPanel.setOpaque(false);
        JLabel sieraName = new JLabel("SIERA");
        sieraName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        sieraName.setForeground(new Color(26,26,46));
        JPanel subLines = new JPanel(new GridLayout(2, 1, 0, 0));
        subLines.setOpaque(false);
        JLabel sub1 = new JLabel("SISTEM INFORMASI");
        sub1.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sub1.setForeground(TEXT_WHITE_MUTED);
        JLabel sub2 = new JLabel("MENTORING");
        sub2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sub2.setForeground(TEXT_WHITE_MUTED);
        subLines.add(sub1);
        subLines.add(sub2);
        logoTextPanel.add(sieraName, BorderLayout.NORTH);
        logoTextPanel.add(subLines,  BorderLayout.SOUTH);

        logoBlock.add(logoIcon);
        logoBlock.add(logoTextPanel);

        // -- Divider 1 --
        topSection.add(logoBlock);
        topSection.add(makeDivider());

        // -- Profile block --
        JPanel profileBlock = new JPanel(new BorderLayout(12, 0));
        profileBlock.setOpaque(false);
        profileBlock.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Avatar circle
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AVATAR_RING);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(AVATAR_FACE);
                g2.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(48, 48));
        avatar.setMinimumSize(new Dimension(48, 48));
        avatar.setMaximumSize(new Dimension(48, 48));
        avatar.setOpaque(false);
        JLabel avatarIcon = new JLabel("\uD83D\uDC64");
        avatarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        avatar.add(avatarIcon);

        // Profile text
        JPanel profileText = new JPanel();
        profileText.setLayout(new BoxLayout(profileText, BoxLayout.Y_AXIS));
        profileText.setOpaque(false);

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(new Color(26,26,46));

        // Role badge
        JLabel roleLabel = new JLabel(userRole.toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ROLE_BADGE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        roleLabel.setForeground(ROLE_BADGE_TEXT);
        roleLabel.setOpaque(false);
        roleLabel.setBorder(new EmptyBorder(2, 7, 2, 7));

        JLabel groupLabel = new JLabel("Kelompok 07");
        groupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        groupLabel.setForeground(TEXT_WHITE_MUTED);

        profileText.add(nameLabel);
        profileText.add(Box.createRigidArea(new Dimension(0, 4)));
        profileText.add(roleLabel);
        profileText.add(Box.createRigidArea(new Dimension(0, 3)));
        profileText.add(groupLabel);

        profileBlock.add(avatar,      BorderLayout.WEST);
        profileBlock.add(profileText, BorderLayout.CENTER);

        topSection.add(profileBlock);
        topSection.add(makeDivider());

        // ── MENU container ────────────────────────────────────────────
        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(6, 10, 6, 10));

        JScrollPane menuScroll = new JScrollPane(menuContainer);
        menuScroll.setOpaque(false);
        menuScroll.getViewport().setOpaque(false);
        menuScroll.setBorder(null);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        // ── BOTTOM: Logout ────────────────────────────────────────────
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setOpaque(false);
        bottomSection.add(makeDivider(), BorderLayout.NORTH);

        JPanel logoutWrapper = new JPanel(new BorderLayout());
        logoutWrapper.setOpaque(false);
        logoutWrapper.setBorder(new EmptyBorder(4, 10, 20, 10));

        JButton logoutBtn = createMenuButton("Logout", "\u21AA");
        logoutBtn.addActionListener(e -> handleLogout());
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!activePageLabel.equals("Logout")) {
                    logoutBtn.setBackground(LOGOUT_RED_BG);
                    logoutBtn.setContentAreaFilled(true);
                    logoutBtn.setOpaque(true);
                    JLabel tl = (JLabel) logoutBtn.getClientProperty("textLbl");
                    JLabel il = (JLabel) logoutBtn.getClientProperty("iconLbl");
                    if (tl != null) tl.setForeground(LOGOUT_RED_TEXT);
                    if (il != null) il.setForeground(LOGOUT_RED_TEXT);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!activePageLabel.equals("Logout")) {
                    logoutBtn.setBackground(TRANSPARENT);
                    logoutBtn.setContentAreaFilled(false);
                    logoutBtn.setOpaque(false);
                    JLabel tl = (JLabel) logoutBtn.getClientProperty("textLbl");
                    JLabel il = (JLabel) logoutBtn.getClientProperty("iconLbl");
                    if (tl != null) tl.setForeground(MENU_TEXT_INACTIVE);
                    if (il != null) il.setForeground(MENU_TEXT_INACTIVE);
                }
            }
        });
        logoutWrapper.add(logoutBtn, BorderLayout.CENTER);
        bottomSection.add(logoutWrapper, BorderLayout.CENTER);

        sidebarPanel.add(topSection,    BorderLayout.NORTH);
        sidebarPanel.add(menuScroll,    BorderLayout.CENTER);
        sidebarPanel.add(bottomSection, BorderLayout.SOUTH);
    }

    // ── Helper: divider line ──────────────────────────────────────────
    private JPanel makeDivider() {
        JPanel d = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(DIVIDER_COLOR);
                g.fillRect(16, 0, getWidth() - 32, 1);
            }
        };
        d.setOpaque(false);
        d.setPreferredSize(new Dimension(0, 1));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return d;
    }

    // ── Menu button factory ───────────────────────────────────────────
    private JButton createMenuButton(String label, String icon) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (isOpaque() && getBackground().getAlpha() > 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(212, 44));
        btn.setPreferredSize(new Dimension(212, 44));
        btn.setMinimumSize(new Dimension(212, 44));
        btn.setBorder(new EmptyBorder(0, 6, 0, 6));

        // Icon circle
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_BG_COLOR);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(28, 28));
        iconCircle.setMaximumSize(new Dimension(28, 28));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLbl.setForeground(MENU_TEXT_INACTIVE);
        iconCircle.add(iconLbl);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLbl.setForeground(MENU_TEXT_INACTIVE);

        btn.add(iconCircle);
        btn.add(textLbl);

        btn.putClientProperty("iconCircle", iconCircle);
        btn.putClientProperty("iconLbl",    iconLbl);
        btn.putClientProperty("textLbl",    textLbl);
        btn.putClientProperty("label",      label);

        return btn;
    }

    // ── Public API ────────────────────────────────────────────────────
    public void addMenuItem(String label, String iconEmoji, JPanel panel) {
        pageMap.put(label, panel);
        contentPanel.add(panel, label);

        JButton btn = createMenuButton(label, iconEmoji);
        btn.addActionListener(e -> switchPage(label));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!activePageLabel.equals(label)) {
                    btn.setBackground(SIDEBAR_HOVER);
                    btn.setContentAreaFilled(true);
                    btn.setOpaque(true);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!activePageLabel.equals(label)) {
                    btn.setBackground(TRANSPARENT);
                    btn.setContentAreaFilled(false);
                    btn.setOpaque(false);
                }
            }
        });

        menuContainer.add(btn);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        menuButtons.add(btn);
    }

    public void switchPage(String label) {
        activePageLabel = label;
        cardLayout.show(contentPanel, label);
        updateMenuStyles();
    }

    private void updateMenuStyles() {
        for (JButton btn : menuButtons) {
            String lbl     = (String) btn.getClientProperty("label");
            JLabel iconLbl = (JLabel) btn.getClientProperty("iconLbl");
            JLabel textLbl = (JLabel) btn.getClientProperty("textLbl");
            boolean active = activePageLabel.equals(lbl);

            if (active) {
                btn.setBackground(ACTIVE_MENU_BG);
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
                if (textLbl != null) { textLbl.setFont(new Font("Segoe UI", Font.BOLD,  14)); textLbl.setForeground(GREEN_PRIMARY); }
                if (iconLbl != null) { iconLbl.setForeground(GREEN_PRIMARY); }
            } else {
                btn.setBackground(TRANSPARENT);
                btn.setContentAreaFilled(false);
                btn.setOpaque(false);
                if (textLbl != null) { textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14)); textLbl.setForeground(MENU_TEXT_INACTIVE); }
                if (iconLbl != null) { iconLbl.setForeground(MENU_TEXT_INACTIVE); }
            }
            btn.repaint();
        }
    }

    public void showTemporaryPage(String label, JPanel panel) {
        contentPanel.add(panel, label);
        switchPage(label);
    }

    public void initializeLayout() {
        if (!pageMap.isEmpty()) {
            String defaultLabel = pageMap.keySet().iterator().next();
            for (String key : pageMap.keySet()) {
                if (key.equalsIgnoreCase("dashboard")) {
                    defaultLabel = key;
                    break;
                }
            }
            switchPage(defaultLabel);
        }
    }

    private void handleLogout() {
        Router.logout(this);
    }
}