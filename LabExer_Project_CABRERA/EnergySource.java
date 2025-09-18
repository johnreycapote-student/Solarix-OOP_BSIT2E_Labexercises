public class EnergySource {
    protected String name;
    protected double capacityKwh;
    protected String location;
    protected boolean isActive;

    public EnergySource(String name, double capacityKwh, String location) {
        this.name = name;
        this.capacityKwh = capacityKwh;
        this.location = location;
        this.isActive = false;
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }

    public String getStatus() {
        return isActive ? "Active" : "Inactive";
    }

    public double generateEnergy() {
        return capacityKwh;
    }
}
