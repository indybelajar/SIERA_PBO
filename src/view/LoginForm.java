package view;

import dao.UserDAO;
import model.User;
import model.Mentor;
import model.Mentee;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginForm extends JFrame {

    // ── Card routing ──────────────────────────────────────────────────────────
    private CardLayout formCardLayout;
    private JPanel     cardsPanel;

    // ── Login fields ──────────────────────────────────────────────────────────
    private JTextField    emailField;
    private JPasswordField passwordField;
    private JButton        loginButton;

    // ── Register fields ───────────────────────────────────────────────────────
    private JTextField     regNameField;
    private JTextField     regEmailField;
    private JPasswordField regPasswordField;
    private JComboBox<String> regRoleComboBox;
    private JTextField     regGroupField;
    private JButton        registerButton;

    // ── DAO ───────────────────────────────────────────────────────────────────
    private UserDAO userDAO;

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color GREEN_DARK   = new Color(27,  94,  32);   // #1B5E20
    private static final Color GREEN_MID    = new Color(46, 125,  50);   // #2E7D32
    private static final Color GREEN_LIGHT  = new Color(76, 175,  80);   // #4CAF50
    private static final Color TEXT_DARK    = new Color(20,  40,  20);
    private static final Color TEXT_MUTED   = new Color(110, 140, 110);
    private static final Color BORDER_COLOR = new Color(200, 225, 200);
    private static final Color BG           = new Color(245, 250, 245);

    // ── Hero image (loaded async) ─────────────────────────────────────────────
    private BufferedImage heroImage = null;
    private static final String HERO_URL =
        "https://i.pinimg.com/736x/8c/ee/2d/8cee2d892c7c7e103d86aced174dee0c.jpg";

    // ────────────────────────────────────────────────────────────────────────
    public LoginForm() {
        userDAO = new UserDAO();
        loadHeroAsync();
        initComponents();
    }

    // ── Async image fetch ────────────────────────────────────────────────────
    private void loadHeroAsync() {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.submit(() -> {
            try {
                heroImage = ImageIO.read(new URL(HERO_URL));
                SwingUtilities.invokeLater(() -> repaint());
            } catch (Exception ignored) {}
        });
        ex.shutdown();
    }

    // ── Root layout ──────────────────────────────────────────────────────────
    private void initComponents() {
        setTitle("SIERA — PKKMB Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 680);
        setMinimumSize(new Dimension(700, 560));
        setLocationRelativeTo(null);
        setResizable(true);

        // Root: vertical stack  (hero on top, form on bottom)
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── TOP: Hero banner ─────────────────────────────────────────────────
        HeroPanel hero = new HeroPanel();
        hero.setPreferredSize(new Dimension(820, 260));
        root.add(hero, BorderLayout.NORTH);

        // ── BOTTOM: scrollable form area ─────────────────────────────────────
        formCardLayout = new CardLayout();
        cardsPanel     = new JPanel(formCardLayout);
        cardsPanel.setBackground(BG);
        cardsPanel.add(createLoginCard(),    "login");
        cardsPanel.add(createRegisterCard(), "register");
        formCardLayout.show(cardsPanel, "login");

        JScrollPane scroll = new JScrollPane(cardsPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        root.add(scroll, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ════════════════════════════════════════════════════════════════════════
    // HERO PANEL  — full-width image with fade-to-white at bottom
    // ════════════════════════════════════════════════════════════════════════
    private class HeroPanel extends JPanel {
        HeroPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            if (heroImage != null) {
                // Scale-to-fill (center-crop)
                double sx = (double) w / heroImage.getWidth();
                double sy = (double) h / heroImage.getHeight();
                double s  = Math.max(sx, sy);
                int sw = (int)(heroImage.getWidth()  * s);
                int sh = (int)(heroImage.getHeight() * s);
                int ox = (w - sw) / 2;
                int oy = (h - sh) / 2;
                g2.drawImage(heroImage, ox, oy, sw, sh, null);
            } else {
                // Placeholder gradient while loading
                GradientPaint gp = new GradientPaint(0, 0, GREEN_DARK, w, h, new Color(76, 175, 80));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2.setColor(new Color(255,255,255,180));
                String msg = "Memuat gambar kampus…";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (w - fm.stringWidth(msg))/2, h/2);
            }

            // Fade to white at the bottom (seamless transition to form)
            GradientPaint fade = new GradientPaint(
                0, h * 0.55f, new Color(255,255,255,0),
                0, h,         BG);
            g2.setPaint(fade);
            g2.fillRect(0, 0, w, h);

            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // LOGIN CARD
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createLoginCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        // White card panel
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(440, 340));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.weightx = 1;

        // Title
        JLabel title = new JLabel("Masuk ke akun Anda");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 0; c.insets = new Insets(0, 0, 20, 0);
        card.add(title, c);

        // Email
        c.gridy = 1; c.insets = new Insets(0, 0, 4, 0);
        card.add(fieldLabel("Email"), c);
        emailField = styledTextField(20);
        c.gridy = 2; c.insets = new Insets(0, 0, 14, 0);
        card.add(emailField, c);

        // Password
        c.gridy = 3; c.insets = new Insets(0, 0, 4, 0);
        card.add(fieldLabel("Password"), c);
        passwordField = styledPassword(20);
        c.gridy = 4; c.insets = new Insets(0, 0, 22, 0);
        card.add(passwordField, c);

        // Masuk button
        loginButton = greenButton("Masuk");
        loginButton.addActionListener(new LoginAction());
        c.gridy = 5; c.insets = new Insets(0, 0, 14, 0);
        card.add(loginButton, c);

        // Divider
        c.gridy = 6; c.insets = new Insets(0, 0, 10, 0);
        card.add(divider("atau"), c);

        // Switch link
        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        switchRow.setOpaque(false);
        JLabel q = smallLabel("Belum punya akun? ");
        JButton toReg = linkBtn("Daftar sini");
        toReg.addActionListener(e -> formCardLayout.show(cardsPanel, "register"));
        switchRow.add(q); switchRow.add(toReg);
        c.gridy = 7; c.insets = new Insets(0, 0, 0, 0);
        card.add(switchRow, c);

        outer.add(card, new GridBagConstraints());
        return outer;
    }

    // ════════════════════════════════════════════════════════════════════════
    // REGISTER CARD
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createRegisterCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(460, 430));
        card.setBorder(BorderFactory.createEmptyBorder(28, 40, 28, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.weightx = 1;

        // Title
        JLabel title = new JLabel("Buat Akun PKKMB");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 0; c.insets = new Insets(0, 0, 18, 0);
        card.add(title, c);

        // Full name
        c.gridy = 1; c.insets = new Insets(0, 0, 4, 0);
        card.add(fieldLabel("Nama Lengkap"), c);
        regNameField = styledTextField(20);
        c.gridy = 2; c.insets = new Insets(0, 0, 12, 0);
        card.add(regNameField, c);

        // Email row
        c.gridy = 3; c.insets = new Insets(0, 0, 4, 0);
        card.add(fieldLabel("Email"), c);
        JPanel emailRow = new JPanel(new BorderLayout(0, 0));
        emailRow.setOpaque(false);
        regEmailField = styledTextField(10);
        JLabel domain = new JLabel("  @mahasiswa.upnvj.ac.id  ");
        domain.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        domain.setForeground(TEXT_MUTED);
        domain.setOpaque(true);
        domain.setBackground(new Color(240, 248, 240));
        domain.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 1, 1, BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 4, 8, 8)));
        emailRow.add(regEmailField, BorderLayout.CENTER);
        emailRow.add(domain, BorderLayout.EAST);
        c.gridy = 4; c.insets = new Insets(0, 0, 12, 0);
        card.add(emailRow, c);

        // Password
        c.gridy = 5; c.insets = new Insets(0, 0, 4, 0);
        card.add(fieldLabel("Password"), c);
        regPasswordField = styledPassword(20);
        c.gridy = 6; c.insets = new Insets(0, 0, 12, 0);
        card.add(regPasswordField, c);

        // Role + Kelompok side by side
        JPanel twoCol = new JPanel(new GridLayout(1, 2, 12, 0));
        twoCol.setOpaque(false);

        JPanel rWrap = new JPanel(new BorderLayout(0, 4)); rWrap.setOpaque(false);
        rWrap.add(fieldLabel("Role"), BorderLayout.NORTH);
        regRoleComboBox = new JComboBox<>(new String[]{"mentee", "mentor"});
        regRoleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        regRoleComboBox.setBackground(Color.WHITE);
        regRoleComboBox.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        rWrap.add(regRoleComboBox, BorderLayout.CENTER);

        JPanel gWrap = new JPanel(new BorderLayout(0, 4)); gWrap.setOpaque(false);
        gWrap.add(fieldLabel("Kelompok"), BorderLayout.NORTH);
        regGroupField = styledTextField(8);
        gWrap.add(regGroupField, BorderLayout.CENTER);

        twoCol.add(rWrap); twoCol.add(gWrap);
        c.gridy = 7; c.insets = new Insets(0, 0, 20, 0);
        card.add(twoCol, c);

        // Register button
        registerButton = greenButton("Daftar Sekarang");
        registerButton.addActionListener(new RegisterAction());
        c.gridy = 8; c.insets = new Insets(0, 0, 12, 0);
        card.add(registerButton, c);

        // Switch link
        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        switchRow.setOpaque(false);
        switchRow.add(smallLabel("Sudah punya akun? "));
        JButton toLogin = linkBtn("Masuk");
        toLogin.addActionListener(e -> formCardLayout.show(cardsPanel, "login"));
        switchRow.add(toLogin);
        c.gridy = 9; c.insets = new Insets(0, 0, 0, 0);
        card.add(switchRow, c);

        outer.add(card, new GridBagConstraints());
        return outer;
    }

    // ════════════════════════════════════════════════════════════════════════
    // Widget helpers
    // ════════════════════════════════════════════════════════════════════════
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JTextField styledTextField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(Color.WHITE);
        f.setForeground(TEXT_DARK);
        f.setCaretColor(GREEN_MID);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        return f;
    }

    private JPasswordField styledPassword(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(Color.WHITE);
        f.setForeground(TEXT_DARK);
        f.setCaretColor(GREEN_MID);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        return f;
    }

    private JButton greenButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = getModel().isRollover() ? new Color(56,142,60) : GREEN_MID;
                Color bot = getModel().isRollover() ? GREEN_DARK : new Color(27,94,32);
                g2.setPaint(new GradientPaint(0,0,top,0,getHeight(),bot));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return btn;
    }

    private JButton linkBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(GREEN_MID);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel divider(String label) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        JSeparator l = new JSeparator(); l.setForeground(BORDER_COLOR);
        c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        p.add(l, c);
        JLabel lbl = new JLabel("  " + label + "  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        c.weightx = 0; p.add(lbl, c);
        JSeparator r = new JSeparator(); r.setForeground(BORDER_COLOR);
        c.weightx = 1; p.add(r, c);
        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // Action listeners
    // ════════════════════════════════════════════════════════════════════════
    private class LoginAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            String email    = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                err("Harap isi semua kolom!"); return;
            }
            User user = userDAO.login(email, password);
            if (user != null) {
                JOptionPane.showMessageDialog(LoginForm.this,
                    "Login berhasil! Selamat datang, " + user.getName(),
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                Router.navigateToDashboard(LoginForm.this, user);
            } else {
                err("Email atau password salah!");
            }
        }
    }

    private class RegisterAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            String name    = regNameField.getText().trim();
            String uname   = regEmailField.getText().trim();
            String pass    = new String(regPasswordField.getPassword()).trim();
            String role    = (String) regRoleComboBox.getSelectedItem();
            String group   = regGroupField.getText().trim();

            if (name.isEmpty() || uname.isEmpty() || pass.isEmpty() || group.isEmpty()) {
                err("Harap isi semua kolom!"); return;
            }
            if (uname.contains("@") || uname.contains(" ")) {
                err("Username email tidak boleh mengandung spasi atau '@'!"); return;
            }
            String email = uname + "@mahasiswa.upnvj.ac.id";
            if (userDAO.isEmailRegistered(email)) {
                err("Email ini sudah terdaftar!"); return;
            }
            User user = "mentor".equals(role)
                ? new Mentor(0, name, email, pass)
                : new Mentee(0, name, email, pass);
            if (userDAO.registerUser(user, group)) {
                JOptionPane.showMessageDialog(LoginForm.this,
                    "Pendaftaran berhasil! Silakan login.",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                regNameField.setText(""); regEmailField.setText("");
                regPasswordField.setText(""); regGroupField.setText("");
                formCardLayout.show(cardsPanel, "login");
                emailField.setText(email);
            } else {
                err("Pendaftaran gagal. Silakan coba lagi.");
            }
        }
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }
}