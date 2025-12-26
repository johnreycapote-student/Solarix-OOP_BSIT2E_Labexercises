import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import util.DatabaseConnection;
import util.CustomerDAO;
import util.CustomerIssueDAO;
import util.MaintenanceDAO;
import java.time.LocalDate;

/**
 * Customer Portal - Main GUI for customers
 * Separate file for customer interface
 * Features:
 * - Request Maintenance Services
 * - Report Equipment Issues
 * - View Maintenance History
 * - Track Issue Status
 */
public class CustomerPortal extends JFrame {

    // Static reference for notifications (map email to instance)
    public static java.util.Map<String, CustomerPortal> customerInstances = new java.util.HashMap<>();
    
    private final String customerEmail;
    private final String customerName;

    private DefaultTableModel maintenanceRequestModel;
    private DefaultTableModel issuesModel;

    public CustomerPortal(String customerEmail) {
        this.customerEmail = customerEmail;
        this.customerName = CustomerDAO.getCustomerName(customerEmail);
        customerInstances.put(customerEmail, this); // Track instance

        setTitle("Customer Portal - Renewable Energy Hardware");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        initComponents();
    }
    
    /**
     * Get CustomerPortal instance by email
     */
    public static CustomerPortal getInstanceByEmail(String email) {
        return customerInstances.get(email);
    }
    
    /**
     * Get customer name
     */
    public String getCustomerName() {
        return customerName;
    }
    
    /**
     * Notify customer of maintenance status update (called from TechnicianFrame)
     */
    public void notifyMaintenanceStatusUpdate(String ticketId, String newStatus) {
        if (maintenanceRequestModel != null) {
            // Reload maintenance requests from database
            refreshMaintenanceRequests();
        }
    }

