public class EnergySourceTester {
    public static void main(String[] args) {

        // Superclass
        EnergySource source = new EnergySource("Generic Source", 100, "Unknown");
        System.out.println(source.getName() + " generates: " + source.generateEnergy() + " kWh\n");

        // Solar
        SolarPanel solar = new SolarPanel("Basic Solar", 150, "City A", 0.85);  
        System.out.println(solar.getName() + " generates: " + solar.generateEnergy() + " kWh\n");

        RooftopSolarPanel rooftop = new RooftopSolarPanel("Home Solar", 200, "City B", 0.8, 30, 0.1);
        System.out.println(rooftop.getName() + " generates: " + rooftop.generateEnergy() + " kWh\n");

        SolarCanopy canopy = new SolarCanopy("Parking Lot Canopy", 500, "City C", 0.9, 200, 50);
        System.out.println(canopy.getName() + " generates: " + canopy.generateEnergy() + " kWh\n");

        FloatingSolarFarm floating = new FloatingSolarFarm("Lake Solar", 1000, "Lake X", 0.85, 1000, 0.2);
        System.out.println(floating.getName() + " generates: " + floating.generateEnergy() + " kWh\n");

        // Wind
        WindTurbine wind = new WindTurbine("Basic Wind", 400, "Hilltop", 40);
        System.out.println(wind.getName() + " generates: " + wind.generateEnergy() + " kWh\n");

        UrbanVerticalAxisWindTurbine vawt = new UrbanVerticalAxisWindTurbine("City VAWT", 300, "Urban Center", 30, 45, 8);
        System.out.println(vawt.getName() + " generates: " + vawt.generateEnergy() + " kWh\n");

        SmallCommunityWindMill windmill = new SmallCommunityWindMill("Village Windmill", 100, "Rural Area", 25, true, true);
        System.out.println(windmill.getName() + " generates: " + windmill.generateEnergy() + " kWh\n");

        OffshoreWindTurbine offshore = new OffshoreWindTurbine("Sea Turbine", 1500, "Coastline", 60, "Monopile", 12);
        System.out.println(offshore.getName() + " generates: " + offshore.generateEnergy() + " kWh\n");

        // Hydro
        HydroGenerator hydro = new HydroGenerator("Basic Hydro", 600, "River Valley", 30);
        System.out.println(hydro.getName() + " generates: " + hydro.generateEnergy() + " kWh\n");

        PicoHydroGenerator pico = new PicoHydroGenerator("Stream Hydro", 50, "Mountain Stream", 20, 2, 5);
        System.out.println(pico.getName() + " generates: " + pico.generateEnergy() + " kWh\n");

        HydroPumpStorageUnit pumpStorage = new HydroPumpStorageUnit("Reservoir Unit", 800, "Valley", 40, 300, 5000);
        System.out.println(pumpStorage.getName() + " generates: " + pumpStorage.generateEnergy() + " kWh\n");

        RunOfRiverMicroHydro river = new RunOfRiverMicroHydro("River Flow Plant", 600, "River Delta", 35, 5.5, "Low Impact");
        System.out.println(river.getName() + " generates: " + river.generateEnergy() + " kWh\n");
    }
}
