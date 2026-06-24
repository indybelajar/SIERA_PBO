package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ModernDialog extends JDialog {
    private boolean confirmed = false;

    private ModernDialog(Window parent, String title, String message, String type, boolean isConfirm) {
        super(parent, title, Dialog.ModalityType.APPLICATION_MODAL);
        init(message, type, isConfirm);
    }

    private void init(String message, String type, boolean isConfirm) {
        setSize(420, 220);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Top Banner with Icon & Title
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        JLabel iconLbl = new JLabel();
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel titleLbl = new JLabel(getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(new Color(26, 26, 46));

        if ("success".equalsIgnoreCase(type)) {
            iconLbl.setText("🎉");
            titleLbl.setForeground(new Color(34, 166, 90));
        } else if ("error".equalsIgnoreCase(type)) {
            iconLbl.setText("❌");
            titleLbl.setForeground(new Color(220, 38, 38));
        } else if ("warning".equalsIgnoreCase(type)) {
            iconLbl.setText("⚠️");
            titleLbl.setForeground(new Color(180, 120, 0));
        } else {
            iconLbl.setText("❓");
            titleLbl.setForeground(new Color(26, 26, 46));
        }

        topBar.add(iconLbl);
        topBar.add(titleLbl);
        add(topBar, BorderLayout.NORTH);

        // Body Text Area (for automatic wrapping)
        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgArea.setForeground(new Color(75, 85, 99));
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setEditable(false);
        msgArea.setFocusable(false);
        msgArea.setBackground(Color.WHITE);
        msgArea.setBorder(new EmptyBorder(20, 24, 10, 24));

        JScrollPane scroll = new JScrollPane(msgArea);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnRow.setBackground(new Color(249, 250, 251));
        btnRow.setBorder(new MatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        if (isConfirm) {
            JButton noBtn = buildButton("Tidak", false);
            noBtn.addActionListener(e -> {
                confirmed = false;
                dispose();
            });
            JButton yesBtn = buildButton("Ya", true);
            yesBtn.addActionListener(e -> {
                confirmed = true;
                dispose();
            });
            btnRow.add(noBtn);
            btnRow.add(yesBtn);
        } else {
            JButton okBtn = buildButton("OK", true);
            okBtn.addActionListener(e -> dispose());
            btnRow.add(okBtn);
        }
        add(btnRow, BorderLayout.SOUTH);
    }

    private JButton buildButton(String text, boolean primary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primary) {
                    Color bg = getModel().isPressed() ? new Color(23, 122, 66) :
                               getModel().isRollover() ? new Color(28, 148, 78) : new Color(34, 166, 90);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(243, 244, 246) : Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(229, 231, 235));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(primary ? Color.WHITE : new Color(26, 26, 46));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(90, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Static Utility Methods
    public static void showInfo(Component parent, String message, String title) {
        Window window = getParentWindow(parent);
        ModernDialog dialog = new ModernDialog(window, title, message, "success", false);
        dialog.setVisible(true);
    }

    public static void showError(Component parent, String message, String title) {
        Window window = getParentWindow(parent);
        ModernDialog dialog = new ModernDialog(window, title, message, "error", false);
        dialog.setVisible(true);
    }

    public static void showWarning(Component parent, String message, String title) {
        Window window = getParentWindow(parent);
        ModernDialog dialog = new ModernDialog(window, title, message, "warning", false);
        dialog.setVisible(true);
    }

    public static boolean showConfirm(Component parent, String message, String title) {
        Window window = getParentWindow(parent);
        ModernDialog dialog = new ModernDialog(window, title, message, "confirm", true);
        dialog.setVisible(true);
        return dialog.confirmed;
    }

    private static Window getParentWindow(Component c) {
        if (c == null) return null;
        if (c instanceof Window) return (Window) c;
        return getParentWindow(c.getParent());
    }
}
