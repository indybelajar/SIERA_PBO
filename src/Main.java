import view.LoginForm;
import com.formdev.flatlaf.FlatDarkLaf;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try {
            FlatDarkLaf.setup();
            System.out.println("Using FlatLaf Dark theme");
        } catch (Exception e) {
            System.out.println("FlatLaf error, using default theme");
            e.printStackTrace();
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}