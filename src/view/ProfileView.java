package view;

import dao.UserProfileDAO;
import model.User;
import model.UserProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfileView extends JPanel {
    private User user;
    private UserProfileDAO profileDAO;
    private UserProfile userProfile;
    private boolean canEdit;
    private String backPage;
    
    // UI Fields
    private JTextField jurusanField;
    private JTextField fakultasField;
    private JTextField bioField;
    private JTextField linkedinField;
    private JTextField instagramField;
    private JTextField tiktokField;
    private JTextField xField;
    private JTextField youtubeField;
    private JTextField lainnyaField;
    
    private JLabel nameLabel;
    private JLabel roleBadge;
    private JButton editSaveButton;
    private boolean isEditMode = false;
    
    public ProfileView(User user, boolean canEdit, String backPage) {
        this.user = user;
        this.canEdit = canEdit;
        this.backPage = backPage;
        this.profileDAO = new UserProfileDAO();
        initComponents();
        loadProfileData();
        setEditMode(false); // Start in view mode
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // --- 1. TITLE PANEL ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Profil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Back Button (if backPage is supplied)
        if (backPage != null) {
            JButton backBtn = new JButton("← Kembali");
            backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            backBtn.addActionListener(e -> {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof BaseLayout) {
                    ((BaseLayout) window).switchPage(backPage);
                }
            });
            titlePanel.add(backBtn, BorderLayout.EAST);
        }
        
        JSeparator titleSep = new JSeparator();
        titleSep.setForeground(Color.LIGHT_GRAY);
        titlePanel.add(titleSep, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);
        
        // --- 2. CENTER PANEL (Profile Details Scrollable) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Header Section (Photo + Name + Role Badge)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Circular Photo Placeholder (Yellow empty circle)
        JPanel photoCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                g2d.setColor(new Color(254, 240, 138)); // soft yellow
                g2d.fillOval(x, y, size, size);
                
                g2d.setColor(new Color(202, 138, 4)); // dark yellow
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x, y, size, size);
            }
        };
        photoCircle.setPreferredSize(new Dimension(100, 100));
        photoCircle.setMaximumSize(new Dimension(100, 100));
        photoCircle.setOpaque(false);
        photoCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(photoCircle);
        headerPanel.add(Box.createVerticalStrut(10));
        
        // Name Label
        nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(15, 23, 42)); // Slate-900
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(nameLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        
        // Role Badge
        roleBadge = new JLabel("  " + user.getRole().toUpperCase() + "  ");
        roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleBadge.setForeground(Color.DARK_GRAY);
        roleBadge.setOpaque(true);
        roleBadge.setBackground(new Color(241, 245, 249)); // light slate
        roleBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        roleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(roleBadge);
        
        centerPanel.add(headerPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Input Fields Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Academic/Personal Fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Jurusan"), gbc);
        jurusanField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(jurusanField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Fakultas"), gbc);
        fakultasField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(fakultasField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Bio"), gbc);
        bioField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(bioField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("LinkedIn"), gbc);
        linkedinField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(linkedinField, gbc);
        
        // Section Header: Social Media
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        JLabel socialHeader = new JLabel("Social Media");
        socialHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        socialHeader.setForeground(new Color(15, 23, 42));
        formPanel.add(socialHeader, gbc);
        
        // Social Media Fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 6, 15);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Instagram"), gbc);
        instagramField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(instagramField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tiktok"), gbc);
        tiktokField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(tiktokField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("X"), gbc);
        xField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(xField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Youtube"), gbc);
        youtubeField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(youtubeField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Lainnya"), gbc);
        lainnyaField = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(lainnyaField, gbc);
        
        centerPanel.add(formPanel);
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        // --- 3. BOTTOM PANEL (Edit/Save Button) ---
        if (canEdit) {
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            bottomPanel.setOpaque(false);
            
            editSaveButton = new JButton("Edit");
            editSaveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            editSaveButton.setPreferredSize(new Dimension(80, 32));
            editSaveButton.addActionListener(new EditSaveAction());
            bottomPanel.add(editSaveButton);
            
            add(bottomPanel, BorderLayout.SOUTH);
        }
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }
    
    private void loadProfileData() {
        userProfile = profileDAO.getProfileByUserId(user.getId());
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUserId(user.getId());
        }
        
        jurusanField.setText(userProfile.getJurusan());
        fakultasField.setText(userProfile.getFakultas());
        bioField.setText(userProfile.getBio());
        linkedinField.setText(userProfile.getLinkedinUrl());
        instagramField.setText(userProfile.getInstagramHandle());
        tiktokField.setText(userProfile.getTiktokHandle());
        xField.setText(userProfile.getXHandle());
        youtubeField.setText(userProfile.getYoutubeUrl());
        lainnyaField.setText(userProfile.getOtherSocial());
    }
    
    private void setEditMode(boolean edit) {
        this.isEditMode = edit;
        
        jurusanField.setEditable(edit);
        fakultasField.setEditable(edit);
        bioField.setEditable(edit);
        linkedinField.setEditable(edit);
        instagramField.setEditable(edit);
        tiktokField.setEditable(edit);
        xField.setEditable(edit);
        youtubeField.setEditable(edit);
        lainnyaField.setEditable(edit);
        
        if (edit) {
            if (editSaveButton != null) editSaveButton.setText("Save");
            setFieldsBackground(Color.WHITE);
        } else {
            if (editSaveButton != null) editSaveButton.setText("Edit");
            setFieldsBackground(new Color(241, 245, 249)); // light slate gray
        }
    }
    
    private void setFieldsBackground(Color color) {
        jurusanField.setBackground(color);
        fakultasField.setBackground(color);
        bioField.setBackground(color);
        linkedinField.setBackground(color);
        instagramField.setBackground(color);
        tiktokField.setBackground(color);
        xField.setBackground(color);
        youtubeField.setBackground(color);
        lainnyaField.setBackground(color);
    }
    
    private class EditSaveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isEditMode) {
                setEditMode(true);
            } else {
                userProfile.setJurusan(jurusanField.getText().trim());
                userProfile.setFakultas(fakultasField.getText().trim());
                userProfile.setBio(bioField.getText().trim());
                userProfile.setLinkedinUrl(linkedinField.getText().trim());
                userProfile.setInstagramHandle(instagramField.getText().trim());
                userProfile.setTiktokHandle(tiktokField.getText().trim());
                userProfile.setXHandle(xField.getText().trim());
                userProfile.setYoutubeUrl(youtubeField.getText().trim());
                userProfile.setOtherSocial(lainnyaField.getText().trim());
                
                if (profileDAO.saveOrUpdateProfile(userProfile)) {
                    JOptionPane.showMessageDialog(ProfileView.this, 
                        "Profil berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    setEditMode(false);
                    loadProfileData();
                } else {
                    JOptionPane.showMessageDialog(ProfileView.this, 
                        "Gagal menyimpan profil.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
