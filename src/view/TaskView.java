package view;

import dao.TaskDAO;
import model.Task;
import model.TaskSubmission;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TaskView extends JPanel {
    private long userId;
    private int groupId;
    private TaskDAO taskDAO;
    private JPanel cardsPanel;

    private static final Color BG_PAGE = new Color(0xF4, 0xF6, 0xF8);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x2E);
    private static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color GREEN_PRIMARY = new Color(34, 166, 90);
    
    public TaskView(long userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
        this.taskDAO = new TaskDAO();
        initComponents();
        loadTasks();
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadTasks();
            }
        });
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

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(18, 28, 18, 28)
        ));

        JPanel textPanel = new JPanel(new BorderLayout(0, 3));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Task Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Pantau tugas kelompokmu dan kumpulkan submission tepat waktu.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);
        bar.add(textPanel, BorderLayout.WEST);

        return bar;
    }
    
    private void loadTasks() {
        cardsPanel.removeAll();
        List<Task> tasks = taskDAO.getTasksByGroupId(groupId);

        if (tasks.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout()) {
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
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(42, 24, 42, 24));
            emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
            JLabel emptyLabel = new JLabel("Belum ada tugas untuk kelompokmu.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(TEXT_MUTED);
            emptyPanel.add(emptyLabel);
            cardsPanel.add(emptyPanel);
        } else {
            // Header label
            JLabel headerTitle = new JLabel("Daftar Tugas (" + tasks.size() + ")");
            headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
            headerTitle.setForeground(TEXT_DARK);
            headerTitle.setBorder(new EmptyBorder(0, 4, 12, 0));
            headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            cardsPanel.add(headerTitle);

            // 1 task = 1 card
            for (Task task : tasks) {
                cardsPanel.add(createTaskCard(task));
                cardsPanel.add(Box.createVerticalStrut(12));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
    
    private JPanel createTaskCard(Task task) {
        TaskSubmission submission = taskDAO.getSubmissionByTaskAndUser(task.getId(), userId);
        String status = (submission != null) ? submission.getStatus() : "Pending";

        JPanel card = new JPanel(new BorderLayout(18, 0)) {
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
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Kiri: info tugas
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(task.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(TEXT_MUTED);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setBorder(null);
        descArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Deadline pill
        JLabel deadlineLabel = new JLabel("Deadline: " + formatDateTime(task.getDeadline().toString()));
        deadlineLabel.setIcon(new SvgIcon(SvgIcon.Type.DEADLINE, 14, TEXT_MUTED));
        deadlineLabel.setIconTextGap(6);
        deadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deadlineLabel.setForeground(TEXT_MUTED);
        deadlineLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel deadlinePill = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF8, 0xFA, 0xFC));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        deadlinePill.setOpaque(false);
        deadlinePill.add(deadlineLabel, BorderLayout.CENTER);
        deadlinePill.setMaximumSize(new Dimension(280, 30));
        deadlinePill.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(descArea);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(deadlinePill);

        // Kanan: status + tombol
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(150, 0));

        JLabel statusBadge = createStatusBadge(status);
        rightPanel.add(statusBadge, BorderLayout.NORTH);

        if ("Accepted".equalsIgnoreCase(status)) {
            rightPanel.add(createSmallNote("Submission diterima"), BorderLayout.SOUTH);
        } else if ("Submitted".equalsIgnoreCase(status)) {
            rightPanel.add(createSmallNote("Menunggu review"), BorderLayout.SOUTH);
        } else {
            JButton addSubBtn = new JButton("+ Kumpulkan") {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? new Color(23, 122, 66) : GREEN_PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            addSubBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            addSubBtn.setForeground(Color.WHITE);
            addSubBtn.setFocusPainted(false);
            addSubBtn.setBorderPainted(false);
            addSubBtn.setContentAreaFilled(false);
            addSubBtn.setOpaque(false);
            addSubBtn.setBorder(new EmptyBorder(8, 14, 8, 14));
            addSubBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addSubBtn.addActionListener(e -> {
                SubmissionForm form = new SubmissionForm(task.getId(), userId);
                form.setVisible(true);
                loadTasks();
            });
            rightPanel.add(addSubBtn, BorderLayout.SOUTH);
        }

        card.add(leftPanel,  BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }

    private JLabel createStatusBadge(String status) {
        boolean accepted = "Accepted".equalsIgnoreCase(status);
        boolean submitted = "Submitted".equalsIgnoreCase(status);
        boolean rejected = "Rejected".equalsIgnoreCase(status);

        String text;
        Color bg;
        Color fg;
        if (accepted) {
            text = "Diterima";
            bg = new Color(220, 252, 231);
            fg = new Color(22, 163, 74);
        } else if (submitted) {
            text = "Terkirim";
            bg = new Color(243, 244, 246);
            fg = new Color(75, 85, 99);
        } else if (rejected) {
            text = "Ditolak";
            bg = new Color(254, 226, 226);
            fg = new Color(153, 27, 27);
        } else {
            text = "Belum Kumpul";
            bg = new Color(254, 243, 199);
            fg = new Color(180, 120, 0);
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
    
    private String formatDateTime(String dateStr) {
        // Date is in YYYY-MM-DD or YYYY-MM-DD HH:MM:SS format
        try {
            String timePart = "23.59";
            String datePart = dateStr;
            
            if (dateStr.contains(" ")) {
                String[] t = dateStr.split(" ");
                datePart = t[0];
                if (t[1].length() >= 5) {
                    timePart = t[1].substring(0, 5).replace(":", ".");
                }
            }
            
            String[] parts = datePart.split("-");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[2]);
                int month = Integer.parseInt(parts[1]);
                String year = parts[0];
                String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                                   "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                if (month >= 1 && month <= 12) {
                    return day + " " + months[month - 1] + " " + year + ", " + timePart;
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
