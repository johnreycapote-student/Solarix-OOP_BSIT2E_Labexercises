import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import util.CSVUtil;
import util.DatabaseConnection;
import util.StockRequestDAO;
import util.ScheduleDAO;
import util.StockArrivalDAO;

public class CompanyAdminFrame extends JFrame {

    // DashboardFrame reference - using fully qualified name to help IDE resolution
    private final DashboardFrame adminDashboard;
    
    // Tables for different modules
    private DefaultTableModel stockRequestModel;
    private DefaultTableModel scheduleModel;
    private DefaultTableModel stockArrivalModel;

    public CompanyAdminFrame(DashboardFrame adminDashboard) {
        this.adminDashboard = adminDashboard;

        setTitle("Company Admin Portal - Renewable Energy Hardware");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        Color primaryOrange = new Color(255, 140, 0);
        Color softOrange = new Color(255, 220, 170);
        Color white = Color.WHITE;

        JPanel root = new JPanel(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryOrange);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Company Admin Portal");
        title.setForeground(white);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Manage stock requests, monitor schedules, and update arrivals");
        subtitle.setForeground(white);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel topLeft = new JPanel();
        topLeft.setOpaque(false);
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));
        topLeft.add(title);
        topLeft.add(Box.createVerticalStrut(4));
        topLeft.add(subtitle);

        // Right side (user + logout)
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(white);
        logoutButton.setForeground(primaryOrange);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel topRight = new JPanel();
        topRight.setOpaque(false);
        topRight.add(logoutButton);

        topBar.add(topLeft, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);

        // Left navigation
        JPanel navPanel = new JPanel();
        navPanel.setBackground(softOrange);
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel menuLabel = new JLabel("Admin Menu");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuLabel.setForeground(new Color(80, 80, 80));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(menuLabel);
        navPanel.add(Box.createVerticalStrut(10));

        JButton requestStockButton = createNavButton("Request Stock");
        JButton monitorScheduleButton = createNavButton("Monitor Schedule");
        JButton updateArrivalsButton = createNavButton("Update Arrivals");

        navPanel.add(requestStockButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(monitorScheduleButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(updateArrivalsButton);

        // Center content card layout
        JPanel contentPanel = new JPanel(new CardLayout());

        JPanel requestStockPanel = createRequestStockPanel();
        JPanel monitorSchedulePanel = createMonitorSchedulePanel();
        JPanel updateArrivalsPanel = createUpdateArrivalsPanel();

        contentPanel.add(requestStockPanel, "REQUEST_STOCK");
        contentPanel.add(monitorSchedulePanel, "MONITOR_SCHEDULE");
        contentPanel.add(updateArrivalsPanel, "UPDATE_ARRIVALS");

        // Button actions
        requestStockButton.addActionListener(e -> showCard(contentPanel, "REQUEST_STOCK"));
        monitorScheduleButton.addActionListener(e -> showCard(contentPanel, "MONITOR_SCHEDULE"));
        updateArrivalsButton.addActionListener(e -> showCard(contentPanel, "UPDATE_ARRIVALS"));

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
    }

    private JButton createNavButton(String text) {
        Color primaryOrange = new Color(255, 140, 0);

        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(primaryOrange);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createLineBorder(primaryOrange));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Request Stock Panel - Request stock from suppliers
     */
    private JPanel createRequestStockPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel heading = new JLabel("Request Stock", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>Request stock items from suppliers for inventory replenishment.</div></html>",
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
                "Request ID", "Item Name", "Category", "Quantity Requested",
                "Supplier", "Request Date", "Status", "Notes"
        };

        stockRequestModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data from database
        try {
            if (DatabaseConnection.testConnection()) {
                StockRequestDAO.loadToTableModel(stockRequestModel);
            }
        } catch (Exception e) {
            System.err.println("Error loading stock requests: " + e.getMessage());
        }

        JTable table = new JTable(stockRequestModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("New Stock Request");
        JButton editButton = new JButton("Edit Request");
        JButton deleteButton = new JButton("Delete Request");

        addButton.setBackground(primaryOrange);
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);

        editButton.setBackground(Color.WHITE);
        editButton.setForeground(primaryOrange);
        editButton.setFocusPainted(false);

        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(180, 50, 50));
        deleteButton.setFocusPainted(false);

        JButton exportButton1 = new JButton("Export");
        JButton importButton1 = new JButton("Import");
        
        exportButton1.setBackground(new Color(0, 120, 200));
        exportButton1.setForeground(Color.BLACK);
        exportButton1.setFocusPainted(false);
        
        importButton1.setBackground(new Color(0, 150, 0));
        importButton1.setForeground(Color.BLACK);
        importButton1.setFocusPainted(false);
        
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton1);
        buttonBar.add(importButton1);
        
        // CSV Export/Import actions
        exportButton1.addActionListener(e -> CSVUtil.exportToCSV(stockRequestModel, "stock_requests.csv", this));
        importButton1.addActionListener(e -> importStockRequestsFromCSV());

        // Actions
        addButton.addActionListener(e -> {
            JTextField itemField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField qtyField = new JTextField();
            JTextField supplierField = new JTextField();
            JTextField dateField = new JTextField();
            // Set placeholder text for date (YYYY-MM-DD format)
            dateField.setToolTipText("Date format: YYYY-MM-DD (e.g., 2025-12-15). Leave empty for today's date.");
            // Set default to today's date
            java.time.LocalDate today = java.time.LocalDate.now();
            dateField.setText(today.toString());
            
            // Status dropdown instead of text field
            String[] statusOptions = {"Pending", "Approved", "Rejected", "Cancelled", "Completed"};
            JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
            statusCombo.setSelectedItem("Pending"); // Default to Pending
            
            JTextArea notesArea = new JTextArea(3, 20);
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            form.add(new JLabel("Item Name:"), gbc);
            gbc.gridy++;
            form.add(new JLabel("Category:"), gbc);
            gbc.gridy++;
            form.add(new JLabel("Quantity:"), gbc);
            gbc.gridy++;
            form.add(new JLabel("Supplier:"), gbc);
            gbc.gridy++;
            JLabel dateLabel = new JLabel("Request Date:");
            dateLabel.setToolTipText("Format: YYYY-MM-DD (e.g., 2025-12-15). Leave empty for today.");
            form.add(dateLabel, gbc);
            gbc.gridy++;
            form.add(new JLabel("Status:"), gbc);
            gbc.gridy++;
            form.add(new JLabel("Notes:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            form.add(itemField, gbc);
            gbc.gridy++;
            form.add(categoryField, gbc);
            gbc.gridy++;
            form.add(qtyField, gbc);
            gbc.gridy++;
            form.add(supplierField, gbc);
            gbc.gridy++;
            form.add(dateField, gbc);
            gbc.gridy++;
            form.add(statusCombo, gbc);
            gbc.gridy++;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            form.add(new JScrollPane(notesArea), gbc);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "New Stock Request",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Validate required fields
                    String itemName = itemField.getText().trim();
                    String category = categoryField.getText().trim();
                    String qtyText = qtyField.getText().trim();
                    String supplier = supplierField.getText().trim();
                    String requestDate = dateField.getText().trim();
                    String status = (String) statusCombo.getSelectedItem(); // Get selected status from combo
                    String notes = notesArea.getText().trim();
                    
                    // Validate required fields
                    if (itemName.isEmpty() || category.isEmpty() || qtyText.isEmpty()) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Please fill in Item Name, Category, and Quantity (required fields).",
                                "Incomplete Data",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    
                    int qty = Integer.parseInt(qtyText);
                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Quantity must be greater than 0.",
                                "Invalid Quantity",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    
                    // Generate unique request ID
                    String reqId = "REQ-" + String.format("%03d", 
                        Math.max(1, stockRequestModel.getRowCount() + 1));
                    
                    // If date is empty, use current date in YYYY-MM-DD format
                    if (requestDate.isEmpty()) {
                        requestDate = java.time.LocalDate.now().toString(); // Format: YYYY-MM-DD
                    }
                    
                    // Default status to "Pending" if empty
                    if (status.isEmpty()) {
                        status = "Pending";
                    }
                    
                    // Save to database
                    if (StockRequestDAO.addRequest(reqId, itemName, category, qty, supplier,
                                                   requestDate, status, notes)) {
                        // Reload from database
                        StockRequestDAO.loadToTableModel(stockRequestModel);
                        
                        // Send to Admin Dashboard (try both instance and static reference)
                        DashboardFrame dashboard = getDashboardInstance();
                        if (dashboard != null) {
                            dashboard.addStockRequestFromCompanyAdmin(
                                    reqId, itemName, category, qty, supplier, requestDate, status, notes
                            );
                        }
                        
                        JOptionPane.showMessageDialog(
                                this,
                                "Stock request submitted successfully!\nRequest ID: " + reqId,
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Failed to save request to database.\nPlease check the console for error details.",
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter a valid quantity (number).",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Error: " + ex.getMessage() + "\nPlease check the console for details.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to edit.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Similar edit dialog (simplified for brevity)
            JOptionPane.showMessageDialog(this, "Edit functionality - select row and modify fields", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this request?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                String requestId = (String) stockRequestModel.getValueAt(row, 0);
                if (StockRequestDAO.updateStatus(requestId, "Cancelled")) {
                    StockRequestDAO.loadToTableModel(stockRequestModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete request from database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Monitor Schedule Panel - View delivery and maintenance schedules
     */
    private JPanel createMonitorSchedulePanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel heading = new JLabel("Monitor Schedule", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>Monitor delivery schedules and maintenance appointments.</div></html>",
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
                "Schedule ID", "Type", "Item/Service", "Supplier/Customer",
                "Scheduled Date", "Time", "Status", "Notes"
        };

        scheduleModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data from database
        try {
            if (DatabaseConnection.testConnection()) {
                ScheduleDAO.loadToTableModel(scheduleModel);
            }
        } catch (Exception e) {
            System.err.println("Error loading schedules: " + e.getMessage());
        }

        JTable table = new JTable(scheduleModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton refreshButton = new JButton("Refresh Schedule");
        refreshButton.setBackground(primaryOrange);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        
        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(refreshButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(scheduleModel, "schedules.csv", this));
        importButton.addActionListener(e -> importSchedulesFromCSV());

        refreshButton.addActionListener(e -> {
            try {
                if (DatabaseConnection.testConnection()) {
                    ScheduleDAO.loadToTableModel(scheduleModel);
                    JOptionPane.showMessageDialog(this, "Schedule refreshed.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Database connection failed.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error refreshing schedule: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Update Stock Arrivals Panel - Update when stock arrives
     */
    private JPanel createUpdateArrivalsPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel heading = new JLabel("Update Stock Arrivals", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>Record stock arrivals and update inventory levels.</div></html>",
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
                "Arrival ID", "Item Name", "Category", "Quantity Received",
                "Supplier", "Arrival Date", "Location", "Status"
        };

        stockArrivalModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data from database
        try {
            if (DatabaseConnection.testConnection()) {
                StockArrivalDAO.loadToTableModel(stockArrivalModel);
            }
        } catch (Exception e) {
            System.err.println("Error loading stock arrivals: " + e.getMessage());
        }

        JTable table = new JTable(stockArrivalModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("Record Arrival");
        JButton editButton = new JButton("Edit Record");
        JButton deleteButton = new JButton("Delete Record");

        addButton.setBackground(primaryOrange);
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);

        editButton.setBackground(Color.WHITE);
        editButton.setForeground(primaryOrange);
        editButton.setFocusPainted(false);

        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(180, 50, 50));
        deleteButton.setFocusPainted(false);

        JButton exportButton2 = new JButton("Export");
        JButton importButton2 = new JButton("Import");
        
        exportButton2.setBackground(new Color(0, 120, 200));
        exportButton2.setForeground(Color.BLACK);
        exportButton2.setFocusPainted(false);
        
        importButton2.setBackground(new Color(0, 150, 0));
        importButton2.setForeground(Color.BLACK);
        importButton2.setFocusPainted(false);
        
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton2);
        buttonBar.add(importButton2);
        
        // CSV Export/Import actions
        exportButton2.addActionListener(e -> CSVUtil.exportToCSV(stockArrivalModel, "stock_arrivals.csv", this));
        importButton2.addActionListener(e -> importStockArrivalsFromCSV());

        // Actions
        addButton.addActionListener(e -> {
            JTextField itemField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField qtyField = new JTextField();
            JTextField supplierField = new JTextField();
            JTextField dateField = new JTextField();
            JTextField locationField = new JTextField();
            
            // Status dropdown instead of text field
            String[] arrivalStatusOptions = {"Received", "Pending", "Processing", "Completed"};
            JComboBox<String> statusCombo = new JComboBox<>(arrivalStatusOptions);
            statusCombo.setSelectedItem("Received"); // Default to Received

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Item Name:"));
            form.add(itemField);
            form.add(new JLabel("Category:"));
            form.add(categoryField);
            form.add(new JLabel("Quantity Received:"));
            form.add(qtyField);
            form.add(new JLabel("Supplier:"));
            form.add(supplierField);
            form.add(new JLabel("Arrival Date:"));
            form.add(dateField);
            form.add(new JLabel("Location:"));
            form.add(locationField);
            form.add(new JLabel("Status:"));
            form.add(statusCombo);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Record Stock Arrival",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    String arrId = "ARR-" + String.format("%03d", stockArrivalModel.getRowCount() + 1);
                    String itemName = itemField.getText().trim();
                    String category = categoryField.getText().trim();
                    String supplier = supplierField.getText().trim();
                    String arrivalDate = dateField.getText().trim();
                    String location = locationField.getText().trim();
                    String status = (String) statusCombo.getSelectedItem(); // Get selected status from combo
                    
                    // Save to database
                    if (StockArrivalDAO.addArrival(arrId, itemName, category, qty, supplier,
                                                   arrivalDate, location, status)) {
                        // Reload from database
                        StockArrivalDAO.loadToTableModel(stockArrivalModel);
                        JOptionPane.showMessageDialog(this, "Stock arrival recorded successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save arrival to database.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter a valid quantity (number).",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a record to edit.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a record to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this record?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                String arrivalId = (String) stockArrivalModel.getValueAt(row, 0);
                if (StockArrivalDAO.deleteArrival(arrivalId)) {
                    StockArrivalDAO.loadToTableModel(stockArrivalModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete arrival from database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Helper method to get DashboardFrame instance.
     * Tries instance reference first, then static method.
     * @return DashboardFrame instance or null if not available
     */
    private DashboardFrame getDashboardInstance() {
        if (adminDashboard != null) {
            return adminDashboard;
        }
        // DashboardFrame.getCurrentInstance() is a static method in the same package
        try {
            return DashboardFrame.getCurrentInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private void showCard(JPanel contentPanel, String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }

    /**
     * Import stock requests from CSV and save to database
     */
    private void importStockRequestsFromCSV() {
        if (CSVUtil.importFromCSV(stockRequestModel, this)) {
            int rowCount = stockRequestModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String requestId = stockRequestModel.getValueAt(i, 0).toString();
                    String itemName = stockRequestModel.getValueAt(i, 1).toString();
                    String category = stockRequestModel.getValueAt(i, 2).toString();
                    int quantityRequested = Integer.parseInt(stockRequestModel.getValueAt(i, 3).toString());
                    String supplier = stockRequestModel.getValueAt(i, 4).toString();
                    String requestDate = stockRequestModel.getValueAt(i, 5).toString();
                    String status = stockRequestModel.getValueAt(i, 6).toString();
                    String notes = stockRequestModel.getValueAt(i, 7).toString();
                    
                    if (!StockRequestDAO.addRequest(requestId, itemName, category, quantityRequested,
                                                   supplier, requestDate, status, notes)) {
                        StockRequestDAO.updateRequest(requestId, status, notes);
                    }
                    savedCount++;
                } catch (Exception e) {
                    skippedCount++;
                    System.err.println("Error saving row " + i + " to database: " + e.getMessage());
                }
            }
            
            StockRequestDAO.loadToTableModel(stockRequestModel);
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Import schedules from CSV and save to database
     */
    private void importSchedulesFromCSV() {
        if (CSVUtil.importFromCSV(scheduleModel, this)) {
            int rowCount = scheduleModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String scheduleId = scheduleModel.getValueAt(i, 0).toString();
                    String type = scheduleModel.getValueAt(i, 1).toString();
                    String itemService = scheduleModel.getValueAt(i, 2).toString();
                    String supplierCustomer = scheduleModel.getValueAt(i, 3).toString();
                    String scheduledDate = scheduleModel.getValueAt(i, 4).toString();
                    String scheduledTime = scheduleModel.getValueAt(i, 5).toString();
                    String status = scheduleModel.getValueAt(i, 6).toString();
                    String notes = scheduleModel.getValueAt(i, 7).toString();
                    
                    if (!ScheduleDAO.addSchedule(scheduleId, type, itemService, supplierCustomer,
                                                 scheduledDate, scheduledTime, status, notes)) {
                        ScheduleDAO.updateSchedule(scheduleId, type, itemService, supplierCustomer,
                                                  scheduledDate, scheduledTime, status, notes);
                    }
                    savedCount++;
                } catch (Exception e) {
                    skippedCount++;
                    System.err.println("Error saving row " + i + " to database: " + e.getMessage());
                }
            }
            
            ScheduleDAO.loadToTableModel(scheduleModel);
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Import stock arrivals from CSV and save to database
     */
    private void importStockArrivalsFromCSV() {
        if (CSVUtil.importFromCSV(stockArrivalModel, this)) {
            int rowCount = stockArrivalModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String arrivalId = stockArrivalModel.getValueAt(i, 0).toString();
                    String itemName = stockArrivalModel.getValueAt(i, 1).toString();
                    String category = stockArrivalModel.getValueAt(i, 2).toString();
                    int quantityReceived = Integer.parseInt(stockArrivalModel.getValueAt(i, 3).toString());
                    String supplier = stockArrivalModel.getValueAt(i, 4).toString();
                    String arrivalDate = stockArrivalModel.getValueAt(i, 5).toString();
                    String location = stockArrivalModel.getValueAt(i, 6).toString();
                    String status = stockArrivalModel.getValueAt(i, 7).toString();
                    
                    if (!StockArrivalDAO.addArrival(arrivalId, itemName, category, quantityReceived,
                                                    supplier, arrivalDate, location, status)) {
                        StockArrivalDAO.updateArrival(arrivalId, itemName, category, quantityReceived,
                                                     supplier, arrivalDate, location, status);
                    }
                    savedCount++;
                } catch (Exception e) {
                    skippedCount++;
                    System.err.println("Error saving row " + i + " to database: " + e.getMessage());
                }
            }
            
            StockArrivalDAO.loadToTableModel(stockArrivalModel);
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
