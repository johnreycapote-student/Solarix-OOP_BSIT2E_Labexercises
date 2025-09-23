public class FloatingSolarFarm extends SolarPanel {
    private double waterSurfaceArea;
    private double coolingEffect;

    public FloatingSolarFarm(String name, double capacityKwh, String location, double efficiency, double waterSurfaceArea, double coolingEffect) {
        super(name, capacityKwh, location, efficiency);
        this.waterSurfaceArea = waterSurfaceArea;
        this.coolingEffect = coolingEffect;
    }

    public double generateEnergy() {
        return super.generateEnergy() * (1 + coolingEffect);
    }
}
