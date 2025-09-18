public class HydroGenerator extends EnergySource {
    protected double waterFlowRateLps;

    public HydroGenerator(String name, double capacityKwh, String location, double waterFlowRateLps) {
        super(name, capacityKwh, location);
        this.waterFlowRateLps = waterFlowRateLps;
    }

    public double generateEnergy() {
        return capacityKwh * (waterFlowRateLps / 100);
    }
}
