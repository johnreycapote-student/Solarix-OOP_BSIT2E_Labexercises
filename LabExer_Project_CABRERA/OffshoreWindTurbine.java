public class OffshoreWindTurbine extends WindTurbine {
    private String foundationType;
    private int maintenanceInterval;

    public OffshoreWindTurbine(String name, double capacityKwh, String location, double avgWindSpeedKmh, String foundationType, int maintenanceInterval) {
        super(name, capacityKwh, location, avgWindSpeedKmh);
        this.foundationType = foundationType;
        this.maintenanceInterval = maintenanceInterval;
    }

    public double generateEnergy() {
        return super.generateEnergy() * 1.2;
    }
}
