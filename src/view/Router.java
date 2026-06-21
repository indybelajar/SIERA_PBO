package view;

import model.User;
import model.Mentor;
import model.Mentee;

import javax.swing.*;

public class Router {
    
    /**
     * Navigates the user to their respective dashboard based on their role
     * and disposes the current frame.
     */
    public static void navigateToDashboard(JFrame currentFrame, User user) {
        if (user == null) {
            showLogin();
            if (currentFrame != null) {
                currentFrame.dispose();
            }
            return;
        }
        
        if (user instanceof Mentor) {
            new MentorDashboard((Mentor) user).setVisible(true);
        } else if (user instanceof Mentee) {
            new MenteeDashboard((Mentee) user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(currentFrame, 
                "Role tidak dikenali!", "Navigasi Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (currentFrame != null) {
            currentFrame.dispose();
        }
    }
    
    /**
     * Opens the login window.
     */
    public static void showLogin() {
        new LoginForm().setVisible(true);
    }
    
    /**
     * Handles the logout confirmation process, opening the login window if confirmed.
     */
    public static void logout(JFrame currentFrame) {
        int confirm = JOptionPane.showConfirmDialog(currentFrame, 
            "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            showLogin();
            if (currentFrame != null) {
                currentFrame.dispose();
            }
        }
    }
}
