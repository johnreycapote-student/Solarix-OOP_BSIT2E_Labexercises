public class RunOfRiverMicroHydro extends HydroGenerator {
    private double channelLength;
    private String environmentalImpact;

    public RunOfRiverMicroHydro(String name, double capacityKwh, String location, double waterFlowRateLps, double channelLength, String environmentalImpact) {
        super(name, capacityKwh, location, waterFlowRateLps);
        this.channelLength = channelLength;
        this.environmentalImpact = environmentalImpact;
    }

    public double generateEnergy() {
        return super.generateEnergy();
    }
}
