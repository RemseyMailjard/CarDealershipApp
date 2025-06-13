package com.skills4it;

import com.skills4it.dealership.data.dao.DatabaseConnector;
import com.skills4it.dealership.data.dao.JdbcVehicleDao;
import com.skills4it.dealership.data.dao.VehicleDao;
import com.skills4it.dealership.service.DealershipService;
import com.skills4it.dealership.ui.UserInterface;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * The main entry point of the Car Dealership application, configured for a database backend.
 *
 * This class is responsible for:
 * 1. Initializing the database connection pool (DataSource).
 * 2. Initializing the Data Access Objects (DAOs).
 * 3. Initializing the Service Layer and injecting the DAOs.
 * 4. Initializing the UI Layer and injecting the Service.
 * 5. Starting the application.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Car Dealership Application with Database Backend...");

        // Step 1: Initialize the DataSource for database connections.
        // This is done once for the entire application.
        BasicDataSource dataSource = DatabaseConnector.getDataSource();

        // Step 2: Create the concrete DAO implementation.
        // This object knows how to talk to the 'vehicles' table.
        VehicleDao vehicleDao = new JdbcVehicleDao(dataSource);

        // TODO: Create DAOs for contracts here when they are built
        // SalesContractDao salesContractDao = new JdbcSalesContractDao(dataSource);

        // Step 3: Create the Service Layer and inject the DAO(s).
        // The service now depends on the DAO interface, not a concrete implementation.
        DealershipService dealershipService = new DealershipService(vehicleDao);

        // NOTE: Step for 'loadAllData' is REMOVED. It is no longer needed.
        // The data lives in the database and is queried on demand.

        // Step 4: Create the User Interface and inject the Service.
        UserInterface userInterface = new UserInterface(dealershipService);

        // Step 5: Start the application's main loop.
        userInterface.display();

        System.out.println("Car Dealership Application has shut down. Goodbye!");
    }
}