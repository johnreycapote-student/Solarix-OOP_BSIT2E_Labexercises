public class SolarPanel extends EnergySource {
    private double efficiency;

    public SolarPanel(String name, double capacityKwh, String location, double efficiency) {
        super(name, capacityKwh, location);
        this.efficiency = efficiency;
    }

    public double generateEnergy() {
        return getCapacityKwh() * efficiency;
    }
}
