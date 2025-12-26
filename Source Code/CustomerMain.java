
import javax.swing.SwingUtilities;
import util.DatabaseConnection;

/**
 * Direct entry point for Customer Portal
 */
public class CustomerMain {
    public static void main(String[] args) {
        // Initialize database connection
        try {
            System.out.println("Initializing database connection...");
            DatabaseConnection.testConnection();
            System.out.println("Database connection established successfully!");
        } catch (Exception e) {
            System.err.println("Warning: Database initialization failed: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            CustomerLoginFrame loginFrame = new CustomerLoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }
}

