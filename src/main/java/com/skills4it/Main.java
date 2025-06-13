package com.skills4it;

import com.skills4it.dealership.io.ContractFileManager;
import com.skills4it.dealership.service.DealershipService;
import com.skills4it.dealership.ui.UserInterface;

/**
 * The main entry point of the Car Dealership application.
 *
 * This class is responsible for:
 * 1. Initializing the main components of the application (data layer, service layer, UI layer).
 * 2. Wiring the components together using Dependency Injection.
 * 3. Kicking off the application's lifecycle.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Car Dealership Application...");

        // Step 1: Initialize the Data Access Layer
        // This component knows how to read from and write to files.
        ContractFileManager contractFileManager = new ContractFileManager();

        // Step 2: Initialize the Service Layer
        // Inject the data access component into the service layer. The service layer
        // can now use it to load and save data without knowing the details of file I/O.
        DealershipService dealershipService = new DealershipService(contractFileManager);

        // Step 3: Load the initial state
        // Instruct the service layer to load all data from persistence into memory.
        // This populates the inventory and the list of existing contracts.
        dealershipService.loadAllData();

        // Step 4: Initialize the User Interface Layer
        // Inject the service layer into the UI. The UI can now interact with the
        // application's business logic (e.g., searching for cars, selling them)
        // without knowing about the data layer.
        UserInterface userInterface = new UserInterface(dealershipService);

        // Step 5: Start the application's main loop
        // Hand over control to the User Interface, which will now handle all user input.
        userInterface.display();

        System.out.println("Car Dealership Application has shut down. Goodbye!");
    }
}