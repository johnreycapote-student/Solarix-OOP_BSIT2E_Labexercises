public class SolarCanopy extends SolarPanel {
    private double coverageArea;
    private int connectedDevices;

    public SolarCanopy(String name, double capacityKwh, String location, double efficiency, double coverageArea, int connectedDevices) {
        super(name, capacityKwh, location, efficiency);
        this.coverageArea = coverageArea;
        this.connectedDevices = connectedDevices;
    }

    public double generateEnergy() {
        return super.generateEnergy() + coverageArea * 0.1;
    }
}
