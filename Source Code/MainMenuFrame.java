
import javax.swing.*;
import java.awt.*;

/**
 * Main menu to choose which portal to access
 */
public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        setTitle("Renewable Energy Hardware - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setResizable(true);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        initComponents();
    }

    private void initComponents() {
        Color primaryOrange = new Color(255, 140, 0);
        Color white = Color.WHITE;
        Color darkGray = new Color(60, 60, 60);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(250, 250, 250));

        // Header
        JPanel header = new JPanel();
        header.setBackground(primaryOrange);
        header.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Renewable Energy Hardware");
        title.setForeground(white);
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enterprise System");
        subtitle.setForeground(new Color(255, 240, 200));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(10));
        header.add(subtitle);

        // Center panel with portal buttons
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        center.setBackground(white);

        JLabel selectLabel = new JLabel("Select Portal");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        selectLabel.setForeground(darkGray);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(selectLabel);
        center.add(Box.createVerticalStrut(30));

        // Admin Portal Button
        JButton adminButton = createPortalButton("Admin Portal", "Manage sales, inventory, suppliers, and maintenance", primaryOrange);
        adminButton.addActionListener(e -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });

        // Company Admin Portal Button
        JButton companyAdminButton = createPortalButton("Company Admin Portal", "Manage stock requests, schedules, and arrivals", new Color(0, 120, 200));
        companyAdminButton.addActionListener(e -> {
            dispose();
            CompanyAdminLoginFrame loginFrame = new CompanyAdminLoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });

        // Technician Portal Button
        JButton technicianButton = createPortalButton("Technician Portal", "View jobs, request parts, and report issues", new Color(0, 150, 0));
        technicianButton.addActionListener(e -> {
            dispose();
            TechnicianLoginFrame loginFrame = new TechnicianLoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });

        // Customer Portal Button
        JButton customerButton = createPortalButton("Customer Portal", "Request maintenance and report equipment issues", new Color(150, 0, 200));
        customerButton.addActionListener(e -> {
            dispose();
            CustomerLoginFrame loginFrame = new CustomerLoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });

        // Make buttons fill the available width
        adminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        companyAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        technicianButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        customerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        center.add(adminButton);
        center.add(Box.createVerticalStrut(15));
        center.add(companyAdminButton);
        center.add(Box.createVerticalStrut(15));
        center.add(technicianButton);
        center.add(Box.createVerticalStrut(15));
        center.add(customerButton);
        center.add(Box.createVerticalGlue());

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JButton createPortalButton(String title, String description, Color color) {
        Color white = Color.WHITE;
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        button.setMinimumSize(new Dimension(600, 90));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 25, 6, 25));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(new Color(60, 60, 60));
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 12, 25));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(titleLabel);
        content.add(descLabel);

        button.add(content, BorderLayout.WEST);

        return button;
    }
}

