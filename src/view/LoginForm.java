package view;

import dao.UserDAO;
import model.User;
import model.Mentor;
import model.Mentee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private CardLayout formCardLayout;
    private JPanel cardsPanel;
    
    // Login Fields
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    // Register Fields
    private JTextField regNameField;
    private JTextField regEmailField; // just the username
    private JPasswordField regPasswordField;
    private JComboBox<String> regRoleComboBox;
    private JTextField regGroupField;
    private JButton registerButton;
    
    private UserDAO userDAO;
    
    public LoginForm() {
        userDAO = new UserDAO();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("PKKMB Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel dengan background gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 118, 210), 
                                                      0, h, new Color(13, 71, 161));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        // Cards Panel (Login & Register)
        formCardLayout = new CardLayout();
        cardsPanel = new JPanel(formCardLayout);
        cardsPanel.setOpaque(false);
        
        // Create Login Card
        JPanel loginCard = createLoginCard();
        // Create Register Card
        JPanel registerCard = createRegisterCard();
        
        cardsPanel.add(loginCard, "login");
        cardsPanel.add(registerCard, "register");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(cardsPanel, gbc);
        
        add(mainPanel);
        
        // Start on login page
        formCardLayout.show(cardsPanel, "login");
    }
    
    private JPanel createLoginCard() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        formPanel.setPreferredSize(new Dimension(380, 400));
        
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Logo/Icon
        JLabel iconLabel = new JLabel("📚");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.gridwidth = 2;
        fgbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(iconLabel, fgbc);
        
        // Title
        JLabel titleLabel = new JLabel("PKKMB Information System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fgbc.gridy = 1;
        formPanel.add(titleLabel, fgbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fgbc.gridy = 2;
        formPanel.add(subtitleLabel, fgbc);
        
        // Spacer
        fgbc.gridy = 3;
        formPanel.add(Box.createVerticalStrut(10), fgbc);
        
        // Email Label & Field
        fgbc.gridwidth = 1;
        fgbc.gridx = 0;
        fgbc.gridy = 4;
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(emailLabel, fgbc);
        
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        fgbc.gridx = 1;
        formPanel.add(emailField, fgbc);
        
        // Password Label & Field
        fgbc.gridx = 0;
        fgbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(passwordLabel, fgbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        fgbc.gridx = 1;
        formPanel.add(passwordField, fgbc);
        
        // Login Button
        loginButton = new JButton("🔑 Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setBackground(new Color(25, 118, 210));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new LoginAction());
        
        fgbc.gridx = 0;
        fgbc.gridy = 6;
        fgbc.gridwidth = 2;
        fgbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(loginButton, fgbc);
        
        // Switch to register button
        JButton toRegisterBtn = new JButton("Don't have an account? Register here");
        toRegisterBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toRegisterBtn.setForeground(new Color(25, 118, 210));
        toRegisterBtn.setBorderPainted(false);
        toRegisterBtn.setContentAreaFilled(false);
        toRegisterBtn.setFocusPainted(false);
        toRegisterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toRegisterBtn.addActionListener(e -> formCardLayout.show(cardsPanel, "register"));
        
        fgbc.gridy = 7;
        fgbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(toRegisterBtn, fgbc);
        
        return formPanel;
    }
    
    private JPanel createRegisterCard() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        formPanel.setPreferredSize(new Dimension(380, 420));
        
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(4, 4, 4, 4);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Register Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.gridwidth = 3;
        formPanel.add(titleLabel, fgbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Create a new PKKMB account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fgbc.gridy = 1;
        formPanel.add(subtitleLabel, fgbc);
        
        // Spacer
        fgbc.gridy = 2;
        formPanel.add(Box.createVerticalStrut(8), fgbc);
        
        // Full Name Label & Field
        fgbc.gridwidth = 1;
        fgbc.gridy = 3;
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        formPanel.add(nameLabel, fgbc);
        
        regNameField = new JTextField(20);
        regNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 6, 5, 6)
        ));
        fgbc.gridx = 1;
        fgbc.gridwidth = 2;
        formPanel.add(regNameField, fgbc);
        
        // Email Label & Input (with automatic domain)
        fgbc.gridx = 0;
        fgbc.gridy = 4;
        fgbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        formPanel.add(emailLabel, fgbc);
        
        regEmailField = new JTextField(10);
        regEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 6, 5, 6)
        ));
        fgbc.gridx = 1;
        formPanel.add(regEmailField, fgbc);
        
        JLabel domainLabel = new JLabel("@mahasiswa.upnvj.ac.id");
        domainLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        domainLabel.setForeground(Color.DARK_GRAY);
        fgbc.gridx = 2;
        formPanel.add(domainLabel, fgbc);
        
        // Password Label & Field
        fgbc.gridx = 0;
        fgbc.gridy = 5;
        fgbc.gridwidth = 1;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        formPanel.add(passwordLabel, fgbc);
        
        regPasswordField = new JPasswordField(20);
        regPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 6, 5, 6)
        ));
        fgbc.gridx = 1;
        fgbc.gridwidth = 2;
        formPanel.add(regPasswordField, fgbc);
        
        // Role Label & Selection
        fgbc.gridx = 0;
        fgbc.gridy = 6;
        fgbc.gridwidth = 1;
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        formPanel.add(roleLabel, fgbc);
        
        String[] roles = {"mentee", "mentor"};
        regRoleComboBox = new JComboBox<>(roles);
        regRoleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fgbc.gridx = 1;
        fgbc.gridwidth = 2;
        formPanel.add(regRoleComboBox, fgbc);
        
        // Kelompok Label & Field
        fgbc.gridx = 0;
        fgbc.gridy = 7;
        fgbc.gridwidth = 1;
        JLabel kelompokLabel = new JLabel("Kelompok");
        kelompokLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        formPanel.add(kelompokLabel, fgbc);
        
        regGroupField = new JTextField(20);
        regGroupField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regGroupField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 6, 5, 6)
        ));
        fgbc.gridx = 1;
        fgbc.gridwidth = 2;
        formPanel.add(regGroupField, fgbc);
        
        // Register Button
        registerButton = new JButton("📝 Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setBackground(new Color(46, 125, 50)); // Green color for success/action
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(new RegisterAction());
        
        fgbc.gridx = 0;
        fgbc.gridy = 8;
        fgbc.gridwidth = 3;
        fgbc.insets = new Insets(12, 4, 4, 4);
        formPanel.add(registerButton, fgbc);
        
        // Switch back to login button
        JButton toLoginBtn = new JButton("Already have an account? Login");
        toLoginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toLoginBtn.setForeground(new Color(25, 118, 210));
        toLoginBtn.setBorderPainted(false);
        toLoginBtn.setContentAreaFilled(false);
        toLoginBtn.setFocusPainted(false);
        toLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toLoginBtn.addActionListener(e -> formCardLayout.show(cardsPanel, "login"));
        
        fgbc.gridy = 9;
        fgbc.insets = new Insets(2, 4, 4, 4);
        formPanel.add(toLoginBtn, fgbc);
        
        return formPanel;
    }
    
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user = userDAO.login(email, password);
            
            if (user != null) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Login successful! Welcome, " + user.getName(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                Router.navigateToDashboard(LoginForm.this, user);
            } else {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = regNameField.getText().trim();
            String emailUsername = regEmailField.getText().trim();
            String password = new String(regPasswordField.getPassword()).trim();
            String role = (String) regRoleComboBox.getSelectedItem();
            String groupName = regGroupField.getText().trim();
            
            if (name.isEmpty() || emailUsername.isEmpty() || password.isEmpty() || groupName.isEmpty()) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (emailUsername.contains("@") || emailUsername.contains(" ")) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Email username tidak boleh mengandung spasi atau karakter '@'!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String email = emailUsername + "@mahasiswa.upnvj.ac.id";
            
            if (userDAO.isEmailRegistered(email)) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Email ini sudah terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user;
            if ("mentor".equals(role)) {
                user = new Mentor(0, name, email, password);
            } else {
                user = new Mentee(0, name, email, password);
            }
            
            if (userDAO.registerUser(user, groupName)) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Registration successful! Please login.", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear fields
                regNameField.setText("");
                regEmailField.setText("");
                regPasswordField.setText("");
                regGroupField.setText("");
                
                // Switch back to login card
                formCardLayout.show(cardsPanel, "login");
                emailField.setText(email);
            } else {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Registration failed. Please try again.", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}