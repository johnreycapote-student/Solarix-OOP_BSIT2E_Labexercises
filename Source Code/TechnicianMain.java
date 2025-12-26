
import javax.swing.*;

/**
 * Entry point for the Technician desktop portal.
 * Similar concept to CompanyAdminMain but focused on field technicians.
 */
public class TechnicianMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            TechnicianLoginFrame loginFrame = new TechnicianLoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }
}

