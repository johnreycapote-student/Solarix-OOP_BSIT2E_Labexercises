public class WindTurbine extends EnergySource {
    private double avgWindSpeedKmh;

    public WindTurbine(String name, double capacityKwh, String location, double avgWindSpeedKmh) {
        super(name, capacityKwh, location);
        this.avgWindSpeedKmh = avgWindSpeedKmh;
    }

    public double generateEnergy() {
        return getCapacityKwh() * (avgWindSpeedKmh / 100);
    }
}
