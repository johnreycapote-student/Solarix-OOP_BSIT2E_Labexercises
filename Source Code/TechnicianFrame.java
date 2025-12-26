import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import util.CSVUtil;
import util.DatabaseConnection;
import util.TechnicianJobDAO;
import util.PartsRequestDAO;
import util.TechnicianIssueDAO;
import util.CustomerIssueDAO;
import util.MaintenanceDAO;
import util.TechnicianDAO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.SwingUtilities;

/**
 * Main technician portal window.
 * Provides:
 * - My Jobs (with simple checklist dialog)
 * - Parts & Requests (request extra parts)
 * - Issues (fault reporting)
 *
 * This mirrors the style of CompanyAdminFrame / DashboardFrame but is focused
 * on technician tasks only. Data is sample-only for now.
 */
public class TechnicianFrame extends JFrame {

    // Static reference for notifications (map email to instance)
    private static TechnicianFrame currentInstance;
    private static java.util.Map<String, TechnicianFrame> technicianInstances = new java.util.HashMap<>();
    
    private final String technicianEmail;
    private final String technicianName;

    private DefaultTableModel jobsTableModel;
    private DefaultTableModel partsTableModel;
    private DefaultTableModel issuesTableModel;
    private DefaultTableModel customerIssuesTableModel;

    public TechnicianFrame(String technicianEmail) {
        this.technicianEmail = technicianEmail;
        this.technicianName = TechnicianDAO.getTechnicianName(technicianEmail);
        currentInstance = this; // Track current instance
        technicianInstances.put(technicianEmail, this); // Track by email

        setTitle("Technician Portal - Renewable Energy Hardware");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        initComponents();
    }
    
    /**
     * Get current TechnicianFrame instance
     */
    public static TechnicianFrame getCurrentInstance() {
        return currentInstance;
    }
    
    /**
     * Get TechnicianFrame instance by email
     */
    public static TechnicianFrame getInstanceByEmail(String email) {
        return technicianInstances.get(email);
    }
    
    /**
     * Get technician email
     */
    public String getTechnicianEmail() {
        return technicianEmail;
    }
    
