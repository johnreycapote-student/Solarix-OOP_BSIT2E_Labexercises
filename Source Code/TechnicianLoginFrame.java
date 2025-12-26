
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple login window for technicians with sign up functionality.
 * Accounts are stored in file for persistence.
 */
public class TechnicianLoginFrame extends JFrame {

    private static final Map<String, String> TECHNICIAN_ACCOUNTS = new HashMap<>();
    private static final String ACCOUNTS_FILE = "technician_accounts.txt";

    static {
        loadAccountsFromFile();
        if (TECHNICIAN_ACCOUNTS.isEmpty()) {
            // Default demo account (also saved to file)
            TECHNICIAN_ACCOUNTS.put("tech@demo.com", "demo123");
            saveAccountToFile("tech@demo.com", "demo123");
        }
    }

    private JTextField emailField;
    private JPasswordField passwordField;

    public TechnicianLoginFrame() {
        setTitle("Technician Portal - Renewable Energy Hardware");
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

        // Left side panel (brand area) - Enhanced
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(primary);
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JLabel brandTitle = new JLabel("Technician");
        brandTitle.setForeground(white);
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel brandSubtitle = new JLabel("Portal");
        brandSubtitle.setForeground(white);
        brandSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel brandTag = new JLabel("Field Technician Access");
        brandTag.setForeground(new Color(255, 240, 200));
        brandTag.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        leftPanel.add(brandTitle);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(brandSubtitle);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(brandTag);
        leftPanel.add(Box.createVerticalGlue());

        // Add welcome message at bottom
        JLabel welcomeMsg = new JLabel("<html><div style='text-align: center;'>" +
            "Welcome back!<br>" +
            "Sign in to access your portal</div></html>");
        welcomeMsg.setForeground(new Color(255, 240, 200));
        welcomeMsg.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        welcomeMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(welcomeMsg);

        // Right form panel - Enhanced
        JPanel center = new JPanel();
        center.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        center.setBackground(white);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Header section
        JPanel headerSection = new JPanel();
        headerSection.setOpaque(false);
        headerSection.setLayout(new BoxLayout(headerSection, BoxLayout.Y_AXIS));
        headerSection.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginLabel = new JLabel("Welcome Back");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setForeground(darkGray);

        JLabel loginSubtitle = new JLabel("Sign in to your technician account");
        loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginSubtitle.setForeground(new Color(120, 120, 120));
        loginSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerSection.add(loginLabel);
        headerSection.add(Box.createVerticalStrut(6));
        headerSection.add(loginSubtitle);

        center.add(headerSection);
        center.add(Box.createVerticalStrut(35));

        JPanel formPanel = new JPanel();
        formPanel.setBackground(white);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(darkGray);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new JTextField();
        Dimension fieldSize = new Dimension(300, 38);
        emailField.setPreferredSize(fieldSize);
        emailField.setMaximumSize(fieldSize);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        emailField.setBackground(new Color(250, 250, 250));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(darkGray);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        passwordField.setBackground(new Color(250, 250, 250));

        JButton loginButton = new JButton("Log In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setFocusPainted(false);
        loginButton.setBackground(primary);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(260, 40));
        loginButton.setMaximumSize(new Dimension(260, 40));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Sign Up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setFocusPainted(false);
        signUpButton.setBackground(white);
        signUpButton.setForeground(primary);
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signUpButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary, 2),
            BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.setPreferredSize(new Dimension(260, 38));
        signUpButton.setMaximumSize(new Dimension(260, 38));
        
        // Hover effect for sign up button
        signUpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signUpButton.setBackground(new Color(255, 240, 220));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signUpButton.setBackground(white);
            }
        });

        // Divider line
        JSeparator divider = new JSeparator();
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        divider.setPreferredSize(new Dimension(300, 1));
        divider.setMaximumSize(new Dimension(300, 1));
        divider.setForeground(new Color(220, 220, 220));

        // Secondary info
        JLabel infoLabel = new JLabel("Don't have an account?");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        // Put form on the right side directly on root
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

        if (TECHNICIAN_ACCOUNTS.containsKey(email)
                && TECHNICIAN_ACCOUNTS.get(email).equals(password)) {
            // Close login window first
            dispose();
            
            // Open technician portal on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    TechnicianFrame portal = new TechnicianFrame(email);
                    portal.setLocationRelativeTo(null);
                    portal.setVisible(true);
                } catch (Exception e) {
                    System.err.println(e.toString());
                    JOptionPane.showMessageDialog(
                            null,
                            "Error opening technician portal: " + e.getMessage(),
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
        Color darkGray = new Color(60, 60, 60);

        // Create custom dialog
        JDialog signUpDialog = new JDialog(this, "Technician Sign Up", true);
        signUpDialog.setSize(450, 420);
        signUpDialog.setResizable(false);
        signUpDialog.setLocationRelativeTo(this);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(white);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryOrange);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel headerTitle = new JLabel("Create Technician Account");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(white);

        JLabel headerSubtitle = new JLabel("Fill in the details to create your account");
        headerSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        headerSubtitle.setForeground(new Color(255, 240, 200));

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.add(headerTitle);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(headerSubtitle);

        headerPanel.add(headerText, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel dialogFormPanel = new JPanel(new GridBagLayout());
        dialogFormPanel.setBackground(white);
        dialogFormPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 8, 12, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField signUpEmailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        // Style fields
        Dimension fieldSize = new Dimension(280, 38);
        java.util.function.Consumer<JTextField> styleField = (field) -> {
            field.setPreferredSize(fieldSize);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            field.setBackground(new Color(245, 245, 245));
        };

        styleField.accept(signUpEmailField);
        styleField.accept(passField);
        styleField.accept(confirmField);

        // Helper to create styled labels
        java.util.function.Function<String, JLabel> createLabel = (text) -> {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(darkGray);
            return label;
        };

        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dialogFormPanel.add(createLabel.apply("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dialogFormPanel.add(signUpEmailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dialogFormPanel.add(createLabel.apply("Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dialogFormPanel.add(passField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dialogFormPanel.add(createLabel.apply("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dialogFormPanel.add(confirmField, gbc);

        mainPanel.add(dialogFormPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(white);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setBackground(white);
        cancelButton.setForeground(darkGray);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> signUpDialog.dispose());

        JButton createButton = new JButton("Create Account");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createButton.setBackground(primaryOrange);
        createButton.setForeground(white);
        createButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryOrange, 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        signUpDialog.add(mainPanel);

        // Create account action
        createButton.addActionListener(e -> {
            String email = signUpEmailField.getText().trim().toLowerCase();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        "Please complete all fields.",
                        "Incomplete Data",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        "Passwords do not match.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (TECHNICIAN_ACCOUNTS.containsKey(email)) {
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        "An account with this email already exists.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            TECHNICIAN_ACCOUNTS.put(email, pass);
            saveAccountToFile(email, pass);

            JOptionPane.showMessageDialog(
                    signUpDialog,
                    "Account created successfully. You can now log in.",
                    "Sign Up Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
            
            signUpDialog.dispose();
        });

        signUpDialog.setVisible(true);
    }

    private static void loadAccountsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    TECHNICIAN_ACCOUNTS.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, that's okay
        }
    }

    private static void saveAccountToFile(String email, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            writer.write(email + ":" + password);
            writer.newLine();
        } catch (IOException e) {
            // Couldn't save, but continue anyway
        }
    }
}

