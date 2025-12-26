
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple company admin login / sign-up window.
 * Accounts are stored in file for persistence.
 */
public class CompanyAdminLoginFrame extends JFrame {

    private static final Map<String, String> COMPANY_ADMIN_ACCOUNTS = new HashMap<>();
    private static final String ACCOUNTS_FILE = "company_admin_accounts.txt";

    static {
        loadAccountsFromFile();
        if (COMPANY_ADMIN_ACCOUNTS.isEmpty()) {
            // Default demo account (also saved to file)
            COMPANY_ADMIN_ACCOUNTS.put("admin@company.com", "admin123");
            saveAccountToFile("admin@company.com", "admin123");
        }
    }

    private JTextField emailField;
    private JPasswordField passwordField;

    public CompanyAdminLoginFrame() {
        setTitle("Company Admin Portal - Renewable Energy Hardware");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setResizable(true);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        Color primaryOrange = new Color(255, 140, 0);
        Color white = Color.WHITE;
        Color darkGray = new Color(60, 60, 60);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(white);

        // Header
        JPanel header = new JPanel();
        header.setBackground(primaryOrange);
        header.setPreferredSize(new Dimension(480, 70));

        JLabel titleLabel = new JLabel("Company Admin Portal");
        titleLabel.setForeground(white);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitleLabel = new JLabel("Sign in or create an account");
        subtitleLabel.setForeground(white);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(titleLabel);
        headerText.add(subtitleLabel);

        header.add(headerText);

        // Center form
        JPanel center = new JPanel();
        center.setBackground(white);
        center.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
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

        JLabel loginSubtitle = new JLabel("Sign in to your company admin account");
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
        loginButton.setBackground(primaryOrange);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setFocusPainted(false);
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(primaryOrange);
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signUpButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryOrange, 2),
            BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.setPreferredSize(new Dimension(300, 38));
        signUpButton.setMaximumSize(new Dimension(300, 38));
        
        // Hover effect for sign up button
        signUpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signUpButton.setBackground(new Color(255, 240, 220));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signUpButton.setBackground(Color.WHITE);
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
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(divider);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(infoLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(signUpButton);

        center.add(formPanel);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(255, 220, 170));
        footer.setPreferredSize(new Dimension(400, 25));
        JLabel footerLabel = new JLabel("Company Admin access only - Renewable Energy Hardware");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(80, 80, 80));
        footer.add(footerLabel);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);

        // Actions
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
        signUpButton.addActionListener(e -> showSignUpDialog());
    }

    private void attemptLogin() {
        String email = emailField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword());

        if (COMPANY_ADMIN_ACCOUNTS.containsKey(email)
                && COMPANY_ADMIN_ACCOUNTS.get(email).equals(password)) {

            // Close login window first
            dispose();
            
            // Open company admin portal on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    CompanyAdminFrame portal = new CompanyAdminFrame(null);
                    portal.setLocationRelativeTo(null);
                    portal.setVisible(true);
                } catch (Exception e) {
                    System.err.println(e.toString());
                    JOptionPane.showMessageDialog(
                            null,
                            "Error opening company admin portal: " + e.getMessage(),
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
        JTextField signUpEmailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
        form.add(new JLabel("Email:"));
        form.add(signUpEmailField);
        form.add(new JLabel("Password:"));
        form.add(passField);
        form.add(new JLabel("Confirm Password:"));
        form.add(confirmField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Company Admin Sign Up",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String email = signUpEmailField.getText().trim().toLowerCase();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please complete all fields.",
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

            if (COMPANY_ADMIN_ACCOUNTS.containsKey(email)) {
                JOptionPane.showMessageDialog(
                        this,
                        "An account with this email already exists.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            COMPANY_ADMIN_ACCOUNTS.put(email, pass);
            saveAccountToFile(email, pass);

            JOptionPane.showMessageDialog(
                    this,
                    "Account created successfully. You can now log in.",
                    "Sign Up Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private static void loadAccountsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    COMPANY_ADMIN_ACCOUNTS.put(parts[0], parts[1]);
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

