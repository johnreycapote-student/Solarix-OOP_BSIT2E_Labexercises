import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import util.CSVUtil;
import util.DatabaseConnection;
import util.SalesOrderDAO;
import util.InventoryDAO;
import util.SupplierDAO;
import util.MaintenanceDAO;
import util.StockRequestDAO;
import util.TechnicianJobDAO;
import util.TechnicianDAO;

public class DashboardFrame extends JFrame {

    // Static reference for CompanyAdminFrame to access
    private static DashboardFrame currentInstance;

    private final String currentUser;
    private final String signInTime;

    // Inventory summary labels + model
    private JLabel invTotalItemsLabel;
    private JLabel invLowStockLabel;
    private DefaultTableModel inventoryTableModel;

    // Sales & Orders summary labels + model
    private JLabel salesTotalOrdersLabel;
    private JLabel salesPendingLabel;
    private JLabel salesPaidLabel;
    private JLabel salesTotalAmountLabel;
    private DefaultTableModel salesTableModel;

    // Suppliers summary labels + model
    private JLabel supTotalSuppliersLabel;
    private JLabel supActiveSuppliersLabel;
    private JLabel supInactiveSuppliersLabel;
    private DefaultTableModel suppliersTableModel;

    // Maintenance summary labels + model
    private JLabel maintTotalTicketsLabel;
    private JLabel maintScheduledLabel;
    private JLabel maintCompletedLabel;
    private DefaultTableModel maintenanceTableModel;

    // Stock Requests summary labels + model
    private JLabel stockReqTotalLabel;
    private JLabel stockReqPendingLabel;
    private JLabel stockReqApprovedLabel;
    private DefaultTableModel stockRequestsTableModel;

    // Technician Applications model
    private DefaultTableModel technicianApplicationsTableModel;

    // Reports summary labels (for Reports tab)
    private JLabel repTitleLabel;
    private JLabel repSummaryLabel;

    public DashboardFrame(String currentUser) {
        this.currentUser = currentUser;
        this.signInTime = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        );
        currentInstance = this; // Set static reference

        setTitle("Renewable Energy Hardware - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        initComponents();
    }

    /**
     * Get the current DashboardFrame instance (for CompanyAdminFrame to use)
     */
    public static DashboardFrame getCurrentInstance() {
        return currentInstance;
    }

    private void initComponents() {
        Color primaryOrange = new Color(255, 140, 0);
        Color softOrange = new Color(255, 220, 170);
        Color white = Color.WHITE;

        // Root layout
        JPanel root = new JPanel(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryOrange);
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel title = new JLabel("Renewable Energy Hardware Enterprise System");
        title.setForeground(white);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Sign in status with timestamp
        JPanel signInPanel = new JPanel();
        signInPanel.setOpaque(false);
        signInPanel.setLayout(new BoxLayout(signInPanel, BoxLayout.Y_AXIS));
        
        JLabel signInLabel = new JLabel("✓ Signed in as: " + currentUser);
        signInLabel.setForeground(white);
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel timeLabel = new JLabel("Signed in: " + signInTime);
        timeLabel.setForeground(new Color(255, 240, 200));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        signInPanel.add(signInLabel);
        signInPanel.add(timeLabel);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setBackground(white);
        logoutButton.setForeground(primaryOrange);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.setOpaque(false);
        rightTop.add(signInPanel, BorderLayout.CENTER);
        rightTop.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        rightTop.add(logoutButton, BorderLayout.EAST);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

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

        // Left navigation
        JPanel navPanel = new JPanel();
        navPanel.setBackground(softOrange);
        navPanel.setPreferredSize(new Dimension(190, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel menuLabel = new JLabel("Main Menu");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuLabel.setForeground(new Color(80, 80, 80));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(menuLabel);
        navPanel.add(Box.createVerticalStrut(10));

        JButton inventoryButton = createNavButton("Inventory Management");
        JButton ordersButton = createNavButton("Sales & Orders");
        JButton suppliersButton = createNavButton("Suppliers");
        JButton stockRequestsButton = createNavButton("Stock Requests");
        JButton reportsButton = createNavButton("Reports");
        JButton maintenanceButton = createNavButton("Maintenance");
        JButton technicianAppsButton = createNavButton("Technician Applications");

        navPanel.add(inventoryButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(ordersButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(suppliersButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(stockRequestsButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(reportsButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(maintenanceButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(technicianAppsButton);

        // Center content card layout (declared early for menu bar access)
        JPanel contentPanel = new JPanel(new CardLayout());

        JPanel homePanel = createHomePanel();
        JPanel inventoryPanel = createInventoryPanel();
        JPanel ordersPanel = createSalesOrdersPanel();
        JPanel suppliersPanel = createSuppliersPanel();
        JPanel stockRequestsPanel = createStockRequestsPanel();
        JPanel reportsPanel = createReportsPanel();
        JPanel maintenancePanel = createMaintenancePanel();
        JPanel technicianAppsPanel = createTechnicianApplicationsPanel();

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(inventoryPanel, "INVENTORY");
        contentPanel.add(ordersPanel, "ORDERS");
        contentPanel.add(suppliersPanel, "SUPPLIERS");
        contentPanel.add(stockRequestsPanel, "STOCK_REQUESTS");
        contentPanel.add(reportsPanel, "REPORTS");
        contentPanel.add(maintenancePanel, "MAINTENANCE");
        contentPanel.add(technicianAppsPanel, "TECHNICIAN_APPS");

        // Button actions – switch cards
        inventoryButton.addActionListener(e -> showCard(contentPanel, "INVENTORY"));
        ordersButton.addActionListener(e -> showCard(contentPanel, "ORDERS"));
        suppliersButton.addActionListener(e -> showCard(contentPanel, "SUPPLIERS"));
        stockRequestsButton.addActionListener(e -> showCard(contentPanel, "STOCK_REQUESTS"));
        reportsButton.addActionListener(e -> showCard(contentPanel, "REPORTS"));
        maintenanceButton.addActionListener(e -> showCard(contentPanel, "MAINTENANCE"));
        technicianAppsButton.addActionListener(e -> showCard(contentPanel, "TECHNICIAN_APPS"));

        logoutButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Do you really want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                // Return to login
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setLocationRelativeTo(this);
                loginFrame.setVisible(true);
                dispose();
            }
        });

        // Assemble root
        root.add(topBar, BorderLayout.NORTH);
        root.add(navPanel, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        
        // Initial data refresh for home panel
        SwingUtilities.invokeLater(() -> {
            updateHomePanelStats();
        });
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

    // Home panel stat card labels (for dynamic updates)
    private JLabel homeTotalItemsLabel;
    private JLabel homeActiveOrdersLabel;
    private JLabel homeKeySuppliersLabel;

    private JPanel createHomePanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel();
        panel.setBackground(white);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Renewable Energy Hardware Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html>Overview of your enterprise system for solar panels, inverters, batteries, " +
                        "and other renewable energy equipment.</html>"
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(new Color(70, 70, 70));

        JPanel topText = new JPanel();
        topText.setOpaque(false);
        topText.setLayout(new BoxLayout(topText, BoxLayout.Y_AXIS));
        topText.add(heading);
        topText.add(Box.createVerticalStrut(8));
        topText.add(description);

        // Simple stats cards - now with dynamic labels
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setOpaque(false);

        // Create stat cards with label references
        homeTotalItemsLabel = new JLabel("0");
        homeTotalItemsLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        homeTotalItemsLabel.setForeground(primaryOrange);
        JPanel totalItemsCard = createStatCardWithLabel("Total Items", homeTotalItemsLabel, "In-stock renewable hardware items");
        
        homeActiveOrdersLabel = new JLabel("0");
        homeActiveOrdersLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        homeActiveOrdersLabel.setForeground(primaryOrange);
        JPanel activeOrdersCard = createStatCardWithLabel("Active Orders", homeActiveOrdersLabel, "Customer orders in progress");
        
        homeKeySuppliersLabel = new JLabel("0");
        homeKeySuppliersLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        homeKeySuppliersLabel.setForeground(primaryOrange);
        JPanel keySuppliersCard = createStatCardWithLabel("Key Suppliers", homeKeySuppliersLabel, "Main renewable hardware suppliers");

        cardsPanel.add(totalItemsCard);
        cardsPanel.add(activeOrdersCard);
        cardsPanel.add(keySuppliersCard);

        panel.add(topText, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);

        // Load real data from database
        updateHomePanelStats();

        return panel;
    }

    @SuppressWarnings("unused")
    private JPanel createStatCard(String title, String value, String subtitle) {
        Color primaryOrange = new Color(255, 140, 0);
        Color white = Color.WHITE;

        JPanel card = new JPanel();
        card.setBackground(white);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(80, 80, 80));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(primaryOrange);
        valueLabel.setName("VALUE_LABEL"); // Add name for easy identification

        JLabel subtitleLabel = new JLabel("<html><small>" + subtitle + "</small></html>");
        subtitleLabel.setForeground(new Color(120, 120, 120));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);

        return card;
    }

    /**
     * Create stat card with a provided label (for dynamic updates)
     */
    private JPanel createStatCardWithLabel(String title, JLabel valueLabel, String subtitle) {
        Color white = Color.WHITE;

        JPanel card = new JPanel();
        card.setBackground(white);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(80, 80, 80));

        JLabel subtitleLabel = new JLabel("<html><small>" + subtitle + "</small></html>");
        subtitleLabel.setForeground(new Color(120, 120, 120));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);

        return card;
    }

    /**
     * Update home panel statistics from database
     */
    private void updateHomePanelStats() {
        try {
            if (DatabaseConnection.testConnection()) {
                // Load total items from inventory
                int totalItems = InventoryDAO.getTotalItems();
                if (homeTotalItemsLabel != null) {
                    homeTotalItemsLabel.setText(String.valueOf(totalItems));
                }

                // Load active orders (pending orders)
                int activeOrders = SalesOrderDAO.getPendingOrders();
                if (homeActiveOrdersLabel != null) {
                    homeActiveOrdersLabel.setText(String.valueOf(activeOrders));
                }

                // Load active suppliers
                int keySuppliers = SupplierDAO.getActiveSuppliers();
                if (homeKeySuppliersLabel != null) {
                    homeKeySuppliersLabel.setText(String.valueOf(keySuppliers));
                }
            } else {
                // Fallback values if database not available
                if (homeTotalItemsLabel != null) homeTotalItemsLabel.setText("0");
                if (homeActiveOrdersLabel != null) homeActiveOrdersLabel.setText("0");
                if (homeKeySuppliersLabel != null) homeKeySuppliersLabel.setText("0");
            }
        } catch (Exception e) {
            // Log error silently
        }
    }

    /**
     * Sales & Orders panel with JTable and Add/Edit/Delete + summary.
     */
    private JPanel createSalesOrdersPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top title / description (shared style)
        JPanel top = createSectionHeader(
                "Sales & Orders",
                "Record customer orders, quotations, and delivery status for renewable energy hardware."
        );

        // Summary badges
        JPanel summaryPanel = new JPanel();
        summaryPanel.setOpaque(false);
        summaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        salesTotalOrdersLabel = new JLabel("Total Orders: 0");
        salesTotalOrdersLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        salesTotalOrdersLabel.setForeground(new Color(60, 60, 60));

        salesPendingLabel = new JLabel("Pending: 0");
        salesPendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        salesPendingLabel.setForeground(new Color(180, 120, 0));

        salesPaidLabel = new JLabel("Paid: 0");
        salesPaidLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        salesPaidLabel.setForeground(new Color(0, 130, 70));

        salesTotalAmountLabel = new JLabel("Total Sales: 0.00");
        salesTotalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        salesTotalAmountLabel.setForeground(primaryOrange);

        summaryPanel.add(salesTotalOrdersLabel);
        summaryPanel.add(salesPendingLabel);
        summaryPanel.add(salesPaidLabel);
        summaryPanel.add(salesTotalAmountLabel);

        top.add(summaryPanel);

        // Table model (shared across app)
        if (salesTableModel == null) {
            String[] columnNames = {
                    "Order ID", "Customer Name", "Order Date",
                    "Item / Package", "Quantity", "Unit Price",
                    "Total Amount", "Status", "Payment Method"
            };

            salesTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Load data from database
            try {
                if (DatabaseConnection.testConnection()) {
                    SalesOrderDAO.loadToTableModel(salesTableModel);
                }
            } catch (Exception e) {
                // Silently ignore
            }
        }

        JTable table = new JTable(salesTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Status-based coloring
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLACK);

                Object statusObj = tbl.getValueAt(row, 7);
                String status = statusObj == null ? "" : statusObj.toString().toLowerCase();

                if (isSelected) {
                    c.setBackground(new Color(255, 220, 170));
                } else if (status.contains("paid")) {
                    c.setBackground(new Color(225, 245, 230)); // light green
                } else if (status.contains("pending")) {
                    c.setBackground(new Color(255, 245, 220)); // light yellow
                } else if (status.contains("cancel")) {
                    c.setBackground(new Color(255, 230, 230)); // light red
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("Add Order");
        JButton editButton = new JButton("Edit Order");
        JButton deleteButton = new JButton("Delete Order");

        addButton.setBackground(primaryOrange);
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);

        editButton.setBackground(Color.WHITE);
        editButton.setForeground(primaryOrange);
        editButton.setFocusPainted(false);

        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(180, 50, 50));
        deleteButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(salesTableModel, "sales_orders.csv", this));
        importButton.addActionListener(e -> importSalesOrdersFromCSV());

