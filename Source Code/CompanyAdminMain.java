
import javax.swing.*;

public class CompanyAdminMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            CompanyAdminLoginFrame loginFrame = new CompanyAdminLoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }
}

