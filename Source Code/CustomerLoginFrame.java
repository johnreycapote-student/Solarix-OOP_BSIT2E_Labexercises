
import javax.swing.*;
import java.awt.*;
import util.DatabaseConnection;
import util.CustomerDAO;

/**
 * Customer login window with sign up functionality.
 * Accounts are stored in database.
 */
public class CustomerLoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public CustomerLoginFrame() {
        setTitle("Customer Portal - Renewable Energy Hardware");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setResizable(true);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        Color primary = new Color(255, 140, 0);
        Color white = Color.WHITE;
        Color darkGray = new Color(60, 60, 60);

        // Root panel with light background
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(250, 250, 250));

        // Left side panel (brand area)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(primary);
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JLabel brandTitle = new JLabel("Customer");
        brandTitle.setForeground(white);
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel brandSubtitle = new JLabel("Portal");
        brandSubtitle.setForeground(white);
        brandSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel brandTag = new JLabel("Request Maintenance & Report Issues");
        brandTag.setForeground(new Color(255, 240, 200));
        brandTag.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        leftPanel.add(brandTitle);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(brandSubtitle);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(brandTag);
        leftPanel.add(Box.createVerticalGlue());

        JLabel welcomeMsg = new JLabel("<html><div style='text-align: center;'>" +
            "Welcome!<br>" +
            "Sign in to request services</div></html>");
        welcomeMsg.setForeground(new Color(255, 240, 200));
        welcomeMsg.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        welcomeMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(welcomeMsg);

        // Right form panel
        JPanel center = new JPanel();
        center.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        center.setBackground(white);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel headerSection = new JPanel();
        headerSection.setOpaque(false);
        headerSection.setLayout(new BoxLayout(headerSection, BoxLayout.Y_AXIS));
        headerSection.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginLabel = new JLabel("Welcome Back");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setForeground(darkGray);

        JLabel loginSubtitle = new JLabel("Sign in to your customer account");
        loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginSubtitle.setForeground(Color.BLACK);

        headerSection.add(loginLabel);
        headerSection.add(Box.createVerticalStrut(6));
        headerSection.add(loginSubtitle);
        center.add(headerSection);
        center.add(Box.createVerticalStrut(30));

        // Form fields
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailLabel.setForeground(darkGray);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLabel.setForeground(darkGray);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(primary);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JSeparator divider = new JSeparator();
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        divider.setMaximumSize(new Dimension(300, 1));

        JLabel infoLabel = new JLabel("Don't have an account?");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(120, 120, 120));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signUpButton.setContentAreaFilled(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setForeground(primary);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(divider);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(infoLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(signUpButton);

        center.add(formPanel);

        root.add(leftPanel, BorderLayout.WEST);
        root.add(center, BorderLayout.CENTER);

        setContentPane(root);

        // Actions
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
        signUpButton.addActionListener(e -> showSignUpDialog());
    }

    private void attemptLogin() {
        String email = emailField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both email and password.",
                    "Incomplete Data",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Test database connection first
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot connect to database. Please ensure:\n" +
                    "1. H2 JDBC driver is in classpath\n" +
                    "2. Check error messages for details",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Authenticate using database
        if (CustomerDAO.authenticate(email, password)) {
            // Close login window first
            dispose();
            
            // Open customer portal on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    CustomerPortal portal = new CustomerPortal(email);
                    portal.setLocationRelativeTo(null);
                    portal.setVisible(true);
                } catch (Exception e) {
                    System.err.println(e.toString());
                    JOptionPane.showMessageDialog(
                            null,
                            "Error opening customer portal: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid email or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showSignUpDialog() {
        Color primaryOrange = new Color(255, 140, 0);
        Color white = Color.WHITE;

        JDialog signUpDialog = new JDialog(this, "Customer Sign Up", true);
        signUpDialog.setSize(450, 500);
        signUpDialog.setResizable(false);
        signUpDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(white);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryOrange);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel headerTitle = new JLabel("Create Customer Account");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(white);

        headerPanel.add(headerTitle, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        formPanel.setBackground(white);

        JTextField fullNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField contactField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        formPanel.add(createFormRow("Full Name:", fullNameField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFormRow("Email:", emailField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFormRow("Contact Number:", contactField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFormRow("Address:", addressField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFormRow("Password:", passwordField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFormRow("Confirm Password:", confirmPasswordField));

        JButton createButton = new JButton("Create Account");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createButton.setBackground(primaryOrange);
        createButton.setForeground(white);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        signUpDialog.add(mainPanel);

        createButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim().toLowerCase();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());

            if (fullName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please fill in all required fields (Full Name, Email, Password).",
                        "Incomplete Data",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Passwords do not match.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (CustomerDAO.emailExists(email)) {
                JOptionPane.showMessageDialog(
                        this,
                        "An account with this email already exists.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (CustomerDAO.addCustomer(email, pass, fullName, contact, address)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Account created successfully! You can now log in.",
                        "Sign Up Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
                signUpDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to create account. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        signUpDialog.setVisible(true);
    }

    private JPanel createFormRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jLabel.setPreferredSize(new Dimension(120, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        row.add(jLabel, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }
}