        // Actions
        addButton.addActionListener(e -> {
            JTextField orderIdField = new JTextField();
            JTextField customerField = new JTextField();
            JTextField dateField = new JTextField();
            JTextField itemField = new JTextField();
            JTextField qtyField = new JTextField();
            JTextField unitPriceField = new JTextField();
            JTextField totalField = new JTextField();
            JTextField statusField = new JTextField("Pending");
            JTextField paymentField = new JTextField();

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Order ID:"));
            form.add(orderIdField);
            form.add(new JLabel("Customer Name:"));
            form.add(customerField);
            form.add(new JLabel("Order Date (YYYY-MM-DD):"));
            form.add(dateField);
            form.add(new JLabel("Item / Package:"));
            form.add(itemField);
            form.add(new JLabel("Quantity:"));
            form.add(qtyField);
            form.add(new JLabel("Unit Price:"));
            form.add(unitPriceField);
            form.add(new JLabel("Total Amount:"));
            form.add(totalField);
            form.add(new JLabel("Status:"));
            form.add(statusField);
            form.add(new JLabel("Payment Method:"));
            form.add(paymentField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Add Order",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                    double total;

                    String totalText = totalField.getText().trim();
                    if (totalText.isEmpty()) {
                        total = qty * unitPrice;
                    } else {
                        total = Double.parseDouble(totalText);
                    }

                    // Save to database
                    if (SalesOrderDAO.addOrder(
                            orderIdField.getText().trim(),
                            customerField.getText().trim(),
                            dateField.getText().trim(),
                            itemField.getText().trim(),
                            qty,
                            unitPrice,
                            total,
                            statusField.getText().trim(),
                            paymentField.getText().trim())) {
                        
                        // Reload from database
                        SalesOrderDAO.loadToTableModel(salesTableModel);
                        updateSalesSummary();
                        JOptionPane.showMessageDialog(this, "Order added successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add order to database.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter valid numeric values for Quantity, Unit Price, and Total Amount.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to edit.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField orderIdField = new JTextField(salesTableModel.getValueAt(row, 0).toString());
            JTextField customerField = new JTextField(salesTableModel.getValueAt(row, 1).toString());
            JTextField dateField = new JTextField(salesTableModel.getValueAt(row, 2).toString());
            JTextField itemField = new JTextField(salesTableModel.getValueAt(row, 3).toString());
            JTextField qtyField = new JTextField(salesTableModel.getValueAt(row, 4).toString());
            JTextField unitPriceField = new JTextField(salesTableModel.getValueAt(row, 5).toString());
            JTextField totalField = new JTextField(salesTableModel.getValueAt(row, 6).toString());
            JTextField statusField = new JTextField(salesTableModel.getValueAt(row, 7).toString());
            JTextField paymentField = new JTextField(salesTableModel.getValueAt(row, 8).toString());

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Order ID:"));
            form.add(orderIdField);
            form.add(new JLabel("Customer Name:"));
            form.add(customerField);
            form.add(new JLabel("Order Date (YYYY-MM-DD):"));
            form.add(dateField);
            form.add(new JLabel("Item / Package:"));
            form.add(itemField);
            form.add(new JLabel("Quantity:"));
            form.add(qtyField);
            form.add(new JLabel("Unit Price:"));
            form.add(unitPriceField);
            form.add(new JLabel("Total Amount:"));
            form.add(totalField);
            form.add(new JLabel("Status:"));
            form.add(statusField);
            form.add(new JLabel("Payment Method:"));
            form.add(paymentField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Order",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Update in database - full update available
                    String orderId = orderIdField.getText().trim();
                    String customerName = customerField.getText().trim();
                    String orderDate = dateField.getText().trim();
                    String itemPackage = itemField.getText().trim();
                    int quantity = Integer.parseInt(qtyField.getText().trim());
                    double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                    double totalAmount = Double.parseDouble(totalField.getText().trim());
                    String status = statusField.getText().trim();
                    String paymentMethod = paymentField.getText().trim();
                    
                    if (SalesOrderDAO.updateOrder(orderId, customerName, orderDate, itemPackage, 
                                                  quantity, unitPrice, totalAmount, status, paymentMethod)) {
                        // Reload from database
                        SalesOrderDAO.loadToTableModel(salesTableModel);
                        updateSalesSummary();
                        JOptionPane.showMessageDialog(this, "Order updated successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update order in database.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter valid numeric values for Quantity, Unit Price, and Total Amount.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected order?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                String orderId = salesTableModel.getValueAt(row, 0).toString();
                
                // Delete from database
                if (SalesOrderDAO.deleteOrder(orderId)) {
                    // Reload from database
                    SalesOrderDAO.loadToTableModel(salesTableModel);
                    updateSalesSummary();
                    JOptionPane.showMessageDialog(this, "Order deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete order from database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout
        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        updateSalesSummary();

        return panel;
    }

    private void updateSalesSummary() {
        if (salesTableModel == null) {
            return;
        }

        // Get statistics from database
        try {
            if (DatabaseConnection.testConnection()) {
                int total = SalesOrderDAO.getTotalOrders();
                int pending = SalesOrderDAO.getPendingOrders();
                double totalAmount = SalesOrderDAO.getTotalAmount();

                if (salesTotalOrdersLabel != null) {
                    salesTotalOrdersLabel.setText("Total Orders: " + total);
                }
                if (salesPendingLabel != null) {
                    salesPendingLabel.setText("Pending: " + pending);
                }
                if (salesPaidLabel != null) {
                    salesPaidLabel.setText("Paid: " + (total - pending));
                }
                if (salesTotalAmountLabel != null) {
                    salesTotalAmountLabel.setText(String.format("Total Sales: ₱%.2f", totalAmount));
                }
            } else {
                // Fallback to table-based calculation
                int rows = salesTableModel.getRowCount();
                int pending = 0;
                int paid = 0;
                double totalSales = 0.0;

                for (int i = 0; i < rows; i++) {
                    Object statusObj = salesTableModel.getValueAt(i, 7);
                    String status = statusObj == null ? "" : statusObj.toString().toLowerCase();
                    if (status.contains("pending")) {
                        pending++;
                    } else if (status.contains("paid")) {
                        paid++;
                    }

                    try {
                        Object totalObj = salesTableModel.getValueAt(i, 6);
                        if (totalObj != null) {
                            totalSales += Double.parseDouble(totalObj.toString());
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (salesTotalOrdersLabel != null) {
                    salesTotalOrdersLabel.setText("Total Orders: " + rows);
                }
                if (salesPendingLabel != null) {
                    salesPendingLabel.setText("Pending: " + pending);
                }
                if (salesPaidLabel != null) {
                    salesPaidLabel.setText("Paid: " + paid);
                }
                if (salesTotalAmountLabel != null) {
                    salesTotalAmountLabel.setText(String.format("Total Sales: ₱%.2f", totalSales));
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating sales summary: " + e.getMessage());
        }
    }

    /**
     * Inventory Management panel with JTable and Add/Edit/Delete.
     */
    private JPanel createInventoryPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top title / description (shared style)
        JPanel top = createSectionHeader(
                "Inventory Management",
                "Manage solar panels, inverters, batteries, and other renewable energy hardware items."
        );

        // Summary badges
        JPanel summaryPanel = new JPanel();
        summaryPanel.setOpaque(false);
        summaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        invTotalItemsLabel = new JLabel("Total Items: 0");
        invTotalItemsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        invTotalItemsLabel.setForeground(new Color(60, 60, 60));

        invLowStockLabel = new JLabel("Low Stock: 0");
        invLowStockLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        invLowStockLabel.setForeground(new Color(180, 80, 0));

        summaryPanel.add(invTotalItemsLabel);
        summaryPanel.add(invLowStockLabel);

        top.add(summaryPanel);

        // Table model (shared across app)
        if (inventoryTableModel == null) {
            String[] columnNames = {
                    "Item ID", "Item Name", "Category", "Brand/Model",
                    "Quantity", "Unit Price", "Reorder Level", "Location", "Status"
            };

            inventoryTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // direct edit via dialogs only
                    return false;
                }
            };

            // Load data from database
            try {
                if (DatabaseConnection.testConnection()) {
                    InventoryDAO.loadToTableModel(inventoryTableModel);
                }
            } catch (Exception e) {
                // Silently ignore
            }
        }

        JTable table = new JTable(inventoryTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Low stock highlighting renderer
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLACK);

                int qty = 0;
                int reorder = 0;
                try {
                    Object qtyObj = tbl.getValueAt(row, 4);
                    Object reorderObj = tbl.getValueAt(row, 6);
                    if (qtyObj != null) qty = Integer.parseInt(qtyObj.toString());
                    if (reorderObj != null) reorder = Integer.parseInt(reorderObj.toString());
                } catch (NumberFormatException ignored) {
                }

                boolean lowStock = qty <= reorder;

                if (isSelected) {
                    c.setBackground(new Color(255, 220, 170));
                } else if (lowStock) {
                    c.setBackground(new Color(255, 235, 210));
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("Add Item");
        JButton editButton = new JButton("Edit Item");
        JButton deleteButton = new JButton("Delete Item");

        addButton.setBackground(primaryOrange);
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);

        editButton.setBackground(Color.WHITE);
        editButton.setForeground(primaryOrange);
        editButton.setFocusPainted(false);

        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(180, 50, 50));
        deleteButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(inventoryTableModel, "inventory.csv", this));
        importButton.addActionListener(e -> importInventoryFromCSV());

        // Actions
        addButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField brandField = new JTextField();
            JTextField qtyField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField reorderField = new JTextField();
            JTextField locationField = new JTextField();
            JTextField statusField = new JTextField("Available");

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Item ID:"));
            form.add(idField);
            form.add(new JLabel("Item Name:"));
            form.add(nameField);
            form.add(new JLabel("Category:"));
            form.add(categoryField);
            form.add(new JLabel("Brand/Model:"));
            form.add(brandField);
            form.add(new JLabel("Quantity:"));
            form.add(qtyField);
            form.add(new JLabel("Unit Price:"));
            form.add(priceField);
            form.add(new JLabel("Reorder Level:"));
            form.add(reorderField);
            form.add(new JLabel("Location:"));
            form.add(locationField);
            form.add(new JLabel("Status:"));
            form.add(statusField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Add Inventory Item",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    int reorder = Integer.parseInt(reorderField.getText().trim());

                    // Save to database
                    if (InventoryDAO.addItem(
                            idField.getText().trim(),
                            nameField.getText().trim(),
                            categoryField.getText().trim(),
                            brandField.getText().trim(),
                            qty,
                            price,
                            reorder,
                            locationField.getText().trim(),
                            statusField.getText().trim())) {
                        
                        // Reload from database
                        InventoryDAO.loadToTableModel(inventoryTableModel);
                        updateInventorySummary();
                        JOptionPane.showMessageDialog(this, "Item added successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add item to database.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter valid numeric values for Quantity, Unit Price, and Reorder Level.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to edit.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField idField = new JTextField(inventoryTableModel.getValueAt(row, 0).toString());
            JTextField nameField = new JTextField(inventoryTableModel.getValueAt(row, 1).toString());
            JTextField categoryField = new JTextField(inventoryTableModel.getValueAt(row, 2).toString());
            JTextField brandField = new JTextField(inventoryTableModel.getValueAt(row, 3).toString());
            JTextField qtyField = new JTextField(inventoryTableModel.getValueAt(row, 4).toString());
            JTextField priceField = new JTextField(inventoryTableModel.getValueAt(row, 5).toString());
            JTextField reorderField = new JTextField(inventoryTableModel.getValueAt(row, 6).toString());
            JTextField locationField = new JTextField(inventoryTableModel.getValueAt(row, 7).toString());
            JTextField statusField = new JTextField(inventoryTableModel.getValueAt(row, 8).toString());

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Item ID:"));
            form.add(idField);
            form.add(new JLabel("Item Name:"));
            form.add(nameField);
            form.add(new JLabel("Category:"));
            form.add(categoryField);
            form.add(new JLabel("Brand/Model:"));
            form.add(brandField);
            form.add(new JLabel("Quantity:"));
            form.add(qtyField);
            form.add(new JLabel("Unit Price:"));
            form.add(priceField);
            form.add(new JLabel("Reorder Level:"));
            form.add(reorderField);
            form.add(new JLabel("Location:"));
            form.add(locationField);
            form.add(new JLabel("Status:"));
            form.add(statusField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Inventory Item",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String itemId = idField.getText().trim();
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    int reorder = Integer.parseInt(reorderField.getText().trim());

                    // Update in database
                    if (InventoryDAO.updateItem(
                            itemId,
                            nameField.getText().trim(),
                            categoryField.getText().trim(),
                            brandField.getText().trim(),
                            qty,
                            price,
                            reorder,
                            locationField.getText().trim(),
                            statusField.getText().trim())) {
                        
                        // Reload from database
                        InventoryDAO.loadToTableModel(inventoryTableModel);
                        updateInventorySummary();
                        JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update item in database.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter valid numeric values for Quantity, Unit Price, and Reorder Level.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected item?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                String itemId = inventoryTableModel.getValueAt(row, 0).toString();
                
                // Delete from database
                if (InventoryDAO.deleteItem(itemId)) {
                    // Reload from database
                    InventoryDAO.loadToTableModel(inventoryTableModel);
                    updateInventorySummary();
                    JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete item from database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout
        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        // Initialize summary counts
        updateInventorySummary();

        return panel;
    }

    private void updateInventorySummary() {
        if (inventoryTableModel == null) {
            return;
        }

        try {
            if (DatabaseConnection.testConnection()) {
                int total = InventoryDAO.getTotalItems();
                int lowStock = InventoryDAO.getLowStockCount();

                if (invTotalItemsLabel != null) {
                    invTotalItemsLabel.setText("Total Items: " + total);
                }
                if (invLowStockLabel != null) {
                    invLowStockLabel.setText("Low Stock: " + lowStock);
                }
            } else {
                // Fallback to table-based calculation
                int rows = inventoryTableModel.getRowCount();
                int lowStock = 0;

                for (int i = 0; i < rows; i++) {
                    int qty = 0;
                    int reorder = 0;
                    try {
                        Object qtyObj = inventoryTableModel.getValueAt(i, 4);
                        Object reorderObj = inventoryTableModel.getValueAt(i, 6);
                        if (qtyObj != null) qty = Integer.parseInt(qtyObj.toString());
                        if (reorderObj != null) reorder = Integer.parseInt(reorderObj.toString());
                    } catch (NumberFormatException ignored) {
                    }

                    if (qty <= reorder) {
                        lowStock++;
                    }
                }

                if (invTotalItemsLabel != null) {
                    invTotalItemsLabel.setText("Total Items: " + rows);
                }
                if (invLowStockLabel != null) {
                    invLowStockLabel.setText("Low Stock: " + lowStock);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating inventory summary: " + e.getMessage());
        }
    }

    /**
     * Suppliers panel with JTable and Add/Edit/Delete + summary.
     */
    private JPanel createSuppliersPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top title / description (shared style)
        JPanel top = createSectionHeader(
                "Suppliers",
                "Track suppliers of solar panels, inverters, batteries, mounting kits, and accessories."
        );

        // Summary badges
        JPanel summaryPanel = new JPanel();
        summaryPanel.setOpaque(false);
        summaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        supTotalSuppliersLabel = new JLabel("Total Suppliers: 0");
        supTotalSuppliersLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        supTotalSuppliersLabel.setForeground(new Color(60, 60, 60));

        supActiveSuppliersLabel = new JLabel("Active: 0");
        supActiveSuppliersLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        supActiveSuppliersLabel.setForeground(new Color(0, 130, 70));

        supInactiveSuppliersLabel = new JLabel("Inactive: 0");
        supInactiveSuppliersLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        supInactiveSuppliersLabel.setForeground(new Color(180, 80, 0));

        summaryPanel.add(supTotalSuppliersLabel);
        summaryPanel.add(supActiveSuppliersLabel);
        summaryPanel.add(supInactiveSuppliersLabel);

        top.add(summaryPanel);

        // Table model (shared across app)
        if (suppliersTableModel == null) {
            String[] columnNames = {
                    "Supplier ID", "Supplier Name", "Contact Person",
                    "Contact Number", "Email", "Address",
                    "Main Products", "Status"
            };

            suppliersTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Load data from database
            try {
                if (DatabaseConnection.testConnection()) {
                    SupplierDAO.loadToTableModel(suppliersTableModel);
                }
            } catch (Exception e) {
                // Silently ignore
            }
        }

        JTable table = new JTable(suppliersTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Status coloring
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLACK);

                Object statusObj = tbl.getValueAt(row, 7);
                String status = statusObj == null ? "" : statusObj.toString().toLowerCase();

                if (isSelected) {
                    c.setBackground(new Color(255, 220, 170));
                } else if (status.contains("inactive")) {
                    c.setBackground(new Color(255, 235, 210)); // light orange
                } else if (status.contains("active")) {
                    c.setBackground(new Color(225, 245, 230)); // light green
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("Add Supplier");
        JButton editButton = new JButton("Edit Supplier");
        JButton deleteButton = new JButton("Delete Supplier");

        addButton.setBackground(primaryOrange);
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);

        editButton.setBackground(Color.WHITE);
        editButton.setForeground(primaryOrange);
        editButton.setFocusPainted(false);

        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(180, 50, 50));
        deleteButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(suppliersTableModel, "suppliers.csv", this));
        importButton.addActionListener(e -> importSuppliersFromCSV());

        // Actions
        addButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField contactPersonField = new JTextField();
            JTextField contactNumberField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField productsField = new JTextField();
            JTextField statusField = new JTextField("Active");

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Supplier ID:"));
            form.add(idField);
            form.add(new JLabel("Supplier Name:"));
            form.add(nameField);
            form.add(new JLabel("Contact Person:"));
            form.add(contactPersonField);
            form.add(new JLabel("Contact Number:"));
            form.add(contactNumberField);
            form.add(new JLabel("Email:"));
            form.add(emailField);
            form.add(new JLabel("Address:"));
            form.add(addressField);
            form.add(new JLabel("Main Products:"));
            form.add(productsField);
            form.add(new JLabel("Status:"));
            form.add(statusField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Add Supplier",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Save to database
                if (SupplierDAO.addSupplier(
                        idField.getText().trim(),
                        nameField.getText().trim(),
                        contactPersonField.getText().trim(),
                        contactNumberField.getText().trim(),
                        emailField.getText().trim(),
                        addressField.getText().trim(),
                        productsField.getText().trim(),
                        statusField.getText().trim())) {
                    
                    // Reload from database
                    SupplierDAO.loadToTableModel(suppliersTableModel);
                    updateSuppliersSummary();
                    JOptionPane.showMessageDialog(this, "Supplier added successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add supplier to database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a supplier to edit.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField idField = new JTextField(suppliersTableModel.getValueAt(row, 0).toString());
            JTextField nameField = new JTextField(suppliersTableModel.getValueAt(row, 1).toString());
            JTextField contactPersonField = new JTextField(suppliersTableModel.getValueAt(row, 2).toString());
            JTextField contactNumberField = new JTextField(suppliersTableModel.getValueAt(row, 3).toString());
            JTextField emailField = new JTextField(suppliersTableModel.getValueAt(row, 4).toString());
            JTextField addressField = new JTextField(suppliersTableModel.getValueAt(row, 5).toString());
            JTextField productsField = new JTextField(suppliersTableModel.getValueAt(row, 6).toString());
            JTextField statusField = new JTextField(suppliersTableModel.getValueAt(row, 7).toString());

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Supplier ID:"));
            form.add(idField);
            form.add(new JLabel("Supplier Name:"));
            form.add(nameField);
            form.add(new JLabel("Contact Person:"));
            form.add(contactPersonField);
            form.add(new JLabel("Contact Number:"));
            form.add(contactNumberField);
            form.add(new JLabel("Email:"));
            form.add(emailField);
            form.add(new JLabel("Address:"));
            form.add(addressField);
            form.add(new JLabel("Main Products:"));
            form.add(productsField);
            form.add(new JLabel("Status:"));
            form.add(statusField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Supplier",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Update in database
                String supplierId = idField.getText().trim();
                if (SupplierDAO.updateSupplier(
                        supplierId,
                        nameField.getText().trim(),
                        contactPersonField.getText().trim(),
                        contactNumberField.getText().trim(),
                        emailField.getText().trim(),
                        addressField.getText().trim(),
                        productsField.getText().trim(),
                        statusField.getText().trim())) {
                    
                    // Reload from database
                    SupplierDAO.loadToTableModel(suppliersTableModel);
                    updateSuppliersSummary();
                    JOptionPane.showMessageDialog(this, "Supplier updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update supplier in database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a supplier to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected supplier?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                String supplierId = suppliersTableModel.getValueAt(row, 0).toString();
                if (SupplierDAO.deleteSupplier(supplierId)) {
                    // Reload from database
                    SupplierDAO.loadToTableModel(suppliersTableModel);
                    updateSuppliersSummary();
                    JOptionPane.showMessageDialog(this, "Supplier deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete supplier from database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout
        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        updateSuppliersSummary();

        return panel;
    }

    private void updateSuppliersSummary() {
        if (suppliersTableModel == null) {
            return;
        }

        try {
            if (DatabaseConnection.testConnection()) {
                int total = SupplierDAO.getTotalSuppliers();
                int active = SupplierDAO.getActiveSuppliers();
                int inactive = total - active;

                if (supTotalSuppliersLabel != null) {
                    supTotalSuppliersLabel.setText("Total Suppliers: " + total);
                }
                if (supActiveSuppliersLabel != null) {
                    supActiveSuppliersLabel.setText("Active: " + active);
                }
                if (supInactiveSuppliersLabel != null) {
                    supInactiveSuppliersLabel.setText("Inactive: " + inactive);
                }
            } else {
                // Fallback to table-based calculation
                int rows = suppliersTableModel.getRowCount();
                int active = 0;
                int inactive = 0;

                for (int i = 0; i < rows; i++) {
                    Object statusObj = suppliersTableModel.getValueAt(i, 7);
                    String status = statusObj == null ? "" : statusObj.toString().toLowerCase();
                    if (status.contains("active") && !status.contains("inactive")) {
                        active++;
                    } else if (status.contains("inactive")) {
                        inactive++;
                    }
                }

                if (supTotalSuppliersLabel != null) {
                    supTotalSuppliersLabel.setText("Total Suppliers: " + rows);
                }
                if (supActiveSuppliersLabel != null) {
                    supActiveSuppliersLabel.setText("Active: " + active);
                }
                if (supInactiveSuppliersLabel != null) {
                    supInactiveSuppliersLabel.setText("Inactive: " + inactive);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating suppliers summary: " + e.getMessage());
        }
    }

    /**
     * Maintenance panel with JTable and Add/Edit/Delete + summary.
     */
    private JPanel createMaintenancePanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel top = createSectionHeader(
                "Maintenance",
                "Record installation, inspection, and repair activities for customer renewable systems."
        );

        // Summary badges
        JPanel summaryPanel = new JPanel();
        summaryPanel.setOpaque(false);
        summaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        maintTotalTicketsLabel = new JLabel("Total Tickets: 0");
        maintTotalTicketsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        maintTotalTicketsLabel.setForeground(new Color(60, 60, 60));

        maintScheduledLabel = new JLabel("Scheduled: 0");
        maintScheduledLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        maintScheduledLabel.setForeground(new Color(180, 120, 0));

        maintCompletedLabel = new JLabel("Completed: 0");
        maintCompletedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        maintCompletedLabel.setForeground(new Color(0, 130, 70));

        summaryPanel.add(maintTotalTicketsLabel);
        summaryPanel.add(maintScheduledLabel);
        summaryPanel.add(maintCompletedLabel);

        top.add(Box.createVerticalStrut(6));
        top.add(summaryPanel);

        // Table model (shared)
        if (maintenanceTableModel == null) {
            String[] columnNames = {
                    "Ticket ID", "Customer Name", "Contact No.",
                    "Site Address", "Equipment", "Service Type",
                    "Schedule Date", "Technician", "Status", "Notes"
            };

            maintenanceTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Load data from database
            try {
                if (DatabaseConnection.testConnection()) {
                    MaintenanceDAO.loadToTableModel(maintenanceTableModel);
                }
            } catch (Exception e) {
                System.err.println("Error loading maintenance: " + e.getMessage());
            }
        }

        JTable table = new JTable(maintenanceTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Row coloring by status
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLACK);

                Object statusObj = tbl.getValueAt(row, 8);
                String status = statusObj == null ? "" : statusObj.toString().toLowerCase();

                if (isSelected) {
                    c.setBackground(new Color(255, 220, 170));
                } else if (status.contains("completed")) {
                    c.setBackground(new Color(225, 245, 230)); // green
                } else if (status.contains("scheduled")) {
                    c.setBackground(new Color(255, 245, 220)); // yellow
                } else if (status.contains("cancel")) {
                    c.setBackground(new Color(255, 230, 230)); // red
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("Add Ticket");
        JButton editButton = new JButton("Edit Ticket");
        JButton deleteButton = new JButton("Delete Ticket");

        addButton.setBackground(primaryOrange);
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);

        editButton.setBackground(Color.WHITE);
        editButton.setForeground(primaryOrange);
        editButton.setFocusPainted(false);

        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(180, 50, 50));
        deleteButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(maintenanceTableModel, "maintenance.csv", this));
        importButton.addActionListener(e -> importMaintenanceFromCSV());

        // Actions
        addButton.addActionListener(e -> {
            JTextField ticketIdField = new JTextField();
            JTextField customerField = new JTextField();
            JTextField contactField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField equipmentField = new JTextField();
            JTextField serviceTypeField = new JTextField("Installation");
            JTextField scheduleField = new JTextField();
            JTextField techField = new JTextField();
            
            // Status dropdown instead of text field
            String[] statusOptions = {"Pending", "Scheduled", "Approved", "In Progress", "Completed", "Cancelled"};
            JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
            statusCombo.setSelectedItem("Pending"); // Default to Pending for new tickets
            
            JTextField notesField = new JTextField();

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Ticket ID:"));
            form.add(ticketIdField);
            form.add(new JLabel("Customer Name:"));
            form.add(customerField);
            form.add(new JLabel("Contact No.:"));
            form.add(contactField);
            form.add(new JLabel("Site Address:"));
            form.add(addressField);
            form.add(new JLabel("Equipment:"));
            form.add(equipmentField);
            form.add(new JLabel("Service Type:"));
            form.add(serviceTypeField);
            form.add(new JLabel("Schedule Date:"));
            form.add(scheduleField);
            form.add(new JLabel("Technician:"));
            form.add(techField);
            form.add(new JLabel("Status:"));
            form.add(statusCombo);
            form.add(new JLabel("Notes:"));
            form.add(notesField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Add Maintenance Ticket",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Save to database
                String selectedStatus = (String) statusCombo.getSelectedItem();
                if (MaintenanceDAO.addMaintenance(
                        ticketIdField.getText().trim(),
                        customerField.getText().trim(),
                        contactField.getText().trim(),
                        addressField.getText().trim(),
                        equipmentField.getText().trim(),
                        serviceTypeField.getText().trim(),
                        scheduleField.getText().trim(),
                        techField.getText().trim(),
                        selectedStatus != null ? selectedStatus : "Pending",
                        notesField.getText().trim())) {
                    
                    // Reload from database
                    MaintenanceDAO.loadToTableModel(maintenanceTableModel);
                    updateMaintenanceSummary();
                    JOptionPane.showMessageDialog(this, "Maintenance ticket added successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add maintenance ticket to database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a ticket to edit.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField ticketIdField = new JTextField(maintenanceTableModel.getValueAt(row, 0).toString());
            ticketIdField.setEditable(false); // Ticket ID should not be editable
            JTextField customerField = new JTextField(maintenanceTableModel.getValueAt(row, 1).toString());
            JTextField contactField = new JTextField(maintenanceTableModel.getValueAt(row, 2).toString());
            JTextField addressField = new JTextField(maintenanceTableModel.getValueAt(row, 3).toString());
            JTextField equipmentField = new JTextField(maintenanceTableModel.getValueAt(row, 4).toString());
            JTextField serviceTypeField = new JTextField(maintenanceTableModel.getValueAt(row, 5).toString());
            JTextField scheduleField = new JTextField(maintenanceTableModel.getValueAt(row, 6).toString());
            JTextField techField = new JTextField(maintenanceTableModel.getValueAt(row, 7).toString());
            
            // Status dropdown instead of text field
            String[] statusOptions = {"Pending", "Scheduled", "Approved", "In Progress", "Completed", "Cancelled"};
            String currentStatus = maintenanceTableModel.getValueAt(row, 8).toString();
            JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
            statusCombo.setSelectedItem(currentStatus); // Set current status
            if (statusCombo.getSelectedItem() == null) {
                statusCombo.setSelectedItem("Pending"); // Default if status not in list
            }
            
            JTextField notesField = new JTextField(maintenanceTableModel.getValueAt(row, 9).toString());

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
            form.add(new JLabel("Ticket ID:"));
            form.add(ticketIdField);
            form.add(new JLabel("Customer Name:"));
            form.add(customerField);
            form.add(new JLabel("Contact No.:"));
            form.add(contactField);
            form.add(new JLabel("Site Address:"));
            form.add(addressField);
            form.add(new JLabel("Equipment:"));
            form.add(equipmentField);
            form.add(new JLabel("Service Type:"));
            form.add(serviceTypeField);
            form.add(new JLabel("Schedule Date:"));
            form.add(scheduleField);
            form.add(new JLabel("Technician:"));
            form.add(techField);
            form.add(new JLabel("Status:"));
            form.add(statusCombo);
            form.add(new JLabel("Notes:"));
            form.add(notesField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Maintenance Ticket",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Update in database
                String ticketId = ticketIdField.getText().trim();
                String customerName = customerField.getText().trim();
                String contactNo = contactField.getText().trim();
                String address = addressField.getText().trim();
                String equipment = equipmentField.getText().trim();
                String serviceType = serviceTypeField.getText().trim();
                String scheduleDate = scheduleField.getText().trim();
                String technicianEmail = techField.getText().trim();
                String status = (String) statusCombo.getSelectedItem(); // Get selected status from combo
                String notes = notesField.getText().trim();
                
                if (MaintenanceDAO.updateMaintenance(
                        ticketId, customerName, contactNo, address, equipment,
                        serviceType, scheduleDate, technicianEmail, status, notes)) {
                    
                    // If status is "Scheduled" or "Approved" and technician is assigned, create job and notify technician
                    if ((status.equals("Scheduled") || status.equals("Approved")) && 
                        !technicianEmail.isEmpty() && !technicianEmail.equals("Pending Assignment")) {
                        approveMaintenanceAndAssignTechnician(ticketId, technicianEmail, customerName,
                                address, equipment, serviceType, scheduleDate, "09:00:00");
                    } else {
                        // Just reload from database
                    MaintenanceDAO.loadToTableModel(maintenanceTableModel);
                    updateMaintenanceSummary();
                    }
                    
                    JOptionPane.showMessageDialog(this, "Maintenance ticket updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update maintenance ticket in database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a ticket to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected maintenance ticket?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                String ticketId = maintenanceTableModel.getValueAt(row, 0).toString();
                if (MaintenanceDAO.deleteMaintenance(ticketId)) {
                    // Reload from database
                    MaintenanceDAO.loadToTableModel(maintenanceTableModel);
                    updateMaintenanceSummary();
                    JOptionPane.showMessageDialog(this, "Maintenance ticket deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete maintenance ticket from database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout
        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        updateMaintenanceSummary();

        return panel;
    }

    private void updateMaintenanceSummary() {
        if (maintenanceTableModel == null) {
            return;
        }

        try {
            if (DatabaseConnection.testConnection()) {
                int total = MaintenanceDAO.getTotalTickets();
                int scheduled = MaintenanceDAO.getScheduledCount();
                int completed = MaintenanceDAO.getCompletedCount();

                if (maintTotalTicketsLabel != null) {
                    maintTotalTicketsLabel.setText("Total Tickets: " + total);
                }
                if (maintScheduledLabel != null) {
                    maintScheduledLabel.setText("Scheduled: " + scheduled);
                }
                if (maintCompletedLabel != null) {
                    maintCompletedLabel.setText("Completed: " + completed);
                }
            } else {
                // Fallback to table-based calculation
                int rows = maintenanceTableModel.getRowCount();
                int scheduled = 0;
                int completed = 0;

                for (int i = 0; i < rows; i++) {
                    Object statusObj = maintenanceTableModel.getValueAt(i, 8);
                    String status = statusObj == null ? "" : statusObj.toString().toLowerCase();
                    if (status.contains("scheduled")) {
                        scheduled++;
                    } else if (status.contains("completed")) {
                        completed++;
                    }
                }

                if (maintTotalTicketsLabel != null) {
                    maintTotalTicketsLabel.setText("Total Tickets: " + rows);
                }
                if (maintScheduledLabel != null) {
                    maintScheduledLabel.setText("Scheduled: " + scheduled);
                }
                if (maintCompletedLabel != null) {
                    maintCompletedLabel.setText("Completed: " + completed);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating maintenance summary: " + e.getMessage());
        }
    }

    /**
     * Called from CompanyAdminFrame to push a maintenance/complaint ticket to admin.
     */
    public void addMaintenanceTicketFromCompanyAdmin(String customerName,
                                                     String contact,
                                                     String address,
                                                     String equipment,
                                                     String serviceType,
                                                     String description) {
        if (maintenanceTableModel == null) {
            // Ensure panel initialized so model exists
            createMaintenancePanel();
        }

        String ticketId = "MT-CUST-" + (maintenanceTableModel.getRowCount() + 1);
        String scheduleDate = LocalDate.now().toString();
        String technician = "Pending Assignment";
        String status = "Scheduled";

        maintenanceTableModel.addRow(new Object[]{
                ticketId,
                customerName,
                contact,
                address,
                equipment,
                serviceType,
                scheduleDate,
                technician,
                status,
                description
        });

        updateMaintenanceSummary();
    }
    
    /**
     * Called from CustomerPortal to notify admin of new maintenance request
     */
    public void notifyNewMaintenanceRequest(String ticketId, String customerName, String contactNo,
                                           String address, String equipment, String serviceType,
                                           String scheduleDate, String notes) {
        if (maintenanceTableModel == null) {
            createMaintenancePanel();
        }
        
        // Reload from database to get the new request
        MaintenanceDAO.loadToTableModel(maintenanceTableModel);
        updateMaintenanceSummary();
    }
    
    /**
     * Called when admin approves maintenance and assigns technician
     * Creates technician job and notifies technician
     */
    public void approveMaintenanceAndAssignTechnician(String ticketId, String technicianEmail,
                                                     String customerName, String address,
                                                     String equipment, String serviceType,
                                                     String scheduleDate, String scheduleTime) {
        // Generate unique job ID (use timestamp to avoid duplicates)
        String baseJobId = "JOB-" + ticketId.replace("MT-", "");
        String jobId = baseJobId + "-" + System.currentTimeMillis() % 10000; // Add timestamp suffix
        
        // Validate technician email
        if (technicianEmail == null || technicianEmail.trim().isEmpty() || 
            technicianEmail.equals("Pending Assignment")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid technician email address.",
                "Invalid Technician Email",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create technician job first
        boolean jobCreated = TechnicianJobDAO.addJob(jobId, technicianEmail.trim(), customerName, address, equipment,
                serviceType, scheduleDate, scheduleTime, "Pending", "Normal");
        
        if (jobCreated) {
            // Update maintenance status to "Scheduled" with technician assigned
            MaintenanceDAO.updateMaintenance(ticketId, customerName, "", address, equipment,
                    serviceType, scheduleDate, technicianEmail, "Scheduled", "");
            
            // Notify technician in real-time (if technician frame is open)
            TechnicianFrame techFrame = TechnicianFrame.getInstanceByEmail(technicianEmail);
            if (techFrame != null) {
                techFrame.notifyNewJob(jobId, customerName, serviceType, scheduleDate);
                System.out.println("✓ Notified technician " + technicianEmail + " of new job: " + jobId);
            } else {
                // Try current instance as fallback
                techFrame = TechnicianFrame.getCurrentInstance();
                if (techFrame != null && techFrame.getTechnicianEmail().equals(technicianEmail)) {
                    techFrame.notifyNewJob(jobId, customerName, serviceType, scheduleDate);
                    System.out.println("✓ Notified technician " + technicianEmail + " of new job: " + jobId);
                } else {
                    System.out.println("⚠ Technician " + technicianEmail + " is not currently logged in. Job created in database.");
                }
            }
            
            // Reload maintenance table
            MaintenanceDAO.loadToTableModel(maintenanceTableModel);
            updateMaintenanceSummary();
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Maintenance scheduled successfully!\n" +
                "Job ID: " + jobId + "\n" +
                "Assigned to: " + technicianEmail,
                "Job Assigned",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Check if job already exists (might be duplicate)
            String existingJobId = "JOB-" + ticketId.replace("MT-", "");
            String errorMsg = "Failed to create technician job.\n\n" +
                             "Possible reasons:\n" +
                             "1. Job ID already exists: " + existingJobId + "\n" +
                             "2. Invalid technician email: " + technicianEmail + "\n" +
                             "3. Database connection issue\n\n" +
                             "Please check the console for detailed error messages.";
            
            JOptionPane.showMessageDialog(this, 
                errorMsg,
                "Error Creating Job",
                JOptionPane.ERROR_MESSAGE);
            
            System.err.println("Failed to create job. Ticket ID: " + ticketId + ", Job ID: " + existingJobId);
        }
    }

    /**
     * Stock Requests Panel - View and manage stock requests from Company Admin
     */
    private JPanel createStockRequestsPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top section: title + summary
        JPanel top = createSectionHeader(
                "Stock Requests",
                "View and manage stock requests submitted by Company Admin / Warehouse Managers."
        );

        // Summary labels
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        summaryPanel.setOpaque(false);

        stockReqTotalLabel = new JLabel("Total Requests: 0");
        stockReqTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stockReqTotalLabel.setForeground(new Color(60, 60, 60));

        stockReqPendingLabel = new JLabel("Pending: 0");
        stockReqPendingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockReqPendingLabel.setForeground(new Color(200, 140, 0));

        stockReqApprovedLabel = new JLabel("Approved: 0");
        stockReqApprovedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockReqApprovedLabel.setForeground(new Color(0, 150, 0));

        summaryPanel.add(stockReqTotalLabel);
        summaryPanel.add(stockReqPendingLabel);
        summaryPanel.add(stockReqApprovedLabel);

        top.add(summaryPanel);
        top.add(Box.createVerticalStrut(10));

        // Table model (shared across app)
        if (stockRequestsTableModel == null) {
            String[] columnNames = {
                    "Request ID", "Item Name", "Category", "Quantity Requested",
                    "Supplier", "Request Date", "Status", "Notes"
            };

            stockRequestsTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Load data from database
            try {
                if (DatabaseConnection.testConnection()) {
                    StockRequestDAO.loadToTableModel(stockRequestsTableModel);
                }
            } catch (Exception e) {
                System.err.println("Error loading stock requests: " + e.getMessage());
            }
        }

        JTable table = new JTable(stockRequestsTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Status-based coloring
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 6) { // Status column
                    String status = value == null ? "" : value.toString().toLowerCase();
                    if (status.contains("pending")) {
                        c.setBackground(new Color(255, 240, 200));
                        c.setForeground(new Color(200, 120, 0));
                    } else if (status.contains("approved")) {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(new Color(0, 120, 0));
                    } else if (status.contains("rejected")) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(new Color(180, 0, 0));
                    } else {
                        c.setBackground(white);
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setBackground(isSelected ? new Color(200, 220, 255) : white);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        table.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);

        JScrollPane scrollPane = new JScrollPane(table);

        // Button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton requestStockButton = new JButton("Request Stock from Solar Philippines");
        JButton editButton = new JButton("Edit Status");
        JButton deleteButton = new JButton("Delete Request");
        
        requestStockButton.setBackground(new Color(0, 120, 200));
        requestStockButton.setForeground(Color.BLACK);
        requestStockButton.setFocusPainted(false);

        editButton.setBackground(primaryOrange);
        editButton.setForeground(Color.BLACK);
        editButton.setFocusPainted(false);

        deleteButton.setBackground(Color.WHITE);
        deleteButton.setForeground(new Color(180, 50, 50));
        deleteButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(requestStockButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(stockRequestsTableModel, "stock_requests.csv", this));
        importButton.addActionListener(e -> importStockRequestsFromCSV());

        // Actions
        requestStockButton.addActionListener(e -> openRequestStockFromSolarPhilippinesDialog());

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to edit.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Status dropdown instead of text field
            String[] statusOptions = {"Pending", "Approved", "Rejected", "Cancelled", "Completed"};
            String currentStatus = stockRequestsTableModel.getValueAt(row, 6).toString();
            JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
            statusCombo.setSelectedItem(currentStatus); // Set current status
            if (statusCombo.getSelectedItem() == null) {
                statusCombo.setSelectedItem("Pending"); // Default if status not in list
            }
            
            JTextArea notesArea = new JTextArea(stockRequestsTableModel.getValueAt(row, 7).toString(), 3, 20);
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            form.add(new JLabel("Status:"), gbc);
            gbc.gridy++;
            form.add(new JLabel("Notes:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            form.add(statusCombo, gbc);
            gbc.gridy++;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            form.add(new JScrollPane(notesArea), gbc);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Stock Request",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String requestId = stockRequestsTableModel.getValueAt(row, 0).toString();
                String newStatus = (String) statusCombo.getSelectedItem(); // Get selected status from combo
                String newNotes = notesArea.getText().trim();
                
                // Update in database
                if (StockRequestDAO.updateRequest(requestId, newStatus, newNotes)) {
                    // Reload from database
                    StockRequestDAO.loadToTableModel(stockRequestsTableModel);
                    updateStockRequestsSummary();
                    JOptionPane.showMessageDialog(this, "Stock request updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update stock request in database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
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
                String requestId = stockRequestsTableModel.getValueAt(row, 0).toString();
                
                // Delete from database
                if (StockRequestDAO.deleteRequest(requestId)) {
                    // Reload from database
                    StockRequestDAO.loadToTableModel(stockRequestsTableModel);
                    updateStockRequestsSummary();
                    JOptionPane.showMessageDialog(this, "Stock request deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete stock request from database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout
        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        updateStockRequestsSummary();

        return panel;
    }

    private void updateStockRequestsSummary() {
        if (stockRequestsTableModel == null) {
            return;
        }

        try {
            if (DatabaseConnection.testConnection()) {
                int total = StockRequestDAO.getTotalRequests();
                int pending = StockRequestDAO.getPendingRequests();
                int approved = StockRequestDAO.getApprovedRequests();

                if (stockReqTotalLabel != null) {
                    stockReqTotalLabel.setText("Total Requests: " + total);
                }
                if (stockReqPendingLabel != null) {
                    stockReqPendingLabel.setText("Pending: " + pending);
                }
                if (stockReqApprovedLabel != null) {
                    stockReqApprovedLabel.setText("Approved: " + approved);
                }
            } else {
                // Fallback to table-based calculation
                int rows = stockRequestsTableModel.getRowCount();
                int pending = 0;
                int approved = 0;

                for (int i = 0; i < rows; i++) {
                    Object statusObj = stockRequestsTableModel.getValueAt(i, 6);
                    String status = statusObj == null ? "" : statusObj.toString().toLowerCase();
                    if (status.contains("pending")) {
                        pending++;
                    } else if (status.contains("approved")) {
                        approved++;
                    }
                }

                if (stockReqTotalLabel != null) {
                    stockReqTotalLabel.setText("Total Requests: " + rows);
                }
                if (stockReqPendingLabel != null) {
                    stockReqPendingLabel.setText("Pending: " + pending);
                }
                if (stockReqApprovedLabel != null) {
                    stockReqApprovedLabel.setText("Approved: " + approved);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating stock requests summary: " + e.getMessage());
        }
    }

    /**
     * Called from CompanyAdminFrame to add a stock request to admin dashboard.
     * Reloads from database to ensure data consistency.
     */
    public void addStockRequestFromCompanyAdmin(String requestId, String itemName, String category,
                                                int quantity, String supplier, String requestDate,
                                                String status, String notes) {
        if (stockRequestsTableModel == null) {
            // Ensure panel initialized so model exists
            createStockRequestsPanel();
        }

        // Reload from database to ensure data consistency and avoid duplicates
        StockRequestDAO.loadToTableModel(stockRequestsTableModel);
        updateStockRequestsSummary();
    }
    /**
     * Reports panel with multiple report types in one JTable.
     */
    private JPanel createReportsPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top section: title + controls
        JPanel top = createSectionHeader(
                "Reports",
                "Generate simple summary reports for sales, inventory levels, and suppliers."
        );

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel typeLabel = new JLabel("Report Type:");
        String[] reportTypes = {"Sales Report", "Inventory Report", "Supplier Report"};
        JComboBox<String> reportTypeCombo = new JComboBox<>(reportTypes);
        JButton generateButton = new JButton("Generate Report");
        generateButton.setBackground(primaryOrange);
        generateButton.setForeground(Color.BLACK);
        generateButton.setFocusPainted(false);

        controls.add(typeLabel);
        controls.add(reportTypeCombo);
        controls.add(generateButton);

        repTitleLabel = new JLabel("Sales Report", SwingConstants.CENTER);
        repTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        repTitleLabel.setForeground(new Color(60, 60, 60));
        repTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        repSummaryLabel = new JLabel(" ", SwingConstants.CENTER);
        repSummaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        repSummaryLabel.setForeground(new Color(90, 90, 90));
        repSummaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(controls);
        top.add(Box.createVerticalStrut(8));
        top.add(repTitleLabel);
        top.add(Box.createVerticalStrut(4));
        top.add(repSummaryLabel);

        // Table for reports
        DefaultTableModel reportModel = new DefaultTableModel();
        JTable table = new JTable(reportModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        // Generate function
        Runnable generate = () -> {
            String selected = (String) reportTypeCombo.getSelectedItem();
            if (selected == null) return;

            switch (selected) {
                case "Sales Report":
                    buildSalesReportTable(reportModel);
                    break;
                case "Inventory Report":
                    buildInventoryReportTable(reportModel);
                    break;
                case "Supplier Report":
                    buildSupplierReportTable(reportModel);
                    break;
                default:
                    break;
            }
        };

        generateButton.addActionListener(e -> generate.run());
        reportTypeCombo.addActionListener(e -> generate.run());

        // Default first load
        generate.run();

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Technician Applications panel - view and approve/reject pending technician applications
     */
    private JPanel createTechnicianApplicationsPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel top = createSectionHeader(
                "Technician Applications",
                "Review and approve or reject technician account applications."
        );

        // Table model
        if (technicianApplicationsTableModel == null) {
            String[] columnNames = {
                    "Email", "Full Name", "Application Notes", "Applied Date", "Status"
            };

            technicianApplicationsTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        }

        JTable table = new JTable(technicianApplicationsTableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(primaryOrange);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(new Color(220, 220, 220));

        // Auto-resize columns
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Full Name
        table.getColumnModel().getColumn(2).setPreferredWidth(300); // Application Notes
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Applied Date
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Status

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(0, 150, 0));
        approveButton.setForeground(Color.BLACK);
        approveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        approveButton.setFocusPainted(false);
        approveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        approveButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an application to approve.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String email = technicianApplicationsTableModel.getValueAt(selectedRow, 0).toString();
            String fullName = technicianApplicationsTableModel.getValueAt(selectedRow, 1).toString();
            String status = technicianApplicationsTableModel.getValueAt(selectedRow, 4).toString();

            if ("Approved".equals(status)) {
                JOptionPane.showMessageDialog(this, "This application is already approved.", "Already Approved",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Approve technician application for:\n\n" +
                    "Email: " + email + "\n" +
                    "Name: " + fullName + "\n\n" +
                    "This will allow the technician to log in.",
                    "Approve Application",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (TechnicianDAO.updateStatus(email, "Approved")) {
                    loadTechnicianApplications();
                    JOptionPane.showMessageDialog(this,
                            "Technician application approved!\n" +
                            "The technician can now log in with their credentials.",
                            "Approved",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to approve application.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(200, 0, 0));
        rejectButton.setForeground(Color.BLACK);
        rejectButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rejectButton.setFocusPainted(false);
        rejectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rejectButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an application to reject.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String email = technicianApplicationsTableModel.getValueAt(selectedRow, 0).toString();
            String fullName = technicianApplicationsTableModel.getValueAt(selectedRow, 1).toString();
            String status = technicianApplicationsTableModel.getValueAt(selectedRow, 4).toString();

            if ("Rejected".equals(status)) {
                JOptionPane.showMessageDialog(this, "This application is already rejected.", "Already Rejected",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Reject technician application for:\n\n" +
                    "Email: " + email + "\n" +
                    "Name: " + fullName + "\n\n" +
                    "This will prevent the technician from logging in.",
                    "Reject Application",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (TechnicianDAO.updateStatus(email, "Rejected")) {
                    loadTechnicianApplications();
                    JOptionPane.showMessageDialog(this,
                            "Technician application rejected.",
                            "Rejected",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to reject application.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(primaryOrange);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTechnicianApplications());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        // Assemble panel
        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        loadTechnicianApplications();

        return panel;
    }

    /**
     * Load technician applications into the table
     */
    private void loadTechnicianApplications() {
        if (technicianApplicationsTableModel == null) {
            return;
        }

        technicianApplicationsTableModel.setRowCount(0);

        java.util.List<java.util.Map<String, String>> applications = TechnicianDAO.getPendingApplications();
        for (java.util.Map<String, String> app : applications) {
            technicianApplicationsTableModel.addRow(new Object[]{
                    app.get("email"),
                    app.get("full_name"),
                    app.get("application_notes"),
                    app.get("created_at"),
                    "Pending"
            });
        }

        // Also load approved and rejected for reference
        try {
            java.sql.Connection conn = util.DatabaseConnection.getConnection();
            String sql = "SELECT email, full_name, application_notes, created_at, status FROM technician_accounts WHERE status IN ('Approved', 'Rejected') ORDER BY created_at DESC";
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    technicianApplicationsTableModel.addRow(new Object[]{
                            rs.getString("email"),
                            rs.getString("full_name"),
                            rs.getString("application_notes"),
                            rs.getTimestamp("created_at").toString(),
                            rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading all technician applications: " + e.getMessage());
        }
    }

    /**
     * Notify admin of new technician application (called from LoginFrame)
     */
    public void notifyNewTechnicianApplication(String email, String fullName, String applicationNotes) {
        // Refresh the applications table if it exists
        if (technicianApplicationsTableModel != null) {
            loadTechnicianApplications();
        }

        // Show notification popup
        JOptionPane.showMessageDialog(this,
                "New Technician Application Received!\n\n" +
                "Email: " + email + "\n" +
                "Name: " + fullName + "\n" +
                "Notes: " + applicationNotes + "\n\n" +
                "Please review in the 'Technician Applications' section.",
                "New Application",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void buildSalesReportTable(DefaultTableModel model) {
        // If there are no sales records yet, clear and show empty
        if (salesTableModel == null) {
            model.setRowCount(0);
            model.setColumnCount(0);
            model.setColumnIdentifiers(new Object[]{"Order ID", "Customer", "Date", "Total Amount", "Status"});
            if (repTitleLabel != null) repTitleLabel.setText("Sales Report (No Data)");
            if (repSummaryLabel != null) repSummaryLabel.setText("No sales records yet.");
            return;
        }

        // Columns: one row per order using the main sales model
        model.setRowCount(0);
        model.setColumnCount(0);
        model.setColumnIdentifiers(new Object[]{"Order ID", "Customer", "Date", "Total Amount", "Status"});

        int totalOrders = salesTableModel.getRowCount();
        int paidOrders = 0;
        double totalSales = 0.0;

        for (int i = 0; i < salesTableModel.getRowCount(); i++) {
            Object orderId = salesTableModel.getValueAt(i, 0);
            Object customer = salesTableModel.getValueAt(i, 1);
            Object date = salesTableModel.getValueAt(i, 2);
            Object total = salesTableModel.getValueAt(i, 6);
            Object statusObj = salesTableModel.getValueAt(i, 7);

            String status = statusObj == null ? "" : statusObj.toString();

            try {
                if (status.toLowerCase().contains("paid")) {
                    paidOrders++;
                }
                if (total != null) {
                    totalSales += Double.parseDouble(total.toString());
                }
            } catch (NumberFormatException ignored) {
            }

            model.addRow(new Object[]{
                    orderId,
                    customer,
                    date,
                    total,
                    status
            });
        }

        if (repTitleLabel != null) {
            repTitleLabel.setText("Sales Report (Summary)");
        }
        if (repSummaryLabel != null) {
            repSummaryLabel.setText(String.format(
                    "Total Orders: %d   |   Paid Orders: %d   |   Total Sales: %.2f",
                    totalOrders, paidOrders, totalSales
            ));
        }
    }

    private void buildInventoryReportTable(DefaultTableModel model) {
        if (inventoryTableModel == null) {
            model.setRowCount(0);
            model.setColumnCount(0);
            model.setColumnIdentifiers(new Object[]{
                    "Item ID", "Item Name", "Category", "Quantity", "Reorder Level", "Status"
            });
            if (repTitleLabel != null) repTitleLabel.setText("Inventory Report (No Data)");
            if (repSummaryLabel != null) repSummaryLabel.setText("No inventory records yet.");
            return;
        }

        // Low stock items based on main inventory model
        model.setRowCount(0);
        model.setColumnCount(0);
        model.setColumnIdentifiers(new Object[]{
                "Item ID", "Item Name", "Category", "Quantity", "Reorder Level", "Status"
        });

        int lowStockCount = 0;

        for (int i = 0; i < inventoryTableModel.getRowCount(); i++) {
            Object qtyObj = inventoryTableModel.getValueAt(i, 4);
            Object reorderObj = inventoryTableModel.getValueAt(i, 6);
            int qty = 0;
            int reorder = 0;
            try {
                if (qtyObj != null) qty = Integer.parseInt(qtyObj.toString());
                if (reorderObj != null) reorder = Integer.parseInt(reorderObj.toString());
            } catch (NumberFormatException ignored) {
            }

            if (qty <= reorder) {
                lowStockCount++;
                model.addRow(new Object[]{
                        inventoryTableModel.getValueAt(i, 0),
                        inventoryTableModel.getValueAt(i, 1),
                        inventoryTableModel.getValueAt(i, 2),
                        qty,
                        reorder,
                        inventoryTableModel.getValueAt(i, 8)
                });
            }
        }

        if (repTitleLabel != null) {
            repTitleLabel.setText("Inventory Report - Low Stock Items");
        }
        if (repSummaryLabel != null) {
            repSummaryLabel.setText("Number of low-stock items: " + lowStockCount);
        }
    }

    private void buildSupplierReportTable(DefaultTableModel model) {
        if (suppliersTableModel == null) {
            model.setRowCount(0);
            model.setColumnCount(0);
            model.setColumnIdentifiers(new Object[]{
                    "Supplier Name", "Main Products", "Status"
            });
            if (repTitleLabel != null) repTitleLabel.setText("Supplier Report (No Data)");
            if (repSummaryLabel != null) repSummaryLabel.setText("No supplier records yet.");
            return;
        }

        model.setRowCount(0);
        model.setColumnCount(0);
        model.setColumnIdentifiers(new Object[]{
                "Supplier Name", "Main Products", "Status"
        });

        int total = suppliersTableModel.getRowCount();
        int active = 0;
        int inactive = 0;

        for (int i = 0; i < total; i++) {
            String status = suppliersTableModel.getValueAt(i, 7).toString().toLowerCase();
            if (status.contains("active") && !status.contains("inactive")) {
                active++;
            } else if (status.contains("inactive")) {
                inactive++;
            }

            model.addRow(new Object[]{
                    suppliersTableModel.getValueAt(i, 1),
                    suppliersTableModel.getValueAt(i, 6),
                    suppliersTableModel.getValueAt(i, 7)
            });
        }

        if (repTitleLabel != null) {
            repTitleLabel.setText("Supplier Report");
        }
        if (repSummaryLabel != null) {
            repSummaryLabel.setText(
                    "Total Suppliers: " + total +
                            "   |   Active: " + active +
                            "   |   Inactive: " + inactive
            );
        }
    }

    /**
     * Simple placeholder panel (used for Maintenance module).
     */
    @SuppressWarnings("unused")
    private JPanel createPlaceholderPanel(String title, String description) {
        Color white = Color.WHITE;

        JPanel panel = new JPanel();
        panel.setBackground(white);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = createSectionHeader(title, description);

        JTextArea placeholderText = new JTextArea();
        placeholderText.setEditable(false);
        placeholderText.setFont(new Font("Consolas", Font.PLAIN, 12));
        placeholderText.setText(
                "Planned features for this module:\n" +
                        "- Add, edit, delete records\n" +
                        "- Basic searching and filtering\n" +
                        "- Simple reports\n" +
                        "- Connection to database (future enhancement)"
        );
        placeholderText.setBackground(new Color(250, 250, 250));
        placeholderText.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(placeholderText), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Shared header style for module titles and descriptions.
     */
    private JPanel createSectionHeader(String title, String description) {
        Color primaryOrange = new Color(255, 140, 0);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(primaryOrange);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("<html>" + description + "</html>", SwingConstants.CENTER);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(80, 80, 80));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(heading);
        header.add(Box.createVerticalStrut(4));
        header.add(desc);
        header.add(Box.createVerticalStrut(4));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        header.add(separator);

        return header;
    }

    /**
     * Product data class for Solar Philippines products
     */
    private static class SolarProduct {
        String name;
        String category;
        String description;
        double price;
        
        SolarProduct(String name, String category, String description, double price) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.price = price;
        }
        
        @Override
        public String toString() {
            return name + " (" + category + ")";
        }
    }
    
    /**
     * Get list of available products from Solar Philippines
     */
    private SolarProduct[] getSolarPhilippinesProducts() {
        return new SolarProduct[]{
            new SolarProduct(
                "Solar Panel 450W Monocrystalline",
                "Solar Panel",
                "High-efficiency monocrystalline solar panel, 450W output",
                9500.00
            ),
            new SolarProduct(
                "Solar Panel 550W Polycrystalline",
                "Solar Panel",
                "Durable polycrystalline panel, 550W output",
                11000.00
            ),
            new SolarProduct(
                "Inverter 5kW Hybrid",
                "Inverter",
                "5kW hybrid inverter with battery backup support",
                45000.00
            ),
            new SolarProduct(
                "Inverter 3kW Grid-Tie",
                "Inverter",
                "3kW grid-tie inverter for residential use",
                18500.00
            ),
            new SolarProduct(
                "Battery 200Ah Lithium",
                "Battery",
                "200Ah lithium battery for solar storage",
                25000.00
            ),
            new SolarProduct(
                "Battery 100Ah Lead-Acid",
                "Battery",
                "100Ah lead-acid battery, cost-effective solution",
                12000.00
            ),
            new SolarProduct(
                "Charge Controller 60A MPPT",
                "Controller",
                "60A MPPT charge controller for optimal charging",
                8500.00
            ),
            new SolarProduct(
                "Mounting Rails 4m",
                "Mounting",
                "4-meter mounting rails for rooftop installation",
                3500.00
            ),
            new SolarProduct(
                "MC4 Connectors Set",
                "Accessories",
                "Set of MC4 connectors for solar panel wiring",
                1200.00
            ),
            new SolarProduct(
                "Solar Cable 10mm² 100m",
                "Accessories",
                "10mm² solar cable, 100 meters length",
                4500.00
            )
        };
    }
    
    /**
     * Open dialog to request stock from Solar Philippines with product selection
     */
    private void openRequestStockFromSolarPhilippinesDialog() {
        Color primaryOrange = new Color(255, 140, 0);
        Color darkGray = new Color(60, 60, 60);
        Color lightGray = new Color(245, 245, 245);
        Color white = Color.WHITE;
        
        SolarProduct[] products = getSolarPhilippinesProducts();
        JComboBox<SolarProduct> productCombo = new JComboBox<>(products);
        productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean hasCellFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, hasCellFocus);
                if (value instanceof SolarProduct) {
                    setText(((SolarProduct) value).name);
                    setFont(new Font("Segoe UI", Font.PLAIN, 13));
                }
                return c;
            }
        });
        productCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JTextField itemNameField = new JTextField();
        itemNameField.setEditable(false);
        itemNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemNameField.setBackground(lightGray);
        itemNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JTextField categoryField = new JTextField();
        categoryField.setEditable(false);
        categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryField.setBackground(lightGray);
        categoryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JTextField qtyField = new JTextField();
        qtyField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qtyField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JTextField priceField = new JTextField();
        priceField.setEditable(false);
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priceField.setBackground(lightGray);
        priceField.setForeground(darkGray);
        priceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JTextArea notesArea = new JTextArea(4, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // When product is selected, auto-fill fields
        productCombo.addActionListener(e -> {
            SolarProduct selected = (SolarProduct) productCombo.getSelectedItem();
            if (selected != null) {
                itemNameField.setText(selected.name);
                categoryField.setText(selected.category);
                priceField.setText(String.format("₱%.2f", selected.price));
                notesArea.setText(selected.description);
            }
        });
        
        // Create styled labels helper
        java.util.function.Function<String, JLabel> createStyledLabel = (text) -> {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(darkGray);
            return label;
        };
        
        // Left panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(white);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Left column - form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createStyledLabel.apply("Select Product:"), gbc);
        
        gbc.gridy++;
        formPanel.add(createStyledLabel.apply("Item Name:"), gbc);
        
        gbc.gridy++;
        formPanel.add(createStyledLabel.apply("Category:"), gbc);
        
        gbc.gridy++;
        formPanel.add(createStyledLabel.apply("Quantity:"), gbc);
        
        gbc.gridy++;
        formPanel.add(createStyledLabel.apply("Unit Price (₱):"), gbc);
        
        gbc.gridy++;
        formPanel.add(createStyledLabel.apply("Request Date:"), gbc);
        
        gbc.gridy++;
        formPanel.add(createStyledLabel.apply("Notes:"), gbc);
        
        // Middle column - input fields
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;
        
        formPanel.add(productCombo, gbc);
        gbc.gridy++;
        formPanel.add(itemNameField, gbc);
        gbc.gridy++;
        formPanel.add(categoryField, gbc);
        gbc.gridy++;
        formPanel.add(qtyField, gbc);
        gbc.gridy++;
        formPanel.add(priceField, gbc);
        gbc.gridy++;
        formPanel.add(dateField, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(notesArea), gbc);
        
        // Main content layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(white);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryOrange);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        
        JLabel headerTitle = new JLabel("Request Stock from Solar Philippines");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(white);
        
        JLabel headerSubtitle = new JLabel("Select a product and specify quantity");
        headerSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        headerSubtitle.setForeground(new Color(255, 240, 200));
        
        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.add(headerTitle);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(headerSubtitle);
        
        headerPanel.add(headerText, BorderLayout.WEST);
        
        // Main dialog panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(white);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Load first product by default
        if (products.length > 0) {
            productCombo.setSelectedIndex(0);
        }
        
        // Create custom option pane with styled buttons
        int result = JOptionPane.showOptionDialog(
            this,
            mainPanel,
            "Request Stock from Solar Philippines",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new Object[]{"Submit Request", "Cancel"},
            "Submit Request"
        );
        
        if (result == 0) { // OK button clicked
            try {
                int qty = Integer.parseInt(qtyField.getText().trim());
                SolarProduct selected = (SolarProduct) productCombo.getSelectedItem();
                double unitPrice = selected != null ? selected.price : 0.0;
                
                String reqId = "REQ-SOLAR-" + String.format("%03d", 
                    (stockRequestsTableModel != null ? stockRequestsTableModel.getRowCount() : 0) + 1);
                String itemName = itemNameField.getText().trim();
                String category = categoryField.getText().trim();
                String supplier = "Solar Philippines";
                String requestDate = dateField.getText().trim();
                String status = "Pending";
                String notes = notesArea.getText().trim();
                
                if (itemName.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please fill in Item Name and Category.",
                        "Incomplete Data",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                
                // Build notes with price info
                String fullNotes = notes;
                if (unitPrice > 0) {
                    double totalPrice = qty * unitPrice;
                    fullNotes += (notes.isEmpty() ? "" : "\n\n") + 
                        String.format("Unit Price: ₱%.2f\nTotal Price: ₱%.2f", unitPrice, totalPrice);
                }
                
                if (stockRequestsTableModel == null) {
                    createStockRequestsPanel();
                }
                
                // Save to database
                if (StockRequestDAO.addRequest(reqId, itemName, category, qty, supplier,
                                               requestDate, status, fullNotes)) {
                    // Reload from database
                    if (stockRequestsTableModel != null) {
                        StockRequestDAO.loadToTableModel(stockRequestsTableModel);
                        updateStockRequestsSummary();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save request to database.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String successMsg = "Stock request submitted to Solar Philippines!\n\n" +
                    "Request ID: " + reqId + "\n" +
                    "Item: " + itemName + "\n" +
                    "Quantity: " + qty;
                
                if (unitPrice > 0) {
                    double totalPrice = qty * unitPrice;
                    successMsg += "\nUnit Price: ₱" + String.format("%.2f", unitPrice) +
                                  "\nTotal Price: ₱" + String.format("%.2f", totalPrice);
                }
                
                JOptionPane.showMessageDialog(
                    this,
                    successMsg,
                    "Request Submitted",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid quantity (number).",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showCard(JPanel contentPanel, String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
        
        // Refresh data when switching panels to ensure latest data is displayed
        refreshPanelData(name);
    }

    /**
     * Refresh data for the specified panel
     */
    private void refreshPanelData(String panelName) {
        try {
            switch (panelName) {
                case "HOME":
                    updateHomePanelStats();
                    break;
                case "INVENTORY":
                    if (inventoryTableModel != null) {
                        InventoryDAO.loadToTableModel(inventoryTableModel);
                        updateInventorySummary();
                    }
                    break;
                case "ORDERS":
                    if (salesTableModel != null) {
                        SalesOrderDAO.loadToTableModel(salesTableModel);
                        updateSalesSummary();
                    }
                    break;
                case "SUPPLIERS":
                    if (suppliersTableModel != null) {
                        SupplierDAO.loadToTableModel(suppliersTableModel);
                        updateSuppliersSummary();
                    }
                    break;
                case "STOCK_REQUESTS":
                    if (stockRequestsTableModel != null) {
                        StockRequestDAO.loadToTableModel(stockRequestsTableModel);
                        updateStockRequestsSummary();
                    }
                    break;
                case "MAINTENANCE":
                    if (maintenanceTableModel != null) {
                        MaintenanceDAO.loadToTableModel(maintenanceTableModel);
                        updateMaintenanceSummary();
                    }
                    break;
                case "TECHNICIAN_APPS":
                    if (technicianApplicationsTableModel != null) {
                        loadTechnicianApplications();
                    }
                    break;
            }
        } catch (Exception e) {
            // Silently ignore
        }
    }

    /**
     * Import sales orders from CSV and save to database
     */
    private void importSalesOrdersFromCSV() {
        // First, import to table model
        if (CSVUtil.importFromCSV(salesTableModel, this)) {
            // Save each row to database
            int rowCount = salesTableModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String orderId = salesTableModel.getValueAt(i, 0).toString();
                    String customerName = salesTableModel.getValueAt(i, 1).toString();
                    String orderDate = salesTableModel.getValueAt(i, 2).toString();
                    String itemPackage = salesTableModel.getValueAt(i, 3).toString();
                    int quantity = Integer.parseInt(salesTableModel.getValueAt(i, 4).toString());
                    double unitPrice = Double.parseDouble(salesTableModel.getValueAt(i, 5).toString());
                    double totalAmount = Double.parseDouble(salesTableModel.getValueAt(i, 6).toString());
                    String status = salesTableModel.getValueAt(i, 7).toString();
                    String paymentMethod = salesTableModel.getValueAt(i, 8).toString();
                    
                    // Try to add, if exists, update instead
                    if (!SalesOrderDAO.addOrder(orderId, customerName, orderDate, itemPackage, 
                                                quantity, unitPrice, totalAmount, status, paymentMethod)) {
                        // If add fails, try update
                        SalesOrderDAO.updateOrder(orderId, customerName, orderDate, itemPackage,
                                                 quantity, unitPrice, totalAmount, status, paymentMethod);
                    }
                    savedCount++;
                } catch (Exception e) {
                    skippedCount++;
                    System.err.println("Error saving row " + i + " to database: " + e.getMessage());
                }
            }
            
            // Reload from database to ensure consistency
            SalesOrderDAO.loadToTableModel(salesTableModel);
            updateSalesSummary();
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Import inventory items from CSV and save to database
     */
    private void importInventoryFromCSV() {
        if (CSVUtil.importFromCSV(inventoryTableModel, this)) {
            int rowCount = inventoryTableModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String itemId = inventoryTableModel.getValueAt(i, 0).toString();
                    String itemName = inventoryTableModel.getValueAt(i, 1).toString();
                    String category = inventoryTableModel.getValueAt(i, 2).toString();
                    String brandModel = inventoryTableModel.getValueAt(i, 3).toString();
                    int quantity = Integer.parseInt(inventoryTableModel.getValueAt(i, 4).toString());
                    double unitPrice = Double.parseDouble(inventoryTableModel.getValueAt(i, 5).toString());
                    int reorderLevel = Integer.parseInt(inventoryTableModel.getValueAt(i, 6).toString());
                    String location = inventoryTableModel.getValueAt(i, 7).toString();
                    String status = inventoryTableModel.getValueAt(i, 8).toString();
                    
                    if (!InventoryDAO.addItem(itemId, itemName, category, brandModel, quantity,
                                             unitPrice, reorderLevel, location, status)) {
                        InventoryDAO.updateItem(itemId, itemName, category, brandModel, quantity,
                                               unitPrice, reorderLevel, location, status);
                    }
                    savedCount++;
                } catch (Exception e) {
                    skippedCount++;
                    System.err.println("Error saving row " + i + " to database: " + e.getMessage());
                }
            }
            
            InventoryDAO.loadToTableModel(inventoryTableModel);
            updateInventorySummary();
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Import suppliers from CSV and save to database
     */
    private void importSuppliersFromCSV() {
        if (CSVUtil.importFromCSV(suppliersTableModel, this)) {
            int rowCount = suppliersTableModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String supplierId = suppliersTableModel.getValueAt(i, 0).toString();
                    String supplierName = suppliersTableModel.getValueAt(i, 1).toString();
                    String contactPerson = suppliersTableModel.getValueAt(i, 2).toString();
                    String contactNumber = suppliersTableModel.getValueAt(i, 3).toString();
                    String email = suppliersTableModel.getValueAt(i, 4).toString();
                    String address = suppliersTableModel.getValueAt(i, 5).toString();
                    String products = suppliersTableModel.getValueAt(i, 6).toString();
                    String status = suppliersTableModel.getValueAt(i, 7).toString();
                    
                    if (!SupplierDAO.addSupplier(supplierId, supplierName, contactPerson, contactNumber,
                                                email, address, products, status)) {
                        SupplierDAO.updateSupplier(supplierId, supplierName, contactPerson, contactNumber,
                                                  email, address, products, status);
                    }
                    savedCount++;
                } catch (Exception e) {
                    skippedCount++;
                    System.err.println("Error saving row " + i + " to database: " + e.getMessage());
                }
            }
            
            SupplierDAO.loadToTableModel(suppliersTableModel);
            updateSuppliersSummary();
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Import maintenance tickets from CSV and save to database
     */
    private void importMaintenanceFromCSV() {
        if (CSVUtil.importFromCSV(maintenanceTableModel, this)) {
            int rowCount = maintenanceTableModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String ticketId = maintenanceTableModel.getValueAt(i, 0).toString();
                    String customerName = maintenanceTableModel.getValueAt(i, 1).toString();
                    String contactNo = maintenanceTableModel.getValueAt(i, 2).toString();
                    String siteAddress = maintenanceTableModel.getValueAt(i, 3).toString();
                    String equipment = maintenanceTableModel.getValueAt(i, 4).toString();
                    String serviceType = maintenanceTableModel.getValueAt(i, 5).toString();
                    String scheduleDate = maintenanceTableModel.getValueAt(i, 6).toString();
                    String technician = maintenanceTableModel.getValueAt(i, 7).toString();
                    String status = maintenanceTableModel.getValueAt(i, 8).toString();
                    String notes = maintenanceTableModel.getValueAt(i, 9).toString();
                    
                    if (!MaintenanceDAO.addMaintenance(ticketId, customerName, contactNo, siteAddress,
                                                       equipment, serviceType, scheduleDate, technician,
                                                       status, notes)) {
                        MaintenanceDAO.updateMaintenance(ticketId, customerName, contactNo, siteAddress,
                                                        equipment, serviceType, scheduleDate, technician,
                                                        status, notes);
                    }
                    savedCount++;
                } catch (Exception e) {
                    skippedCount++;
                    System.err.println("Error saving row " + i + " to database: " + e.getMessage());
                }
            }
            
            MaintenanceDAO.loadToTableModel(maintenanceTableModel);
            updateMaintenanceSummary();
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Import stock requests from CSV and save to database
     */
    private void importStockRequestsFromCSV() {
        if (CSVUtil.importFromCSV(stockRequestsTableModel, this)) {
            int rowCount = stockRequestsTableModel.getRowCount();
            int savedCount = 0;
            int skippedCount = 0;
            
            for (int i = 0; i < rowCount; i++) {
                try {
                    String requestId = stockRequestsTableModel.getValueAt(i, 0).toString();
                    String itemName = stockRequestsTableModel.getValueAt(i, 1).toString();
                    String category = stockRequestsTableModel.getValueAt(i, 2).toString();
                    int quantityRequested = Integer.parseInt(stockRequestsTableModel.getValueAt(i, 3).toString());
                    String supplier = stockRequestsTableModel.getValueAt(i, 4).toString();
                    String requestDate = stockRequestsTableModel.getValueAt(i, 5).toString();
                    String status = stockRequestsTableModel.getValueAt(i, 6).toString();
                    String notes = stockRequestsTableModel.getValueAt(i, 7).toString();
                    
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
            
            StockRequestDAO.loadToTableModel(stockRequestsTableModel);
            updateStockRequestsSummary();
            
            JOptionPane.showMessageDialog(this, 
                "Import completed!\nSaved: " + savedCount + " rows\nSkipped: " + skippedCount + " rows",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}


