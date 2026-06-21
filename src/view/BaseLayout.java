package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseLayout extends JFrame {
    private String userName;
    private String userRole;
    
    private JPanel sidebarPanel;
    private JPanel menuContainer;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private List<JButton> menuButtons;
    private Map<String, JPanel> pageMap;
    private String activePageLabel = "";
    
    // Aesthetic Colors (Sleek Dark Slate & Indigo accent)
    private static final Color COLOR_SIDEBAR_BG = new Color(20, 24, 33);       // Dark Slate
    private static final Color COLOR_MENU_HOVER = new Color(34, 43, 58);       // Slate Hover
    private static final Color COLOR_MENU_ACTIVE = new Color(42, 54, 74);      // Slate Active
    private static final Color COLOR_MENU_TEXT = new Color(156, 163, 175);     // Gray-400 Text
    private static final Color COLOR_MENU_TEXT_ACTIVE = Color.WHITE;
    private static final Color COLOR_ACCENT = new Color(37, 99, 235);          // Blue Accent
    private static final Color COLOR_LOGOUT_HOVER = new Color(239, 68, 68, 30); // Soft Red Hover
    
    public BaseLayout(String frameTitle, String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;
        this.menuButtons = new ArrayList<>();
        // Using LinkedHashMap to preserve insertion order
        this.pageMap = new LinkedHashMap<>();
        
        setTitle(frameTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);
        
        // Main window layout
        getContentPane().setLayout(new BorderLayout());
        
        // 1. Sidebar Panel (Left)
        initSidebar();
        
        // 2. Content Panel (Right)
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }
    
    private void initSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(240, 700));
        sidebarPanel.setBackground(COLOR_SIDEBAR_BG);
        
        // Top Header: Logo + App Name + User Info
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 15, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        
        JLabel logoLabel = new JLabel("📚 SIERA PKKMB");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, gbc);
        
        // Separator Line
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0);
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(51, 65, 85));
        headerPanel.add(separator, gbc);
        
        // User Profile Summary in Sidebar
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 0, 0, 0);
        JLabel userLabel = new JLabel(userName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 0, 15, 0);
        JLabel roleLabel = new JLabel(userRole.toUpperCase());
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        roleLabel.setForeground(new Color(148, 163, 184)); // Muted blue/gray
        headerPanel.add(roleLabel, gbc);
        
        // Middle Panel: Menu Items Container
        menuContainer = new JPanel();
        menuContainer.setOpaque(false);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        // Bottom Panel: Logout Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 20, 10));
        
        JButton logoutBtn = createSidebarButton("Logout", "🚪");
        logoutBtn.addActionListener(e -> handleLogout());
        // Custom red hover style for Logout button
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(COLOR_LOGOUT_HOVER);
                logoutBtn.setForeground(Color.RED);
                logoutBtn.setContentAreaFilled(true);
                logoutBtn.setOpaque(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(0, 0, 0, 0));
                logoutBtn.setForeground(COLOR_MENU_TEXT);
                logoutBtn.setContentAreaFilled(false);
                logoutBtn.setOpaque(false);
            }
        });
        bottomPanel.add(logoutBtn, BorderLayout.CENTER);
        
        sidebarPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Wrap menu container in a transparent JScrollPane for safe sizing
        JScrollPane scrollPane = new JScrollPane(menuContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JButton createSidebarButton(String label, String icon) {
        JButton button = new JButton("  " + icon + "   " + label);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(COLOR_MENU_TEXT);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(220, 42));
        button.setPreferredSize(new Dimension(220, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 2, 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));
        
        return button;
    }
    
    public void addMenuItem(String label, String icon, JPanel panel) {
        pageMap.put(label, panel);
        contentPanel.add(panel, label);
        
        JButton btn = createSidebarButton(label, icon);
        btn.addActionListener(e -> switchPage(label));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!activePageLabel.equals(label)) {
                    btn.setBackground(COLOR_MENU_HOVER);
                    btn.setContentAreaFilled(true);
                    btn.setOpaque(true);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!activePageLabel.equals(label)) {
                    btn.setBackground(new Color(0, 0, 0, 0));
                    btn.setContentAreaFilled(false);
                    btn.setOpaque(false);
                }
            }
        });
        
        menuContainer.add(btn);
        menuContainer.add(Box.createVerticalStrut(6));
        menuButtons.add(btn);
    }
    
    public void switchPage(String label) {
        activePageLabel = label;
        cardLayout.show(contentPanel, label);
        
        // Update selection UI states
        for (JButton btn : menuButtons) {
            String btnText = btn.getText().trim();
            if (btnText.endsWith(label)) {
                btn.setBackground(COLOR_MENU_ACTIVE);
                btn.setForeground(COLOR_MENU_TEXT_ACTIVE);
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
                // Active blue left border indicator
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, COLOR_ACCENT),
                    BorderFactory.createEmptyBorder(0, 10, 0, 0)
                ));
            } else {
                btn.setBackground(new Color(0, 0, 0, 0));
                btn.setForeground(COLOR_MENU_TEXT);
                btn.setContentAreaFilled(false);
                btn.setOpaque(false);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 2, 2, 2),
                    BorderFactory.createEmptyBorder(0, 10, 0, 0)
                ));
            }
        }
    }
    
    public void showTemporaryPage(String label, JPanel panel) {
        contentPanel.add(panel, label);
        switchPage(label);
    }
    
    public void initializeLayout() {
        if (!pageMap.isEmpty()) {
            // Default to Dashboard page, or first page if not found
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
