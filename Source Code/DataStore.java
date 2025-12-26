
import javax.swing.table.DefaultTableModel;

/**
 * Shared singleton data store. Provides DefaultTableModel instances used by
 * DashboardFrame and CompanyAdminFrame so updates are visible in both.
 */
public final class DataStore {
    private static DataStore instance;

    private final DefaultTableModel stockRequestModel;
    private final DefaultTableModel scheduleModel;
    private final DefaultTableModel stockArrivalModel;

    private int requestCounter = 3;
    private int scheduleCounter = 4;
    private int arrivalCounter = 2;

    private DataStore() {
        String[] reqCols = {
                "Request ID", "Item Name", "Category", "Quantity Requested",
                "Supplier", "Request Date", "Status", "Notes"
        };
        stockRequestModel = new DefaultTableModel(reqCols, 0);
        stockRequestModel.addRow(new Object[]{
                "REQ-001", "Solar Panel 450W", "Solar Panel", 50,
                "SunTech Philippines", "2025-12-01", "Pending", "Urgent restock needed"
        });
        stockRequestModel.addRow(new Object[]{
                "REQ-002", "Battery 200Ah", "Battery", 20,
                "EcoPower Supply", "2025-12-02", "Approved", "For warehouse A"
        });
        // Additional sample requests for testing
        stockRequestModel.addRow(new Object[]{"REQ-003", "Inverter 5kW", "Inverter", 10, "GridMasters", "2025-12-02", "Pending", "For new installer"});
        stockRequestModel.addRow(new Object[]{"REQ-004", "Charge Controller 60A", "Controller", 15, "PowerControls", "2025-12-03", "Pending", "Check compatibility"});
        stockRequestModel.addRow(new Object[]{"REQ-005", "Cabling Kit 10m", "Accessories", 100, "CablePro Inc.", "2025-12-03", "Pending", "Standard kit"});
        stockRequestModel.addRow(new Object[]{"REQ-006", "Mounting Rails", "Mounting", 200, "MountTech", "2025-12-04", "Approved", "For rooftop units"});

        String[] schCols = {
                "Schedule ID", "Type", "Item/Service", "Supplier/Customer",
                "Scheduled Date", "Time", "Status", "Notes"
        };
        scheduleModel = new DefaultTableModel(schCols, 0);
        scheduleModel.addRow(new Object[]{
                "SCH-001", "Delivery", "Solar Panel 450W (50 units)", "SunTech Philippines",
                "2025-12-05", "10:00 AM", "Scheduled", "Warehouse A"
        });

        String[] arrCols = {
                "Arrival ID", "Item Name", "Category", "Quantity Received",
                "Supplier", "Arrival Date", "Location", "Status"
        };
        stockArrivalModel = new DefaultTableModel(arrCols, 0);
        stockArrivalModel.addRow(new Object[]{
                "ARR-001", "Solar Panel 450W", "Solar Panel", 20,
                "SunTech Philippines", "2025-12-04", "Warehouse A", "Received"
        });
        // Additional sample arrivals
        stockArrivalModel.addRow(new Object[]{"ARR-002", "Battery 200Ah", "Battery", 10, "EcoPower Supply", "2025-12-05", "Warehouse B", "Received"});
        stockArrivalModel.addRow(new Object[]{"ARR-003", "Inverter 5kW", "Inverter", 5, "GridMasters", "2025-12-06", "Warehouse A", "Pending"});
        stockArrivalModel.addRow(new Object[]{"ARR-004", "Mounting Rails", "Mounting", 50, "MountTech", "2025-12-05", "Warehouse C", "Received"});
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public DefaultTableModel getStockRequestModel() {
        return stockRequestModel;
    }

    public DefaultTableModel getScheduleModel() {
        return scheduleModel;
    }

    public DefaultTableModel getStockArrivalModel() {
        return stockArrivalModel;
    }

    public synchronized String nextRequestId() {
        return String.format("REQ-%03d", requestCounter++);
    }

    public synchronized String nextScheduleId() {
        return String.format("SCH-%03d", scheduleCounter++);
    }

    public synchronized String nextArrivalId() {
        return String.format("ARR-%03d", arrivalCounter++);
    }
}

