# Solarix OOP_Project
#Project Features


<<entity>> 
    User 
 -name : String 
 -phone : String 
 -address : String 
 -energyType : String  
 +register() 
 +updateProfile() 
 +viewUsage() 


"<<entity>>       
EnergySystem  "
"-id: int
-type : String
-status : String 
-powerOutput : double

+install ()
+monitor ()
+repair ()
"

"<<entity>>
Payment"
"-transactionId: String
-amount: double
-date: date
-method: String

+makePayment ()
+refund ()
+getReceipt ()
"

"<<entity>
Location"
"-region: String
-coordination: Point

+trackAccess ()"

"<<entity>
ServiceCenter"
"-name: Sting
-location: String
staff: List<Person>

+scheduleVisit ()"

"<<entity>
Staff"
"-name: String
-role: String
-contact: String

+assignTask ()"

"<<entity>
EnergySystem"
"-id: int
-type: String
-battery: String
-sollarPanel: SolarPanel

+install ()"

"<<entity>
Battery"
"-capacity: double
-health: float"

"<<entity>
SolarPanel"
"-efficiency: float
-wattage: int"

"<<entity>
PersonalUser"
"-incomeLevel: int
-householdSize: int

+applyForSubsidy ()"

"<<entity>>
User"
"-name: String
-phone: String

+register ()"

"<<entity>>
BusinessUser"
"-businessName: String
-employees: int

+requestMiniGrid ()"

"<<boundary>>
DashboardView
"
"+showUsageChart ()
+displayBill ()"

"<<control>>
UserService"
"+getUserData ()
+updateProfile ()"

"<<control>>
PaymentService"
"+processPayment ()
+generateReceipt ()"

"<<control>>
MonitoringService"
"+checkStatus ()
+sendAlert ()"

"<<event>>
Event"
"-eventType: String
-timestamp: Date"

"<<control>>
EventProcessor"
+handleEvent ()
