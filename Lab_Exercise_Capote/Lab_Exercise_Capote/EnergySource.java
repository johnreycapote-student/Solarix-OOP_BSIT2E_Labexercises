public class EnergySource {
    private String name;
    private double capacityKwh;
    private String location;

    public EnergySource(String name, double capacityKwh, String location) {
        this.name = name;
        this.capacityKwh = capacityKwh;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public double getCapacityKwh() {
        return capacityKwh;
    }

    public String getLocation() {
        return location;
    }

    public double generateEnergy() {
        return capacityKwh;
    }
}
