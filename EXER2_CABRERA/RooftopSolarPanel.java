public class RooftopSolarPanel extends SolarPanel {
    private double roofTiltAngle;
    private double shadingFactor;

    public RooftopSolarPanel(String name, double capacityKwh, String location, double efficiency, double roofTiltAngle, double shadingFactor) {
        super(name, capacityKwh, location, efficiency);
        this.roofTiltAngle = roofTiltAngle;
        this.shadingFactor = shadingFactor;
    }

    public double generateEnergy() {
        return super.generateEnergy() * (1 - shadingFactor);
    }
}
