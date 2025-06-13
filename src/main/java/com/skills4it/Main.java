package com.skills4it;



import com.skills4it.dealership.service.DealershipService;
import com.skills4it.dealership.ui.UserInterface; // Deze klasse maken we hierna

public class Main {

    public static void main(String[] args) {
        // 1. Initialiseer de hoofd business-service.
        // Dit object beheert de inventaris en contracten in het geheugen.
        DealershipService dealershipService = new DealershipService();

        // 2. Maak een instantie van de UserInterface.
        // We injecteren de dealershipService, zodat de UI ermee kan praten.
        // Dit is een vorm van Dependency Injection en is essentieel voor clean architecture.
        UserInterface userInterface = new UserInterface(dealershipService);

        // 3. Start de applicatie door de display-methode van de UI aan te roepen.
        // Vanaf hier neemt de UI de controle over.
        userInterface.display();
    }
}