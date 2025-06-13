package com.skills4it.dealership.ui;

import com.skills4it.dealership.model.Vehicle;
import com.skills4it.dealership.service.DealershipService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all user interaction for the dealership application.
 */
public class UserInterface {
    private final DealershipService dealershipService;
    private final Scanner scanner;

    /**
     * Constructor.
     * @param dealershipService The service layer that the UI will use.
     */
    public UserInterface(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
        this.scanner = new Scanner(System.in);
        init(); // Load some test data
    }

    /**
     * Loads initial data into the dealership for demonstration purposes.
     */
    private void init() {
        // Optionally add a few standard vehicles for testing purposes
        dealershipService.addVehicle(new Vehicle("VIN123", 2021, "Honda", "Civic", com.skills4it.dealership.model.VehicleType.CAR, "Black", 15000, new BigDecimal("22000.00")));
        dealershipService.addVehicle(new Vehicle("VIN456", 2023, "Ford", "F-150", com.skills4it.dealership.model.VehicleType.TRUCK, "Red", 5000, new BigDecimal("45000.00")));
        // TODO: Add more vehicles to make the demo richer
    }

    /**
     * Displays the main menu and processes user choices in a loop.
     */
    public void display() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== Main Menu =====");
            System.out.println("1) Find vehicle by price range");
            System.out.println("2) Find vehicle by make/model");
            System.out.println("3) Find vehicle by year range");
            System.out.println("4) Find vehicle by color");
            System.out.println("5) Find vehicle by mileage range");
            System.out.println("6) Find vehicle by type (CAR, TRUCK, SUV, VAN)");
            System.out.println("7) List all vehicles");
            System.out.println("8) Add a vehicle");
            System.out.println("9) Remove a vehicle");
            System.out.println("10) Sell/Lease a vehicle");
            System.out.println("99) Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        processFindByPriceRequest();
                        break;
                    case 2:
                        processFindByMakeModelRequest();
                        break;
                    // TODO: Implement cases 3 through 10
                    case 99:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number.");
            }
        }
    }

    // --- Implementation of Menu Options ---

    private void processFindByPriceRequest() {
        System.out.print("Enter minimum price: ");
        BigDecimal min = new BigDecimal(scanner.nextLine());
        System.out.print("Enter maximum price: ");
        BigDecimal max = new BigDecimal(scanner.nextLine());

        List<Vehicle> vehicles = dealershipService.search(null, null, min, max);
        displayVehicles(vehicles);
    }

    private void processFindByMakeModelRequest() {
        System.out.print("Enter make or model: ");
        String makeModel = scanner.nextLine();
        List<Vehicle> vehicles = dealershipService.search(makeModel, null, null, null);
        displayVehicles(vehicles);
    }

    private void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found matching the criteria.");
        } else {
            System.out.println("\n--- Found Vehicles ---");
            vehicles.forEach(System.out::println); // Uses the toString() from Vehicle
        }
    }
}