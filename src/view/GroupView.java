package view;

import dao.GroupDAO;
import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GroupView extends JPanel {
    private int groupId;
    private User currentUser;
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    
    private JLabel groupNameLabel;
    private JLabel mentorNameLabel;
    private JLabel mentorJurusanLabel;
    private JPanel mentorPanel;
    
    private JTable memberTable;
    private DefaultTableModel memberTableModel;
    
    private User mentorUser;
    private List<User> menteesList;

    // Warna statis lokal khusus struktural yang tidak ada di BaseLayout
    private static final Color BG_PAGE    = new Color(0xF9, 0xFA, 0xFB);
    private static final Color TEXT_DARK  = new Color(0x11, 0x18, 0x27);
    private static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xE5, 0xE7, 0xEB);

    public GroupView(int groupId, User currentUser) {
        this.groupId = groupId;
        this.currentUser = currentUser;
        this.groupDAO = new GroupDAO();
        this.userDAO = new UserDAO();
        this.menteesList = new ArrayList<>();
        
        initComponents();
        loadGroupData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_PAGE); 
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JPanel titleWrap = new JPanel(new GridLayout(2, 1, 0, 5));
        titleWrap.setOpaque(false);
        
        JPanel mainTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        mainTitlePanel.setOpaque(false);
        groupNameLabel = new JLabel("Kelompok 82");
        groupNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        groupNameLabel.setForeground(TEXT_DARK); 
        
        JLabel groupIcon = new JLabel("\uD83D\uDC65"); 
        groupIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        groupIcon.setForeground(BaseLayout.GREEN_PRIMARY);
        
        mainTitlePanel.add(groupNameLabel);
        mainTitlePanel.add(groupIcon);
        
        JLabel subtitleLabel = new JLabel("Kelola informasi kelompok dan data mentee.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_MUTED);
        
        titleWrap.add(mainTitlePanel);
        titleWrap.add(subtitleLabel);
        topSection.add(titleWrap, BorderLayout.WEST);
        
        JLabel bellIcon = new JLabel("\uD83D\uDD14");
        bellIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        bellIcon.setForeground(TEXT_MUTED);
        topSection.add(bellIcon, BorderLayout.EAST);
        
        add(topSection, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        centerPanel.add(createSectionHeader("\uD83D\uDC64", "Mentor", null));
        centerPanel.add(Box.createVerticalStrut(15));
        
        mentorPanel = createRoundedCardPanel();
        mentorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        
        JPanel mentorAvatar = createAvatarIcon();
        JPanel mentorTextPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        mentorTextPanel.setOpaque(false);
        
        mentorNameLabel = new JLabel("Zulfi Alisya");
        mentorNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mentorNameLabel.setForeground(TEXT_DARK);
        
        mentorJurusanLabel = new JLabel("S1 Sistem Informasi");
        mentorJurusanLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mentorJurusanLabel.setForeground(TEXT_MUTED);
        
        mentorTextPanel.add(mentorNameLabel);
        mentorTextPanel.add(mentorJurusanLabel);
        
        mentorPanel.add(mentorAvatar);
        mentorPanel.add(mentorTextPanel);
        
        centerPanel.add(mentorPanel);
        centerPanel.add(Box.createVerticalStrut(40));
        
        JPanel menteesHeader = new JPanel(new BorderLayout());
        menteesHeader.setOpaque(false);
        menteesHeader.add(createSectionHeader("\uD83D\uDC65", "Mentees", "Daftar mentee dalam kelompok ini."), BorderLayout.WEST);
        
        // Button tambah mentee removed per user request
        
        centerPanel.add(menteesHeader);
        centerPanel.add(Box.createVerticalStrut(15));
        
        JPanel tableCardPanel = createRoundedCardPanel();
        tableCardPanel.setLayout(new BorderLayout());
        tableCardPanel.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        
        // KONSISTENSI: Kolom "Aksi" dihilangkan sesuai request lu sebelumnya
        String[] columns = {"No.", "Nama", "NIM", "Jurusan", "Email", "Kontak"};
        memberTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        memberTable = new JTable(memberTableModel);
        memberTable.setRowHeight(50); 
        memberTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        memberTable.setOpaque(true);
        memberTable.setBackground(CARD_BG);
        memberTable.setForeground(TEXT_DARK);
        memberTable.setSelectionBackground(BaseLayout.GREEN_LIGHT);
        memberTable.setSelectionForeground(TEXT_DARK);
        
        // MENGAKTIFKAN GRID AGAR TABEL RAPI KOTAK-KOTAK
        memberTable.setShowGrid(true); 
        memberTable.setGridColor(BORDER_CLR);
        memberTable.setIntercellSpacing(new Dimension(1, 1));
        memberTable.setFillsViewportHeight(true);
        
        setupTableRenderers();
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(CARD_BG);
        
        tableCardPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tableCardPanel);
        add(centerPanel, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int side = Math.max(40, (w - 920) / 2);
                setBorder(new EmptyBorder(30, side, 30, side));
                revalidate();
            }
        });
    }
    
    private void setupTableRenderers() {
        // Preferred column widths
        int[] widths = {50, 220, 100, 180, 220, 150};
        for (int i = 0; i < widths.length; i++) {
            if (i < memberTable.getColumnCount()) {
                memberTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }
        memberTable.getColumnModel().getColumn(0).setMaxWidth(60);
        memberTable.getColumnModel().getColumn(2).setMaxWidth(120);

        DefaultTableCellRenderer standardRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSel, focus, r, c);
                lbl.setOpaque(true);
                if (isSel) {
                    lbl.setBackground(table.getSelectionBackground());
                    lbl.setForeground(table.getSelectionForeground());
                } else {
                    lbl.setBackground(CARD_BG);
                    lbl.setForeground(TEXT_DARK);
                }
                lbl.setBorder(new EmptyBorder(0, 15, 0, 15));
                if (c == 0 || c == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        };
        
        for (int i = 0; i < memberTable.getColumnCount(); i++) {
            memberTable.getColumnModel().getColumn(i).setCellRenderer(standardRenderer);
        }

        // Column 1 (Nama) now uses the standard cell renderer, showing text only

        memberTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
                wrap.setBackground(s ? t.getSelectionBackground() : CARD_BG);
                
                JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(BaseLayout.GREEN_LIGHT);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                        g2.dispose();
                    }
                };
                pill.setOpaque(false);
                JLabel txt = new JLabel(v != null ? v.toString() : "");
                txt.setFont(new Font("Segoe UI", Font.BOLD, 11));
                txt.setForeground(BaseLayout.GREEN_PRIMARY);
                pill.add(txt);
                wrap.add(pill);
                return wrap;
            }
        });

        JTableHeader header = memberTable.getTableHeader();
        header.setPreferredSize(new Dimension(100, 45));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setOpaque(true);
                lbl.setBackground(BG_PAGE);
                lbl.setForeground(TEXT_DARK);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, BORDER_CLR), new EmptyBorder(0, 15, 0, 15)
                ));
                if (c == 0 || c == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        };
        for (int i = 0; i < memberTable.getColumnModel().getColumnCount(); i++) {
            memberTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private JPanel createSectionHeader(String iconUnicode, String title, String subtitle) {
        JPanel wrapper = new JPanel(new GridLayout(subtitle != null ? 2 : 1, 1, 0, 2));
        wrapper.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel icon = new JLabel(iconUnicode);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        icon.setForeground(BaseLayout.GREEN_PRIMARY);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_DARK);
        
        titlePanel.add(icon);
        titlePanel.add(lblTitle);
        wrapper.add(titlePanel);
        
        if (subtitle != null) {
            JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            subPanel.setOpaque(false);
            JLabel lblSub = new JLabel(subtitle);
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblSub.setForeground(TEXT_MUTED);
            subPanel.add(lblSub);
            wrapper.add(subPanel);
        }
        return wrapper;
    }

    private JPanel createAvatarIcon() {
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_PAGE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(50, 50));
        iconCircle.setOpaque(false);
        // FIX EMOJI: Menggunakan emoji siluet standar agar tidak Tofu/Pucat di Windows
        JLabel iconLbl = new JLabel("\uD83D\uDC64"); 
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconCircle.add(iconLbl);
        return iconCircle;
    }

    private JPanel createRoundedCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_CLR); 
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JButton makeGreenButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BaseLayout.GREEN_DARK : BaseLayout.GREEN_PRIMARY); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
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
        btn.setPreferredSize(new Dimension(150, 38));
        return btn;
    }

    private void loadGroupData() {
        String groupName = groupDAO.getGroupNameById(groupId);
        groupNameLabel.setText(groupName != null ? groupName : "Kelompok");
        
        memberTableModel.setRowCount(0);
        menteesList.clear();
        mentorUser = null;
        
        List<User> members = groupDAO.getGroupMembers(groupId);
        for (User member : members) {
            if ("mentor".equalsIgnoreCase(member.getRole())) {
                mentorUser = member;
            } else {
                menteesList.add(member);
            }
        }
        
        if (mentorUser != null) {
            mentorNameLabel.setText(mentorUser.getName());
            String jurusan = (mentorUser.getJurusan() == null || mentorUser.getJurusan().trim().isEmpty())
                             ? "Jurusan belum diisi" : mentorUser.getJurusan();
            mentorJurusanLabel.setText(jurusan);
        }
        
        int no = 1;
        for (User mentee : menteesList) {
            String jurusan = (mentee.getJurusan() == null || mentee.getJurusan().trim().isEmpty()) ? "-" : mentee.getJurusan();
            String kontak = (mentee.getKontak() == null || mentee.getKontak().trim().isEmpty()) ? "-" : mentee.getKontak();
            // FIX KONSISTENSI: Hilangkan empty string "" di akhir array yang memicu bug saat kolom Aksi dihapus
            memberTableModel.addRow(new Object[]{no++, mentee.getName(), mentee.getId(), jurusan, mentee.getEmail(), kontak});
        }
    }
}