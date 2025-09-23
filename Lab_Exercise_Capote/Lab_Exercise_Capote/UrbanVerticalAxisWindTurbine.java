public class UrbanVerticalAxisWindTurbine extends WindTurbine {
    private double noiseLevel;
    private int birdSafetyRating;

    public UrbanVerticalAxisWindTurbine(String name, double capacityKwh, String location, double avgWindSpeedKmh, double noiseLevel, int birdSafetyRating) {
        super(name, capacityKwh, location, avgWindSpeedKmh);
        this.noiseLevel = noiseLevel;
        this.birdSafetyRating = birdSafetyRating;
    }

    public double generateEnergy() {
        return super.generateEnergy() * 0.9;
    }
}
