public class EnergySourceTester {
    public static void main(String[] args) {

        // Superclass
        EnergySource source = new EnergySource("Generic Source", 100, "Unknown");
        System.out.println(source.name + " generates: " + source.generateEnergy() + " kWh");

        // Solar
        SolarPanel solar = new SolarPanel("Basic Solar", 150, "City A", 0.85);
        System.out.println(solar.name + " generates: " + solar.generateEnergy() + " kWh");

        RooftopSolarPanel rooftop = new RooftopSolarPanel("Home Solar", 200, "City B", 0.8, 30, 0.1);
        System.out.println(rooftop.name + " generates: " + rooftop.generateEnergy() + " kWh");

        SolarCanopy canopy = new SolarCanopy("Parking Lot Canopy", 500, "City C", 0.9, 200, 50);
        System.out.println(canopy.name + " generates: " + canopy.generateEnergy() + " kWh");

        FloatingSolarFarm floating = new FloatingSolarFarm("Lake Solar", 1000, "Lake X", 0.85, 1000, 0.2);
        System.out.println(floating.name + " generates: " + floating.generateEnergy() + " kWh");

        // Wind
        WindTurbine wind = new WindTurbine("Basic Wind", 400, "Hilltop", 40);
        System.out.println(wind.name + " generates: " + wind.generateEnergy() + " kWh");

        UrbanVerticalAxisWindTurbine vawt = new UrbanVerticalAxisWindTurbine("City VAWT", 300, "Urban Center", 30, 45, 8);
        System.out.println(vawt.name + " generates: " + vawt.generateEnergy() + " kWh");

        SmallCommunityWindMill windmill = new SmallCommunityWindMill("Village Windmill", 100, "Rural Area", 25, true, true);
        System.out.println(windmill.name + " generates: " + windmill.generateEnergy() + " kWh");

        OffshoreWindTurbine offshore = new OffshoreWindTurbine("Sea Turbine", 1500, "Coastline", 60, "Monopile", 12);
        System.out.println(offshore.name + " generates: " + offshore.generateEnergy() + " kWh");

        // Hydro
        HydroGenerator hydro = new HydroGenerator("Basic Hydro", 600, "River Valley", 30);
        System.out.println(hydro.name + " generates: " + hydro.generateEnergy() + " kWh");

        PicoHydroGenerator pico = new PicoHydroGenerator("Stream Hydro", 50, "Mountain Stream", 20, 2, 5);
        System.out.println(pico.name + " generates: " + pico.generateEnergy() + " kWh");

        HydroPumpStorageUnit pumpStorage = new HydroPumpStorageUnit("Reservoir Unit", 800, "Valley", 40, 300, 5000);
        System.out.println(pumpStorage.name + " generates: " + pumpStorage.generateEnergy() + " kWh");

        RunOfRiverMicroHydro river = new RunOfRiverMicroHydro("River Flow Plant", 600, "River Delta", 35, 5.5, "Low Impact");
        System.out.println(river.name + " generates: " + river.generateEnergy() + " kWh");

    }
}
