public class SolarPanel extends EnergySource {
    protected double efficiency;

    public SolarPanel(String name, double capacityKwh, String location, double efficiency) {
        super(name, capacityKwh, location);
        this.efficiency = efficiency;
    }

    public double generateEnergy() {
        return capacityKwh * efficiency;
    }
}
