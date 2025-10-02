public class PicoHydroGenerator extends HydroGenerator {
    private double streamWidth;
    private double headHeight;

    public PicoHydroGenerator(String name, double capacityKwh, String location, double waterFlowRateLps, double streamWidth, double headHeight) {
        super(name, capacityKwh, location, waterFlowRateLps);
        this.streamWidth = streamWidth;
        this.headHeight = headHeight;
    }

    public double generateEnergy() {
        return super.generateEnergy() * headHeight;
    }
}
