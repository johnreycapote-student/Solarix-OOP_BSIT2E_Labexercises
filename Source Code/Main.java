
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        // Initialize database connection early to ensure tables and sample data are created
        try {
            System.out.println("Initializing database connection...");
            DatabaseConnection.testConnection();
            System.out.println("Database connection established successfully!");
        } catch (Exception e) {
            System.err.println("Warning: Database initialization failed: " + e.getMessage());
            System.err.println(e.toString());
        }
        
        // Make UI look a bit nicer on each OS
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        // Show intro interface first, then go to LoginFrame
        SwingUtilities.invokeLater(() -> {
            IntroInterface intro = new IntroInterface();
            intro.setLocationRelativeTo(null);
            intro.setVisible(true);
        });
    }
}