    private void initComponents() {
        Color primaryOrange = new Color(255, 140, 0);
        Color softOrange = new Color(255, 220, 170);
        Color white = Color.WHITE;

        JPanel root = new JPanel(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryOrange);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Large Welcome message (responsive font size)
        String displayName = customerName != null && !customerName.isEmpty() ? customerName : customerEmail;
        JLabel welcomeLabel = new JLabel("Welcome, " + displayName + "!");
        welcomeLabel.setForeground(white);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setName("welcomeLabel"); // For responsive updates

        JLabel title = new JLabel("Customer Portal");
        title.setForeground(white);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setName("titleLabel");

        JLabel subtitle = new JLabel("Request maintenance services and report equipment issues");
        subtitle.setForeground(white);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setName("subtitleLabel");

        JPanel topLeft = new JPanel();
        topLeft.setOpaque(false);
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));
        topLeft.add(welcomeLabel);
        topLeft.add(Box.createVerticalStrut(8));
        topLeft.add(title);
        topLeft.add(Box.createVerticalStrut(4));
        topLeft.add(subtitle);

        JLabel userLabel = new JLabel("Logged in as: " + (customerName != null ? customerName : customerEmail));
        userLabel.setForeground(white);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(white);
        logoutButton.setForeground(primaryOrange);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel topRight = new JPanel();
        topRight.setOpaque(false);
        topRight.setLayout(new BoxLayout(topRight, BoxLayout.Y_AXIS));
        topRight.add(userLabel);
        topRight.add(Box.createVerticalStrut(5));
        topRight.add(logoutButton);

        topBar.add(topLeft, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);

        // Left navigation (responsive - can be hidden on small screens)
        JPanel navPanel = new JPanel();
        navPanel.setBackground(softOrange);
        navPanel.setPreferredSize(new Dimension(220, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        navPanel.setName("navPanel");

        JLabel menuLabel = new JLabel("Customer Menu");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuLabel.setForeground(new Color(80, 80, 80));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(menuLabel);
        navPanel.add(Box.createVerticalStrut(10));

        JButton maintenanceButton = createNavButton("Request Maintenance");
        JButton issuesButton = createNavButton("Report Issues");

        navPanel.add(maintenanceButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(issuesButton);
        navPanel.add(Box.createVerticalGlue());

        // Content panel with CardLayout
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(white);

        JPanel maintenancePanel = createMaintenanceRequestPanel();
        JPanel issuesPanel = createIssuesPanel();

        contentPanel.add(maintenancePanel, "MAINTENANCE");
        contentPanel.add(issuesPanel, "ISSUES");

        // Button actions
        maintenanceButton.addActionListener(e -> showCard(contentPanel, "MAINTENANCE"));
        issuesButton.addActionListener(e -> showCard(contentPanel, "ISSUES"));

        // Logout action: ask for confirmation, then return to LoginFrame
        logoutButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Do you really want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                dispose();
                LoginFrame.showLogin();
            }
        });

        root.add(topBar, BorderLayout.NORTH);
        root.add(navPanel, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        
        // Add responsive layout listener - triggers on every resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update immediately when resized
                SwingUtilities.invokeLater(() -> {
                    updateResponsiveLayout();
                });
            }
        });
        
        // Initial responsive update
        SwingUtilities.invokeLater(() -> updateResponsiveLayout());
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(60, 60, 60));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showCard(JPanel contentPanel, String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
        
        // Refresh data when switching panels
        if ("MAINTENANCE".equals(name)) {
            refreshMaintenanceRequests();
        } else if ("ISSUES".equals(name)) {
            refreshIssues();
        }
    }

    /**
     * Maintenance Request Panel
     */
    private JPanel createMaintenanceRequestPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Request Maintenance", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>Request maintenance services for your equipment. Our technicians will schedule a visit.</div></html>",
                SwingConstants.CENTER
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(new Color(80, 80, 80));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(heading);
        top.add(Box.createVerticalStrut(6));
        top.add(description);
        top.add(Box.createVerticalStrut(15));

        // Table
        String[] columnNames = {
                "Ticket ID", "Equipment", "Service Type", "Schedule Date", "Technician", "Status", "Notes"
        };

        maintenanceRequestModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load customer's maintenance requests
        refreshMaintenanceRequests();

        JTable table = new JTable(maintenanceRequestModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton requestButton = new JButton("Request Maintenance");
        
        requestButton.setBackground(primaryOrange);
        requestButton.setForeground(Color.BLACK);
        requestButton.setFocusPainted(false);
        
        buttonBar.add(requestButton);

        // Actions
        requestButton.addActionListener(e -> openMaintenanceRequestDialog());

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Issues Panel
     */
    private JPanel createIssuesPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Report Equipment Issues", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>Report problems with your equipment. Our technicians will be notified and will fix the issue.</div></html>",
                SwingConstants.CENTER
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(new Color(80, 80, 80));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(heading);
        top.add(Box.createVerticalStrut(6));
        top.add(description);
        top.add(Box.createVerticalStrut(15));

        // Table
        String[] columnNames = {
                "Issue ID", "Equipment", "Description", "Severity", "Reported Date", "Status", "Assigned Technician"
        };

        issuesModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load customer's issues
        refreshIssues();

        JTable table = new JTable(issuesModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton reportButton = new JButton("Report New Issue");
        
        reportButton.setBackground(primaryOrange);
        reportButton.setForeground(Color.BLACK);
        reportButton.setFocusPainted(false);
        
        buttonBar.add(reportButton);

        // Actions
        reportButton.addActionListener(e -> openIssueReportDialog());

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    private void openMaintenanceRequestDialog() {
        JTextField equipmentField = new JTextField();
        JTextField serviceTypeField = new JTextField("Installation");
        JTextField scheduleDateField = new JTextField(LocalDate.now().plusDays(7).toString());
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
        form.add(new JLabel("Equipment:"));
        form.add(equipmentField);
        form.add(new JLabel("Service Type:"));
        form.add(serviceTypeField);
        form.add(new JLabel("Preferred Date (YYYY-MM-DD):"));
        form.add(scheduleDateField);
        form.add(new JLabel("Notes:"));
        form.add(new JScrollPane(notesArea));

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Request Maintenance",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String equipment = equipmentField.getText().trim();
            String serviceType = serviceTypeField.getText().trim();
            String scheduleDate = scheduleDateField.getText().trim();
            String notes = notesArea.getText().trim();

            if (equipment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter equipment name.", "Incomplete Data",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Generate ticket ID
            String ticketId = "MT-" + System.currentTimeMillis() % 100000;
            String customerName = CustomerDAO.getCustomerName(customerEmail);
            String contactNo = CustomerDAO.getCustomerContact(customerEmail);
            String address = CustomerDAO.getCustomerAddress(customerEmail);

            if (customerName == null) customerName = customerEmail;
            if (contactNo == null) contactNo = "";
            if (address == null) address = "";

            // Save to database
            if (MaintenanceDAO.addMaintenance(ticketId, customerName, contactNo, address,
                    equipment, serviceType, scheduleDate, "", "Pending", notes)) {
                refreshMaintenanceRequests();
                
                // Notify Admin Dashboard in real-time
                DashboardFrame dashboard = DashboardFrame.getCurrentInstance();
                if (dashboard != null) {
                    dashboard.notifyNewMaintenanceRequest(ticketId, customerName, contactNo, address,
                            equipment, serviceType, scheduleDate, notes);
                }
                
                JOptionPane.showMessageDialog(this, "Maintenance request submitted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit maintenance request.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openIssueReportDialog() {
        JTextField equipmentField = new JTextField();
        JComboBox<String> severityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High", "Critical"});
        severityCombo.setSelectedIndex(1);
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        form.add(new JLabel("Equipment Name:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Severity:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        form.add(equipmentField, gbc);
        gbc.gridy++;
        form.add(severityCombo, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        form.add(new JScrollPane(descriptionArea), gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Report Equipment Issue",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String equipment = equipmentField.getText().trim();
            String severity = (String) severityCombo.getSelectedItem();
            String description = descriptionArea.getText().trim();

            if (equipment.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Data",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Generate issue ID
            String issueId = "ISS-" + System.currentTimeMillis() % 100000;
            String reportedDate = LocalDate.now().toString();

            // Save to database
            if (CustomerIssueDAO.addIssue(issueId, customerEmail, equipment, description, severity, reportedDate)) {
                refreshIssues();
                JOptionPane.showMessageDialog(this, "Issue reported successfully! A technician will be assigned soon.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to report issue.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshMaintenanceRequests() {
        if (maintenanceRequestModel != null) {
            maintenanceRequestModel.setRowCount(0);
            try {
                if (DatabaseConnection.testConnection()) {
                    // Load only this customer's maintenance requests
                    String customerName = CustomerDAO.getCustomerName(customerEmail);
                    if (customerName != null) {
                        // Load all and filter by customer name
                        DefaultTableModel allMaintenance = new DefaultTableModel(new String[]{
                            "Ticket ID", "Customer Name", "Contact No.", "Site Address", "Equipment",
                            "Service Type", "Schedule Date", "Technician", "Status", "Notes"
                        }, 0);
                        MaintenanceDAO.loadToTableModel(allMaintenance);
                        
                        // Filter for this customer
                        for (int i = 0; i < allMaintenance.getRowCount(); i++) {
                            if (customerName.equals(allMaintenance.getValueAt(i, 1).toString())) {
                                Object[] row = {
                                    allMaintenance.getValueAt(i, 0), // Ticket ID
                                    allMaintenance.getValueAt(i, 4), // Equipment
                                    allMaintenance.getValueAt(i, 5), // Service Type
                                    allMaintenance.getValueAt(i, 6), // Schedule Date
                                    allMaintenance.getValueAt(i, 7), // Technician
                                    allMaintenance.getValueAt(i, 8), // Status
                                    allMaintenance.getValueAt(i, 9)  // Notes
                                };
                                maintenanceRequestModel.addRow(row);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading maintenance requests: " + e.getMessage());
            }
        }
    }

    private void refreshIssues() {
        if (issuesModel != null) {
            issuesModel.setRowCount(0);
            try {
                if (DatabaseConnection.testConnection()) {
                    CustomerIssueDAO.loadToTableModel(issuesModel, customerEmail);
                }
            } catch (Exception e) {
                System.err.println("Error loading issues: " + e.getMessage());
            }
        }
    }
    
    /**
     * Update layout for responsive design (mobile/phone-like sizes)
     */
    private void updateResponsiveLayout() {
        int width = getWidth();
        boolean isMobile = width < 600; // Phone-like width
        
        // Find components by traversing the component tree
        Container root = getContentPane();
        JPanel[] navPanelRef = new JPanel[1]; // Use array to pass by reference
        JLabel[] labels = new JLabel[3]; // [welcome, title, subtitle]
        
        // Find navPanel and labels
        findComponents(root, navPanelRef, labels);
        
        JPanel navPanel = navPanelRef[0];
        
        // Update welcome label font size
        if (labels[0] != null) {
            labels[0].setFont(new Font("Segoe UI", Font.BOLD, isMobile ? 20 : 32));
        }
        
        // Update title font size
        if (labels[1] != null) {
            labels[1].setFont(new Font("Segoe UI", Font.BOLD, isMobile ? 16 : 20));
        }
        
        // Hide/show subtitle on mobile
        if (labels[2] != null) {
            labels[2].setVisible(!isMobile);
        }
        
        // Adjust navigation panel
        if (navPanel != null) {
            if (isMobile) {
                navPanel.setPreferredSize(new Dimension(0, 0));
                navPanel.setVisible(false);
            } else {
                navPanel.setPreferredSize(new Dimension(220, 0));
                navPanel.setVisible(true);
            }
            revalidate();
            repaint();
        }
    }
    
    private void findComponents(Container container, JPanel[] navPanelRef, JLabel[] labels) {
        if (container == null) return;
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                String panelName = panel.getName();
                if (panelName != null && "navPanel".equals(panelName)) {
                    navPanelRef[0] = panel;
                }
                // Always recurse into panels to find nested components
                findComponents(panel, navPanelRef, labels);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                String name = label.getName();
                if (name != null) {
                    if ("welcomeLabel".equals(name)) {
                        labels[0] = label;
                    } else if ("titleLabel".equals(name)) {
                        labels[1] = label;
                    } else if ("subtitleLabel".equals(name)) {
                        labels[2] = label;
                    }
                }
            } else if (comp instanceof Container) {
                // Also check Box and other containers
                findComponents((Container) comp, navPanelRef, labels);
            }
        }
    }
}

