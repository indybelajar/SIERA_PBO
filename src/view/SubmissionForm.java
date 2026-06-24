package view;

import database.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class SubmissionForm extends JDialog {
    private int taskId;
    private long userId;
    private JTextField linkField;

    private static final Color GREEN_PRIMARY = new Color(34, 166, 90);
    private static final Color GREEN_DARK    = new Color(23, 122, 66);
    private static final Color BG_PAGE       = new Color(0xF4, 0xF6, 0xF8);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color BORDER_CLR    = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT_DARK     = new Color(0x1A, 0x1A, 0x2E);
    private static final Color TEXT_MUTED    = new Color(0x6B, 0x72, 0x80);

    public SubmissionForm(int taskId, long userId) {
        this.taskId = taskId;
        this.userId = userId;
        initComponents();
    }

    private void initComponents() {
        setTitle("Kumpulkan Tugas");
        setModal(true);
        setSize(460, 260);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_PAGE);
        setLayout(new BorderLayout());

        // ── Top bar ──────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD_BG);
        topBar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(16, 24, 16, 24)
        ));
        JLabel titleLbl = new JLabel("📤  Kumpulkan Tugas");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(TEXT_DARK);
        JLabel subLbl = new JLabel("Masukkan link submission kamu (Google Drive, dll)");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(TEXT_MUTED);
        JPanel topText = new JPanel(new BorderLayout(0, 4));
        topText.setOpaque(false);
        topText.add(titleLbl, BorderLayout.NORTH);
        topText.add(subLbl,   BorderLayout.SOUTH);
        topBar.add(topText, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // ── Body ─────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel fieldLabel = new JLabel("Link Submission");
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fieldLabel.setForeground(TEXT_DARK);

        linkField = new JTextField();
        linkField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        linkField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        linkField.setBackground(CARD_BG);
        linkField.setPreferredSize(new Dimension(0, 40));

        JPanel fieldPanel = new JPanel(new BorderLayout(0, 6));
        fieldPanel.setOpaque(false);
        fieldPanel.add(fieldLabel, BorderLayout.NORTH);
        fieldPanel.add(linkField,  BorderLayout.CENTER);

        body.add(fieldPanel, BorderLayout.CENTER);

        // ── Buttons ──────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = new JButton("Batal") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelBtn.setForeground(TEXT_DARK);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setOpaque(false);
        cancelBtn.setPreferredSize(new Dimension(90, 38));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        JButton submitBtn = new JButton("Kumpulkan") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? GREEN_DARK : GREEN_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setContentAreaFilled(false);
        submitBtn.setOpaque(false);
        submitBtn.setPreferredSize(new Dimension(120, 38));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.addActionListener(e -> submitTask());

        btnRow.add(cancelBtn);
        btnRow.add(submitBtn);
        body.add(btnRow, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);
    }

    private void submitTask() {
        String link = linkField.getText().trim();
        if (link.isEmpty()) {
            ModernDialog.showWarning(this,
                "Link submission tidak boleh kosong!",
                "Peringatan");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                ModernDialog.showError(this,
                    "Gagal terhubung ke database.",
                    "Error");
                return;
            }

            String checkQuery = "SELECT id FROM task_submissions WHERE task_id = ? AND user_id = ?";
            boolean exists = false;
            try (PreparedStatement cs = conn.prepareStatement(checkQuery)) {
                cs.setInt(1, taskId);
                cs.setLong(2, userId);
                try (ResultSet rs = cs.executeQuery()) { exists = rs.next(); }
            }

            boolean success;
            if (exists) {
                String q = "UPDATE task_submissions SET submission_link = ?, status = 'Submitted', submitted_at = NOW() WHERE task_id = ? AND user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setString(1, link);
                    ps.setInt(2, taskId);
                    ps.setLong(3, userId);
                    success = ps.executeUpdate() > 0;
                }
            } else {
                String q = "INSERT INTO task_submissions (task_id, user_id, submission_link, status, submitted_at) VALUES (?, ?, ?, 'Submitted', NOW())";
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setInt(1, taskId);
                    ps.setLong(2, userId);
                    ps.setString(3, link);
                    success = ps.executeUpdate() > 0;
                }
            }

            if (success) {
                ModernDialog.showInfo(this,
                    "Tugas berhasil dikumpulkan! 🎉",
                    "Berhasil");
                dispose();
            } else {
                ModernDialog.showError(this,
                    "Gagal mengumpulkan tugas.",
                    "Error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ModernDialog.showError(this,
                "Database error: " + e.getMessage(),
                "Error");
        }
    }
}