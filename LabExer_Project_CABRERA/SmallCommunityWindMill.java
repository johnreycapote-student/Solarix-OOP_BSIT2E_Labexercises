public class SmallCommunityWindMill extends WindTurbine {
    private boolean manualMaintenance;
    private boolean localMaterialsUsed;

    public SmallCommunityWindMill(String name, double capacityKwh, String location, double avgWindSpeedKmh, boolean manualMaintenance, boolean localMaterialsUsed) {
        super(name, capacityKwh, location, avgWindSpeedKmh);
        this.manualMaintenance = manualMaintenance;
        this.localMaterialsUsed = localMaterialsUsed;
    }

    public double generateEnergy() {
        return super.generateEnergy();
    }
}