    /**
     * Notify technician of new job assignment (called from DashboardFrame)
     */
    public void notifyNewJob(String jobId, String customer, String serviceType, String scheduleDate) {
        if (jobsTableModel != null) {
            // Reload jobs from database to get the new job
            try {
                if (DatabaseConnection.testConnection()) {
                    TechnicianJobDAO.loadToTableModel(jobsTableModel, technicianEmail);
                    
                    // Show notification to user
                    JOptionPane.showMessageDialog(this,
                        "New job assigned!\n\n" +
                        "Job ID: " + jobId + "\n" +
                        "Customer: " + customer + "\n" +
                        "Service: " + serviceType + "\n" +
                        "Schedule: " + scheduleDate,
                        "New Job Assignment",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    System.out.println("✓ Technician " + technicianEmail + " notified of new job: " + jobId);
                }
            } catch (Exception e) {
                System.err.println("Error refreshing technician jobs: " + e.getMessage());
                e.printStackTrace();
            }
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
        String displayName = technicianName != null && !technicianName.isEmpty() ? technicianName : technicianEmail;
        JLabel welcomeLabel = new JLabel("Welcome, " + displayName + "!");
        welcomeLabel.setForeground(white);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setName("welcomeLabel"); // For responsive updates

        JLabel title = new JLabel("Technician Portal");
        title.setForeground(white);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setName("titleLabel");

        JLabel subtitle = new JLabel("View jobs, update checklists, request parts, and report issues");
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

        String userDisplayText = technicianName != null && !technicianName.isEmpty() 
            ? technicianName + " (" + technicianEmail + ")" 
            : technicianEmail;
        JLabel userLabel = new JLabel("Logged in as: " + userDisplayText);
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
        topRight.add(Box.createVerticalStrut(4));
        topRight.add(logoutButton);

        topBar.add(topLeft, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);

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

        // Left navigation (responsive - can be hidden on small screens)
        JPanel navPanel = new JPanel();
        navPanel.setBackground(softOrange);
        navPanel.setPreferredSize(new Dimension(220, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        navPanel.setName("navPanel");

        JLabel menuLabel = new JLabel("Technician Menu");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuLabel.setForeground(new Color(80, 80, 80));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(menuLabel);
        navPanel.add(Box.createVerticalStrut(10));

        JButton jobsButton = createNavButton("My Jobs");
        JButton partsButton = createNavButton("Parts & Requests");
        JButton issuesButton = createNavButton("My Issues / Faults");
        JButton customerIssuesButton = createNavButton("Customer Issues");

        navPanel.add(jobsButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(partsButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(issuesButton);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(customerIssuesButton);

        // Center content
        JPanel contentPanel = new JPanel(new CardLayout());

        JPanel jobsPanel = createJobsPanel();
        JPanel partsPanel = createPartsPanel();
        JPanel issuesPanel = createIssuesPanel();
        JPanel customerIssuesPanel = createCustomerIssuesPanel();

        contentPanel.add(jobsPanel, "JOBS");
        contentPanel.add(partsPanel, "PARTS");
        contentPanel.add(issuesPanel, "ISSUES");
        contentPanel.add(customerIssuesPanel, "CUSTOMER_ISSUES");

        jobsButton.addActionListener(e -> showCard(contentPanel, "JOBS"));
        partsButton.addActionListener(e -> showCard(contentPanel, "PARTS"));
        issuesButton.addActionListener(e -> showCard(contentPanel, "ISSUES"));
        customerIssuesButton.addActionListener(e -> showCard(contentPanel, "CUSTOMER_ISSUES"));

        root.add(topBar, BorderLayout.NORTH);
        root.add(navPanel, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        
        // Add responsive layout listener - triggers on every resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
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
     * My Jobs panel – list of assigned jobs with actions.
     */
    private JPanel createJobsPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("My Jobs", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>View today's assigned jobs, open checklists, and mark work as completed.</div></html>",
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

        String[] cols = {
                "Job ID", "Customer", "Address", "Type", "Date", "Time", "Status"
        };
        jobsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data from database
        try {
            if (DatabaseConnection.testConnection()) {
                TechnicianJobDAO.loadToTableModel(jobsTableModel, technicianEmail);
            }
        } catch (Exception e) {
            System.err.println("Error loading technician jobs: " + e.getMessage());
        }

        JTable table = new JTable(jobsTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton viewButton = new JButton("View / Checklist");
        JButton completeButton = new JButton("Mark Complete");
        JButton issueButton = new JButton("Report Issue");

        viewButton.setBackground(primaryOrange);
        viewButton.setForeground(Color.BLACK);
        viewButton.setFocusPainted(false);

        completeButton.setBackground(Color.WHITE);
        completeButton.setForeground(primaryOrange);
        completeButton.setFocusPainted(false);

        issueButton.setBackground(Color.WHITE);
        issueButton.setForeground(new Color(0, 120, 200));
        issueButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(viewButton);
        buttonBar.add(completeButton);
        buttonBar.add(issueButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(jobsTableModel, "technician_jobs.csv", this));
        importButton.addActionListener(e -> CSVUtil.importFromCSV(jobsTableModel, this));

        viewButton.addActionListener(e -> openChecklistDialog(panel, table));
        completeButton.addActionListener(e -> markJobComplete(table));
        issueButton.addActionListener(e -> openIssueDialogFromJob(panel, table));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    private void openChecklistDialog(Component parent, JTable jobsTable) {
        int row = jobsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a job first.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String jobId = jobsTable.getValueAt(row, 0).toString();
        String customer = jobsTable.getValueAt(row, 1).toString();

        JCheckBox preCheck = new JCheckBox("Pre-check completed (tools, PPE, site readiness)");
        JCheckBox safety = new JCheckBox("Safety checks done (isolation, lock-out, signage)");
        JCheckBox install = new JCheckBox("Installation / maintenance steps completed");
        JCheckBox testing = new JCheckBox("Testing & verification completed (readings recorded)");
        JCheckBox handover = new JCheckBox("Customer handover & explanation done");

        JTextArea notesArea = new JTextArea(4, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        form.add(new JLabel("Job: " + jobId + " - " + customer), gbc);
        gbc.gridy++;
        form.add(preCheck, gbc);
        gbc.gridy++;
        form.add(safety, gbc);
        gbc.gridy++;
        form.add(install, gbc);
        gbc.gridy++;
        form.add(testing, gbc);
        gbc.gridy++;
        form.add(handover, gbc);
        gbc.gridy++;
        form.add(new JLabel("Technician notes:"), gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        form.add(new JScrollPane(notesArea), gbc);

        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(parent),
                form,
                "Job Checklist",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            boolean allCompleted = preCheck.isSelected() && safety.isSelected() && install.isSelected()
                    && testing.isSelected() && handover.isSelected();
            
            // Update checklist in database
            if (DatabaseConnection.testConnection()) {
                TechnicianJobDAO.updateChecklistCompleted(jobId, allCompleted);
            }
            
            if (!allCompleted) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Some checklist steps are not ticked.\n" +
                                "You can still save notes, but please complete all steps before final completion.",
                        "Checklist Incomplete",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        parent,
                        "Checklist saved for " + jobId + ".",
                        "Checklist",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    private void markJobComplete(JTable jobsTable) {
        int row = jobsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job to mark as complete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Mark selected job as completed?",
                "Confirm Completion",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            String jobId = (String) jobsTableModel.getValueAt(row, 0);
            String customerName = (String) jobsTableModel.getValueAt(row, 1);
            
            if (DatabaseConnection.testConnection()) {
                if (TechnicianJobDAO.updateJobStatus(jobId, "Completed")) {
                    TechnicianJobDAO.loadToTableModel(jobsTableModel, technicianEmail);
                    
                    // Find maintenance ticket by job ID (jobId format: JOB-MT-xxxxx)
                    // Extract ticket ID from job ID
                    String ticketId = jobId.replace("JOB-", "MT-");
                    
                    // Update maintenance status to "Completed"
                    if (MaintenanceDAO.updateMaintenance(ticketId, customerName, "", "", "",
                            "", "", technicianEmail, "Completed", "Job completed by technician")) {
                        
                        // Notify customer in real-time
                        // Find customer portal by customer name
                        for (CustomerPortal portal : CustomerPortal.customerInstances.values()) {
                            if (portal.getCustomerName() != null && portal.getCustomerName().equals(customerName)) {
                                portal.notifyMaintenanceStatusUpdate(ticketId, "Completed");
                                break;
                            }
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this, "Job marked as completed.", "Job Completed",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update job status.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Parts & Requests panel – simple parts list with ability to request extras.
     */
    private JPanel createPartsPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Parts & Requests", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>View parts assigned to your jobs and request additional parts from the warehouse/admin.</div></html>",
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

        String[] cols = {
                "Request ID", "Part Name", "Quantity", "Request Date", "Status"
        };
        partsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data from database
        try {
            if (DatabaseConnection.testConnection()) {
                PartsRequestDAO.loadToTableModel(partsTableModel, technicianEmail);
            }
        } catch (Exception e) {
            System.err.println("Error loading parts requests: " + e.getMessage());
        }

        JTable table = new JTable(partsTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton requestButton = new JButton("Request Extra Part");
        requestButton.setBackground(primaryOrange);
        requestButton.setForeground(Color.BLACK);
        requestButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(requestButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(partsTableModel, "parts_requests.csv", this));
        importButton.addActionListener(e -> CSVUtil.importFromCSV(partsTableModel, this));

        requestButton.addActionListener(e -> openPartRequestDialog(panel));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    private void openPartRequestDialog(Component parent) {
        JTextField partField = new JTextField();
        JTextField jobField = new JTextField();
        JTextField qtyField = new JTextField();
        JComboBox<String> urgencyCombo = new JComboBox<>(new String[]{"Normal", "Urgent"});
        JTextArea reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        form.add(new JLabel("Part Name:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Job ID:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Quantity:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Urgency:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Reason:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        form.add(partField, gbc);
        gbc.gridy++;
        form.add(jobField, gbc);
        gbc.gridy++;
        form.add(qtyField, gbc);
        gbc.gridy++;
        form.add(urgencyCombo, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        form.add(new JScrollPane(reasonArea), gbc);

        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(parent),
                form,
                "Request Extra Part",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String part = partField.getText().trim();
            String jobId = jobField.getText().trim();
            String qtyText = qtyField.getText().trim();
            String urgency = (String) urgencyCombo.getSelectedItem();
            String reason = reasonArea.getText().trim();

            if (part.isEmpty() || jobId.isEmpty() || qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Please fill in Part, Job ID, and Quantity.",
                        "Incomplete Data",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                int qty = Integer.parseInt(qtyText);
                String requestId = "PART-" + String.format("%03d", partsTableModel.getRowCount() + 1);
                String requestDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String status = "Pending";
                
                // Combine urgency with reason/notes
                String notes = reason;
                if (urgency != null && !urgency.isEmpty()) {
                    if (notes.isEmpty()) {
                        notes = "Urgency: " + urgency;
                    } else {
                        notes = "Urgency: " + urgency + "\n" + notes;
                    }
                }
                
                // Save to database
                if (DatabaseConnection.testConnection()) {
                    if (PartsRequestDAO.addRequest(requestId, technicianEmail, part, qty, requestDate, status, notes)) {
                        // Reload from database
                        PartsRequestDAO.loadToTableModel(partsTableModel, technicianEmail);
                        
                        String message = "Part request submitted successfully!\n\n";
                        message += "Request ID: " + requestId + "\n";
                        message += "Part: " + part + "\n";
                        message += "Quantity: " + qty + "\n";
                        message += "Urgency: " + urgency + "\n";
                        if (!reason.isEmpty()) {
                            message += "Reason: " + reason + "\n";
                        }
                        
                        JOptionPane.showMessageDialog(
                                parent,
                                message,
                                "Request Submitted",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                parent,
                                "Failed to save request to database.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Quantity must be a number.",
                        "Invalid Quantity",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Issues / Faults panel – list of issues + button to create new issue.
     */
    private JPanel createIssuesPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Issues / Fault Reports", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>Record equipment issues found on site and send them back to admin/maintenance.</div></html>",
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

        String[] cols = {
                "Issue ID", "Job ID", "Description", "Severity", "Reported Date", "Status"
        };
        issuesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data from database
        try {
            if (DatabaseConnection.testConnection()) {
                TechnicianIssueDAO.loadToTableModel(issuesTableModel, technicianEmail);
            }
        } catch (Exception e) {
            System.err.println("Error loading technician issues: " + e.getMessage());
        }

        JTable table = new JTable(issuesTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton newIssueButton = new JButton("New Issue / Fault");
        newIssueButton.setBackground(primaryOrange);
        newIssueButton.setForeground(Color.BLACK);
        newIssueButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        
        exportButton.setBackground(new Color(0, 120, 200));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(0, 150, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
        buttonBar.add(newIssueButton);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(exportButton);
        buttonBar.add(importButton);
        
        // CSV Export/Import actions
        exportButton.addActionListener(e -> CSVUtil.exportToCSV(issuesTableModel, "technician_issues.csv", this));
        importButton.addActionListener(e -> CSVUtil.importFromCSV(issuesTableModel, this));

        newIssueButton.addActionListener(e -> openIssueDialog(panel, null, ""));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    private void openIssueDialogFromJob(Component parent, JTable jobsTable) {
        int row = jobsTable.getSelectedRow();
        String prefillJobId = "";
        if (row != -1) {
            prefillJobId = jobsTable.getValueAt(row, 0).toString();
        }
        openIssueDialog(parent, null, prefillJobId);
    }

    private void openIssueDialog(Component parent, JTable issueTable, String prefillJobId) {
        JTextField issueJobField = new JTextField(prefillJobId != null ? prefillJobId : "");
        JTextField equipmentField = new JTextField();
        JComboBox<String> severityCombo = new JComboBox<>(new String[]{"Low", "Medium", "Critical"});
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        form.add(new JLabel("Job ID:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Equipment:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Severity:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        form.add(issueJobField, gbc);
        gbc.gridy++;
        form.add(equipmentField, gbc);
        gbc.gridy++;
        form.add(severityCombo, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        form.add(new JScrollPane(descArea), gbc);

        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(parent),
                form,
                "New Issue / Fault Report",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String jobId = issueJobField.getText().trim();
            String equipment = equipmentField.getText().trim();
            String severity = (String) severityCombo.getSelectedItem();
            String desc = descArea.getText().trim();

            if (jobId.isEmpty() || equipment.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Please fill in Job ID, Equipment, and Description.",
                        "Incomplete Data",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String issueId = "ISS-" + String.format("%03d", issuesTableModel.getRowCount() + 1);
            String reportedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String status = "Open";

            // Save to database
            if (DatabaseConnection.testConnection()) {
                if (TechnicianIssueDAO.addIssue(issueId, technicianEmail, jobId, desc, severity, reportedDate, status)) {
                    // Reload from database
                    TechnicianIssueDAO.loadToTableModel(issuesTableModel, technicianEmail);
                    
                    JOptionPane.showMessageDialog(
                            parent,
                            "Issue recorded successfully!\n\nIssue ID: " + issueId + "\nThis has been sent to admin for review.",
                            "Issue Saved",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            parent,
                            "Failed to save issue to database.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * Customer Issues panel – view and fix customer-reported issues
     */
    private JPanel createCustomerIssuesPanel() {
        Color white = Color.WHITE;
        Color primaryOrange = new Color(255, 140, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Customer Reported Issues", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(primaryOrange);

        JLabel description = new JLabel(
                "<html><div style='text-align: center;'>View issues reported by customers. Assign yourself and update status when fixed.</div></html>",
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

        String[] cols = {
                "Issue ID", "Customer", "Equipment", "Description", "Severity", "Reported Date", "Status", "Assigned To"
        };
        customerIssuesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load all customer issues
        refreshCustomerIssues();

        JTable table = new JTable(customerIssuesTableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton assignButton = new JButton("Assign to Me");
        JButton fixButton = new JButton("Mark as Fixed");
        
        assignButton.setBackground(primaryOrange);
        assignButton.setForeground(Color.BLACK);
        assignButton.setFocusPainted(false);
        
        fixButton.setBackground(new Color(0, 150, 0));
        fixButton.setForeground(Color.WHITE);
        fixButton.setFocusPainted(false);

        buttonBar.add(assignButton);
        buttonBar.add(fixButton);

        // Actions
        assignButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an issue to assign.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String issueId = customerIssuesTableModel.getValueAt(row, 0).toString();
            if (CustomerIssueDAO.updateIssue(issueId, "In Progress", technicianEmail, null, null)) {
                refreshCustomerIssues();
                JOptionPane.showMessageDialog(this, "Issue assigned to you successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign issue.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        fixButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an issue to mark as fixed.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String issueId = customerIssuesTableModel.getValueAt(row, 0).toString();
            String resolution = JOptionPane.showInputDialog(this, "Enter resolution details:", "Resolution");
            
            if (resolution != null && !resolution.trim().isEmpty()) {
                String resolvedDate = LocalDate.now().toString();
                if (CustomerIssueDAO.updateIssue(issueId, "Resolved", technicianEmail, resolution, resolvedDate)) {
                    refreshCustomerIssues();
                    JOptionPane.showMessageDialog(this, "Issue marked as resolved!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update issue.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonBar, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshCustomerIssues() {
        if (customerIssuesTableModel != null) {
            try {
                if (DatabaseConnection.testConnection()) {
                    // Load all customer issues
                    DefaultTableModel allIssues = new DefaultTableModel(new String[]{
                        "Issue ID", "Customer Email", "Equipment", "Description", "Severity", "Reported Date", "Status", "Assigned Technician"
                    }, 0);
                    CustomerIssueDAO.loadAllToTableModel(allIssues);
                    
                    // Convert to display format
                    customerIssuesTableModel.setRowCount(0);
                    for (int i = 0; i < allIssues.getRowCount(); i++) {
                        Object[] row = {
                            allIssues.getValueAt(i, 0), // Issue ID
                            allIssues.getValueAt(i, 1), // Customer Email
                            allIssues.getValueAt(i, 2), // Equipment
                            allIssues.getValueAt(i, 3), // Description
                            allIssues.getValueAt(i, 4), // Severity
                            allIssues.getValueAt(i, 5), // Reported Date
                            allIssues.getValueAt(i, 6), // Status
                            allIssues.getValueAt(i, 7)  // Assigned Technician
                        };
                        customerIssuesTableModel.addRow(row);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading customer issues: " + e.getMessage());
            }
        }
    }

    private void showCard(JPanel contentPanel, String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
        
        // Refresh data when switching panels
        if ("CUSTOMER_ISSUES".equals(name)) {
            refreshCustomerIssues();
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
        JLabel welcomeLabel = labels[0];
        JLabel titleLabel = labels[1];
        JLabel subtitleLabel = labels[2];
        
        // Update welcome label font size
        if (welcomeLabel != null) {
            if (isMobile) {
                welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            } else {
                welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            }
        }
        
        // Update title font size
        if (titleLabel != null) {
            if (isMobile) {
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            } else {
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            }
        }
        
        // Hide/show subtitle on mobile
        if (subtitleLabel != null) {
            subtitleLabel.setVisible(!isMobile);
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


