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
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;
    
    public LoginForm() {
        userDAO = new UserDAO();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("PKKMB Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 380);
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Panel putih untuk form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setPreferredSize(new Dimension(350, 280));
        
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Logo/Icon
        JLabel iconLabel = new JLabel("📚");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.gridwidth = 2;
        fgbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(iconLabel, fgbc);
        
        // Title
        JLabel titleLabel = new JLabel("PKKMB Information System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 118, 210));
        fgbc.gridy = 1;
        formPanel.add(titleLabel, fgbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        fgbc.gridy = 2;
        formPanel.add(subtitleLabel, fgbc);
        
        // Spacer
        fgbc.gridy = 3;
        formPanel.add(Box.createVerticalStrut(10), fgbc);
        
        // Email
        fgbc.gridwidth = 1;
        fgbc.gridx = 0;
        fgbc.gridy = 4;
        JLabel emailLabel = new JLabel("📧 Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(emailLabel, fgbc);
        
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        emailField.setPreferredSize(new Dimension(250, 35));
        fgbc.gridx = 1;
        formPanel.add(emailField, fgbc);
        
        // Password
        fgbc.gridx = 0;
        fgbc.gridy = 5;
        JLabel passwordLabel = new JLabel("🔒 Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(passwordLabel, fgbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setPreferredSize(new Dimension(250, 35));
        fgbc.gridx = 1;
        formPanel.add(passwordField, fgbc);
        
        // Login Button
        loginButton = new JButton("🔑 Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(25, 118, 210));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setPreferredSize(new Dimension(250, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new LoginAction());
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(21, 101, 192));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(25, 118, 210));
            }
        });
        
        fgbc.gridx = 0;
        fgbc.gridy = 6;
        fgbc.gridwidth = 2;
        fgbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, fgbc);
        
        // Register hint
        JLabel hintLabel = new JLabel("Don't have an account? Contact admin");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintLabel.setForeground(Color.GRAY);
        fgbc.gridy = 7;
        formPanel.add(hintLabel, fgbc);
        
        // Tambahkan form panel ke main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(formPanel, gbc);
        
        add(mainPanel);
    }
    
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
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
                
                if (user instanceof Mentor) {
                    new MentorDashboard((Mentor) user).setVisible(true);
                } else if (user instanceof Mentee) {
                    new MenteeDashboard((Mentee) user).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}