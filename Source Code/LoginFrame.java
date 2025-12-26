import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import util.DatabaseConnection;
import util.AdminDAO;
import util.CompanyAdminDAO;
import util.TechnicianDAO;
import util.CustomerDAO;

public class LoginFrame extends JFrame {

    private static LoginFrame currentInstance;
    
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Renewable Energy Hardware - System Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setResizable(true);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);

        initComponents();
        
        // Track this instance
        currentInstance = this;
    }
    
    /**
     * Static method to show login frame - ensures only one instance is shown
     * Disposes any existing portal frames and shows a single LoginFrame
     */
    public static void showLogin() {
        SwingUtilities.invokeLater(() -> {
            // If a LoginFrame already exists and is visible, just bring it to front
            if (currentInstance != null && currentInstance.isVisible()) {
                currentInstance.toFront();
                currentInstance.requestFocus();
                return;
            }
            
            // Dispose any existing LoginFrame
            if (currentInstance != null) {
                currentInstance.dispose();
            }
            
            // Create and show new LoginFrame
            LoginFrame login = new LoginFrame();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }

    private void initComponents() {
        // Colors
        Color primaryOrange = new Color(255, 140, 0);
        Color white = Color.WHITE;
        Color darkGray = new Color(60, 60, 60);

        // Root panel with gradient-like background
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(250, 250, 250));

        // Left side panel (brand area) - Enhanced
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(primaryOrange);
        leftPanel.setPreferredSize(new Dimension(400, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));


        JLabel brandTitle = new JLabel("Renewable");
        brandTitle.setForeground(white);
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));

        JLabel brandSubtitle = new JLabel("Energy Hardware");
        brandSubtitle.setForeground(white);
        brandSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 28));

        leftPanel.add(brandTitle);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(brandSubtitle);
        leftPanel.add(Box.createVerticalGlue());

        // Add welcome message at bottom
        JLabel welcomeMsg = new JLabel("<html><div style='text-align: center;'>" +
            "Welcome back!<br>" +
            "Sign in to manage your system</div></html>");
        welcomeMsg.setForeground(new Color(255, 240, 200));
        welcomeMsg.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        welcomeMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(welcomeMsg);

        // Right form panel - Enhanced
        JPanel center = new JPanel();
        center.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
        center.setBackground(white);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Header section
        JPanel headerSection = new JPanel();
        headerSection.setOpaque(false);
        headerSection.setLayout(new BoxLayout(headerSection, BoxLayout.Y_AXIS));
        headerSection.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginLabel = new JLabel("Welcome Back");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setForeground(darkGray);
        loginLabel.setName("loginLabel");

        JLabel loginSubtitle = new JLabel("Sign in to your account");
        loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        loginSubtitle.setForeground(new Color(120, 120, 120));
        loginSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginSubtitle.setName("loginSubtitle");

        headerSection.add(loginLabel);
        headerSection.add(Box.createVerticalStrut(6));
        headerSection.add(loginSubtitle);

        center.add(headerSection);
        center.add(Box.createVerticalStrut(35));

        // Small form panel to keep fields centered under the title
        JPanel formPanel = new JPanel();
        formPanel.setBackground(white);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(new Color(60, 60, 60));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        Dimension fieldSize = new Dimension(350, 42);
        usernameField.setPreferredSize(fieldSize);
        usernameField.setMaximumSize(fieldSize);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        usernameField.setBackground(new Color(250, 250, 250));

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(new Color(60, 60, 60));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        passwordField.setBackground(new Color(250, 250, 250));
        final char defaultEchoChar = passwordField.getEchoChar();

        // Show password checkbox
        JCheckBox showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPasswordCheckBox.setBackground(white);
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });

        // Remember me checkbox
        JCheckBox rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        rememberMeCheckBox.setBackground(white);
        rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Login button
        JButton loginButton = new JButton("Log In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setFocusPainted(false);
        loginButton.setBackground(primaryOrange);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryOrange, 1),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(350, 45));
        loginButton.setMaximumSize(new Dimension(350, 45));
        
        // Hover effect for login button
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(255, 160, 20));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(primaryOrange);
            }
        });

        // Sign Up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setFocusPainted(false);
        signUpButton.setBackground(white);
        signUpButton.setForeground(primaryOrange);
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signUpButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryOrange, 2),
            BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.setPreferredSize(new Dimension(350, 42));
        signUpButton.setMaximumSize(new Dimension(350, 42));
        
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
        divider.setPreferredSize(new Dimension(280, 1));
        divider.setMaximumSize(new Dimension(280, 1));
        divider.setForeground(new Color(220, 220, 220));

        // Secondary info
        JLabel infoLabel = new JLabel("Don't have an account?");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Arrange components inside form panel (centered)
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(showPasswordCheckBox);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(rememberMeCheckBox);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(16));
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
        
        // Add responsive layout listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    updateResponsiveLayout();
                });
            }
        });
        
        // Initial responsive update
        SwingUtilities.invokeLater(() -> updateResponsiveLayout());

        // Load remembered user if any
        loadRememberedUser(rememberMeCheckBox);

        // Actions
        loginButton.addActionListener(e -> attemptLogin(rememberMeCheckBox));
        passwordField.addActionListener(e -> attemptLogin(rememberMeCheckBox));
        signUpButton.addActionListener(e -> showSignUpDialog());
    }

    /**
     * Unified login for all roles.
     * Tries, in order:
     *   1. Admin (username/password)
     *   2. Company Admin (email/password)
     *   3. Technician (email/password)
     *   4. Customer (email/password)
     * and opens the corresponding portal.
     */
    private void attemptLogin(JCheckBox rememberMeCheckBox) {
        String identifier = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username/email and password.",
                    "Incomplete Data",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Handle remember me preference
        if (rememberMeCheckBox != null) {
            if (rememberMeCheckBox.isSelected()) {
                saveRememberedUser(identifier);
            } else {
                clearRememberedUser();
            }
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

        // 1) Try Admin (username)
        if (AdminDAO.authenticate(identifier, password)) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    DashboardFrame dashboard = new DashboardFrame(identifier);
                    dashboard.setLocationRelativeTo(null);
                    dashboard.setVisible(true);
                } catch (Exception e) {
                    System.err.println(e.toString());
                    JOptionPane.showMessageDialog(
                            null,
                            "Error opening admin dashboard: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            return;
        }

        // 2) Try Company Admin (email)
        if (CompanyAdminDAO.authenticate(identifier, password)) {
            dispose();
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
            return;
        }

        // 3) Try Technician (email)
        if (TechnicianDAO.authenticate(identifier, password)) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    TechnicianFrame portal = new TechnicianFrame(identifier);
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
            return;
        }

        // 4) Try Customer (email)
        if (CustomerDAO.authenticate(identifier, password)) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    CustomerPortal portal = new CustomerPortal(identifier);
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
            return;
        }

        // If none matched
        JOptionPane.showMessageDialog(
                this,
                "Invalid username/email or password.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Simple remember-me implementation using a small text file in project root
    private static final String REMEMBER_ME_FILE = "remember_me.txt";

    private void loadRememberedUser(JCheckBox rememberMeCheckBox) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(REMEMBER_ME_FILE);
            if (java.nio.file.Files.exists(path)) {
                String value = new String(java.nio.file.Files.readAllBytes(path)).trim();
                if (!value.isEmpty()) {
                    usernameField.setText(value);
                    if (rememberMeCheckBox != null) {
                        rememberMeCheckBox.setSelected(true);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void saveRememberedUser(String identifier) {
        try {
            java.nio.file.Files.write(
                java.nio.file.Paths.get(REMEMBER_ME_FILE),
                identifier.getBytes()
            );
        } catch (Exception ignored) {
        }
    }

    private void clearRememberedUser() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(REMEMBER_ME_FILE);
            if (java.nio.file.Files.exists(path)) {
                java.nio.file.Files.delete(path);
            }
        } catch (Exception ignored) {
        }
    }

    private void showSignUpDialog() {
        Color primaryOrange = new Color(255, 140, 0);
        Color white = Color.WHITE;
        Color darkGray = new Color(60, 60, 60);
        Color lightGray = new Color(245, 245, 245);

        // Create custom dialog with account type selection (Customer or Technician only)
        JDialog signUpDialog = new JDialog(this, "Sign Up", true);
        signUpDialog.setSize(500, 600);
        signUpDialog.setResizable(false);
        signUpDialog.setLocationRelativeTo(this);
        signUpDialog.setModal(true);
        signUpDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        signUpDialog.setUndecorated(false); // Make sure it has normal window decorations

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(white);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryOrange);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel headerTitle = new JLabel("Create Account");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(white);

        JLabel headerSubtitle = new JLabel("Create Customer or Technician account");
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
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(white);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 8, 12, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Account type selection (only Customer and Technician can sign up)
        String[] accountTypes = {"Customer", "Technician"};
        JComboBox<String> accountTypeCombo = new JComboBox<>(accountTypes);
        accountTypeCombo.setSelectedItem("Customer"); // Default to Customer
        accountTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountTypeCombo.setPreferredSize(new Dimension(280, 38));
        accountTypeCombo.setBackground(lightGray);
        
        JTextField fullNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        
        // Application notes field for Technician (only visible when Technician is selected)
        JTextArea applicationNotesArea = new JTextArea(4, 20);
        applicationNotesArea.setLineWrap(true);
        applicationNotesArea.setWrapStyleWord(true);
        applicationNotesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        applicationNotesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        applicationNotesArea.setBackground(lightGray);
        JScrollPane notesScrollPane = new JScrollPane(applicationNotesArea);
        notesScrollPane.setPreferredSize(new Dimension(280, 80));

        // Style fields
        Dimension fieldSize = new Dimension(280, 38);
        java.util.function.Consumer<JTextField> styleField = (field) -> {
            field.setPreferredSize(fieldSize);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            field.setBackground(lightGray);
        };

        styleField.accept(fullNameField);
        styleField.accept(emailField);
        styleField.accept(contactField);
        styleField.accept(addressField);
        styleField.accept(passField);
        styleField.accept(confirmField);

        // Helper to create styled labels
        java.util.function.Function<String, JLabel> createLabel = (text) -> {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(darkGray);
            return label;
        };
        
        // Store references for dynamic visibility
        JLabel contactLabel = createLabel.apply("Contact Number:");
        JLabel addressLabel = createLabel.apply("Address:");
        
        // Application notes label (for Technician only)
        JLabel applicationNotesLabel = createLabel.apply("Why do you want to apply as Technician?");
        applicationNotesLabel.setVisible(false);
        notesScrollPane.setVisible(false);

        int row = 0;

        // Account Type
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(createLabel.apply("Account Type:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(accountTypeCombo, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(createLabel.apply("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(fullNameField, gbc);

        // Email (login identifier)
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(createLabel.apply("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        // Contact Number (only for Customer)
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(contactLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(contactField, gbc);

        // Address (only for Customer)
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(addressField, gbc);
        
        // Function to update field visibility based on account type
        java.util.function.Consumer<String> updateVisibility = (selectedType) -> {
            boolean isCustomer = "Customer".equals(selectedType);
            boolean isTechnician = "Technician".equals(selectedType);
            contactLabel.setVisible(isCustomer);
            contactField.setVisible(isCustomer);
            addressLabel.setVisible(isCustomer);
            addressField.setVisible(isCustomer);
            applicationNotesLabel.setVisible(isTechnician);
            notesScrollPane.setVisible(isTechnician);
            signUpDialog.pack();
        };
        
        // Update visibility when account type changes
        accountTypeCombo.addActionListener(e -> {
            String selectedType = (String) accountTypeCombo.getSelectedItem();
            updateVisibility.accept(selectedType);
        });
        
        // Set initial visibility (Customer is default, so fields should be visible)
        updateVisibility.accept("Customer");

        // Password
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(createLabel.apply("Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(passField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(createLabel.apply("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(confirmField, gbc);
        
        // Application Notes (only for Technician)
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(applicationNotesLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        formPanel.add(notesScrollPane, gbc);
        gbc.weighty = 0; // Reset weighty

        // Error label (for validation messages)
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        formPanel.add(errorLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(white);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setBackground(white);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> signUpDialog.dispose());

        JButton createButton = new JButton("Create Account");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createButton.setBackground(primaryOrange);
        createButton.setForeground(Color.BLACK);
        createButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryOrange, 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set content pane properly
        signUpDialog.setContentPane(mainPanel);
        signUpDialog.pack(); // Pack to fit content

        // Create account action - supports Customer and Technician
        createButton.addActionListener(e -> {
            String accountType = (String) accountTypeCombo.getSelectedItem();
            boolean isCustomer = "Customer".equals(accountType);
            
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim().toLowerCase();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            errorLabel.setText(" "); // clear previous error

            // Required fields validation
            if (fullName.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                String msg = "Please complete all required fields (Full Name, Email, Password, Confirm Password).";
                errorLabel.setText(msg);
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        msg,
                        "Sign Up Requirements",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // For Technician, application notes are required
            String applicationNotes = applicationNotesArea.getText().trim();
            boolean isTechnician = "Technician".equals(accountType);
            if (isTechnician && applicationNotes.isEmpty()) {
                String msg = "Please explain why you want to apply as a Technician. This is required for admin approval.";
                errorLabel.setText(msg);
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        msg,
                        "Sign Up Requirements",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // For Customer, contact and address are optional but recommended
            // For Technician, contact and address are not needed

            // SIMPLE password rule: at least 6 characters
            if (pass.length() < 6) {
                String msg = "Password must be at least 6 characters long.";
                errorLabel.setText(msg);
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        msg,
                        "Sign Up Requirements",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!pass.equals(confirm)) {
                String msg = "Passwords do not match.";
                errorLabel.setText(msg);
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        msg,
                        "Sign Up Requirements",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Check if identifier (email/username) is already taken in any account table
            boolean exists =
                AdminDAO.usernameExists(email) ||
                CompanyAdminDAO.emailExists(email) ||
                TechnicianDAO.emailExists(email) ||
                CustomerDAO.emailExists(email);

            if (exists) {
                String msg = "This email is already used by another account. Please choose a different email.";
                errorLabel.setText(msg);
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        msg,
                        "Sign Up Requirements",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Confirm registration with name and account type
            String accountTypeText = isCustomer ? "Customer" : "Technician";
            int option = JOptionPane.showConfirmDialog(
                    signUpDialog,
                    "<html>Are you sure you want to register as <b>" + fullName + "</b><br>Account Type: <b>" + accountTypeText + "</b>?</html>",
                    "Confirm Registration",
                    JOptionPane.YES_NO_OPTION
            );
            if (option != JOptionPane.YES_OPTION) {
                return; // user cancelled, keep fields
            }

            // Create account based on selected type
            boolean success = false;
            if (isCustomer) {
                // Create customer account
                success = CustomerDAO.addCustomer(email, pass, fullName, contact, address);
            } else {
                // Create technician account (with Pending status, requires admin approval)
                success = TechnicianDAO.createAccount(email, pass, fullName, applicationNotes);
                
                // Notify admin of new technician application
                if (success) {
                    DashboardFrame dashboard = DashboardFrame.getCurrentInstance();
                    if (dashboard != null) {
                        dashboard.notifyNewTechnicianApplication(email, fullName, applicationNotes);
                    }
                }
            }
            
            if (success) {
                if (isCustomer) {
                    JOptionPane.showMessageDialog(
                            signUpDialog,
                            "Customer account created successfully! You can now log in.",
                            "Sign Up Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            signUpDialog,
                            "Technician application submitted successfully!\n\n" +
                            "Your application is pending admin approval.\n" +
                            "You will be able to log in once your application is approved.",
                            "Application Submitted",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
                signUpDialog.dispose();
            } else {
                String msg = "Failed to create " + accountTypeText.toLowerCase() + " account.\n\n" +
                            "Possible reasons:\n" +
                            "- Email already exists\n" +
                            "- Database connection error\n" +
                            "- Invalid data\n\n" +
                            "Please check your information and try again.";
                errorLabel.setText(msg);
                JOptionPane.showMessageDialog(
                        signUpDialog,
                        msg,
                        "Sign Up Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        signUpDialog.setVisible(true);
    }
    
    /**
     * Update layout for responsive design (mobile/phone-like sizes)
     */
    private void updateResponsiveLayout() {
        int width = getWidth();
        boolean isMobile = width < 600; // Phone-like width
        
        // Find components
        Container root = getContentPane();
        JPanel[] leftPanelRef = new JPanel[1];
        JLabel[] labels = new JLabel[4]; // [brandTitle, brandSubtitle, loginLabel, loginSubtitle]
        
        findLoginComponents(root, leftPanelRef, labels);
        
        JPanel leftPanel = leftPanelRef[0];
        JLabel brandTitle = labels[0];
        JLabel brandSubtitle = labels[1];
        JLabel loginLabel = labels[2];
        JLabel loginSubtitle = labels[3];
        
        // Update brand title font size
        if (brandTitle != null) {
            brandTitle.setFont(new Font("Segoe UI", Font.BOLD, isMobile ? 24 : 38));
        }
        
        // Update brand subtitle font size
        if (brandSubtitle != null) {
            brandSubtitle.setFont(new Font("Segoe UI", Font.BOLD, isMobile ? 18 : 28));
        }
        
        // Update login label font size
        if (loginLabel != null) {
            loginLabel.setFont(new Font("Segoe UI", Font.BOLD, isMobile ? 24 : 32));
        }
        
        // Update login subtitle font size
        if (loginSubtitle != null) {
            loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, isMobile ? 13 : 15));
        }
        
        // Adjust field sizes
        if (usernameField != null && passwordField != null) {
            Dimension fieldSize = isMobile ? new Dimension(280, 38) : new Dimension(350, 42);
            usernameField.setPreferredSize(fieldSize);
            usernameField.setMaximumSize(fieldSize);
            passwordField.setPreferredSize(fieldSize);
            passwordField.setMaximumSize(fieldSize);
        }
        
        // Adjust left panel (hide on mobile)
        if (leftPanel != null) {
            if (isMobile) {
                leftPanel.setPreferredSize(new Dimension(0, 0));
                leftPanel.setVisible(false);
            } else {
                leftPanel.setPreferredSize(new Dimension(400, 0));
                leftPanel.setVisible(true);
            }
            revalidate();
            repaint();
        }
    }
    
    private void findLoginComponents(Container container, JPanel[] leftPanelRef, JLabel[] labels) {
        if (container == null) return;
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                String panelName = panel.getName();
                if (panelName != null && "leftPanel".equals(panelName)) {
                    leftPanelRef[0] = panel;
                }
                findLoginComponents(panel, leftPanelRef, labels);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                String name = label.getName();
                if (name != null) {
                    if ("brandTitle".equals(name)) {
                        labels[0] = label;
                    } else if ("brandSubtitle".equals(name)) {
                        labels[1] = label;
                    } else if ("loginLabel".equals(name)) {
                        labels[2] = label;
                    } else if ("loginSubtitle".equals(name)) {
                        labels[3] = label;
                    }
                }
            } else if (comp instanceof Container) {
                findLoginComponents((Container) comp, leftPanelRef, labels);
            }
        }
    }

}

