public class HydroPumpStorageUnit extends HydroGenerator {
    private double pumpingCapacity;
    private double storageTankVolume;

    public HydroPumpStorageUnit(String name, double capacityKwh, String location, double waterFlowRateLps, double pumpingCapacity, double storageTankVolume) {
        super(name, capacityKwh, location, waterFlowRateLps);
        this.pumpingCapacity = pumpingCapacity;
        this.storageTankVolume = storageTankVolume;
    }

    public double generateEnergy() {
        return super.generateEnergy() + pumpingCapacity * 0.5;
    }
}
