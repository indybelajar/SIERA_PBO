package view;

import dao.AttendanceDAO;
import model.Attendance;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class AttendanceView extends JPanel {
    private int userId;
    private AttendanceDAO attendanceDAO;
    private JPanel cardsPanel;

    private static final Color BG_PAGE = new Color(0xF4, 0xF6, 0xF8);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x2E);
    private static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color GREEN_PRIMARY = new Color(34, 166, 90);
    private static final Color GREEN_LIGHT = new Color(232, 245, 238);
    
    public AttendanceView(int userId) {
        this.userId = userId;
        this.attendanceDAO = new AttendanceDAO();
        initComponents();
        loadAttendance();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);

        add(createTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_PAGE);
        body.setBorder(new EmptyBorder(24, 28, 24, 28));

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        body.add(scrollPane, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(18, 28, 18, 28)
        ));

        JPanel textPanel = new JPanel(new BorderLayout(0, 3));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Attendance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Lihat riwayat kehadiran dan status absensimu selama kegiatan.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);
        bar.add(textPanel, BorderLayout.WEST);

        return bar;
    }
    
    private void loadAttendance() {
        cardsPanel.removeAll();
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByUserId(userId);
        
        if (attendanceList.isEmpty()) {
            RoundedPanel emptyPanel = new RoundedPanel();
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.setBorder(new EmptyBorder(42, 24, 42, 24));
            emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

            JLabel emptyLabel = new JLabel("Belum ada data absensi.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(TEXT_MUTED);
            emptyPanel.add(emptyLabel);
            cardsPanel.add(emptyPanel);
            cardsPanel.add(Box.createVerticalGlue());
        } else {
            for (Attendance att : attendanceList) {
                JPanel card = createAttendanceCard(att);
                cardsPanel.add(card);
                cardsPanel.add(Box.createVerticalStrut(15));
            }
            cardsPanel.add(Box.createVerticalGlue());
        }
        
        cardsPanel.revalidate();
        cardsPanel.repaint();
     }
    
    private JPanel createAttendanceCard(Attendance att) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(18, 0));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel iconCircle = createIconCircle(att.getStatus());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel agendaLabel = new JLabel(capitalize(att.getAgenda()));
        agendaLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        agendaLabel.setForeground(TEXT_DARK);
        leftPanel.add(agendaLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        
        JLabel dateLabel = new JLabel(formatDate(att.getAttendanceDate().toString()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(TEXT_MUTED);
        dateLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel datePill = new RoundedPill(new Color(0xF8, 0xFA, 0xFC), BORDER_CLR);
        datePill.setLayout(new BorderLayout());
        datePill.add(dateLabel, BorderLayout.CENTER);
        datePill.setMaximumSize(new Dimension(180, 28));
        datePill.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(datePill);

        if (att.getNotes() != null && !att.getNotes().trim().isEmpty()) {
            JLabel notesLabel = new JLabel("Catatan: " + att.getNotes());
            notesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            notesLabel.setForeground(TEXT_MUTED);
            notesLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            leftPanel.add(notesLabel);
        }

        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(150, 0));
        rightPanel.add(createStatusBadge(att.getStatus()), BorderLayout.NORTH);
        rightPanel.add(createSmallNote(getStatusNote(att.getStatus())), BorderLayout.SOUTH);

        card.add(iconCircle, BorderLayout.WEST);
        card.add(leftPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        boolean hasNotes = att.getNotes() != null && !att.getNotes().trim().isEmpty();
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, hasNotes ? 130 : 110));
        card.setPreferredSize(new Dimension(850, hasNotes ? 130 : 110));
        
        return card;
    }

    private JPanel createIconCircle(String status) {
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
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
        iconCircle.setPreferredSize(new Dimension(46, 46));
        iconCircle.setMaximumSize(new Dimension(46, 46));

        JLabel icon = new JLabel(getStatusInitial(status));
        icon.setFont(new Font("Segoe UI", Font.BOLD, 15));
        icon.setForeground(GREEN_PRIMARY);
        iconCircle.add(icon);

        return iconCircle;
    }

    private JLabel createStatusBadge(String status) {
        Color bg;
        Color fg;
        String text = capitalize(status);
        String normalized = status == null ? "" : status.trim().toLowerCase();

        switch (normalized) {
            case "hadir":
                bg = new Color(220, 252, 231);
                fg = new Color(22, 163, 74);
                break;
            case "izin":
            case "sakit":
                bg = new Color(254, 243, 199);
                fg = new Color(180, 120, 0);
                break;
            default:
                bg = new Color(254, 226, 226);
                fg = new Color(220, 38, 38);
                break;
        }

        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
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
        badge.setBorder(new EmptyBorder(7, 12, 7, 12));
        return badge;
    }

    private JLabel createSmallNote(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private String getStatusInitial(String status) {
        String normalized = status == null ? "" : status.trim().toLowerCase();
        if ("hadir".equals(normalized)) return "H";
        if ("izin".equals(normalized)) return "I";
        if ("sakit".equals(normalized)) return "S";
        return "A";
    }

    private String getStatusNote(String status) {
        String normalized = status == null ? "" : status.trim().toLowerCase();
        if ("hadir".equals(normalized)) return "Tercatat hadir";
        if ("izin".equals(normalized)) return "Izin tercatat";
        if ("sakit".equals(normalized)) return "Sakit tercatat";
        return "Tidak hadir";
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String formatDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
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

    private static class RoundedPanel extends JPanel {
        RoundedPanel() {
            setOpaque(false);
            setBackground(CARD_BG);
        }

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

    private static class RoundedPill extends JPanel {
        private Color bg;
        private Color border;

        RoundedPill(Color bg, Color border) {
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(border);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}



