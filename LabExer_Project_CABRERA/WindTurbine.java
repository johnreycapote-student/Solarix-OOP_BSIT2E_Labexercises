public class WindTurbine extends EnergySource {
    protected double avgWindSpeedKmh;

    public WindTurbine(String name, double capacityKwh, String location, double avgWindSpeedKmh) {
        super(name, capacityKwh, location);
        this.avgWindSpeedKmh = avgWindSpeedKmh;
    }

    public double generateEnergy() {
        return capacityKwh * (avgWindSpeedKmh / 100);
    }
}
