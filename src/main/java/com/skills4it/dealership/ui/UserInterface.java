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
        // The init() method is no longer needed here,
        // since DealershipService now handles its own data loading.
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
                String input = scanner.nextLine();
                if (input.isBlank()) {
                    continue; // If user just hits enter, show menu again.
                }
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        processFindByPriceRequest();
                        break;
                    case 2:
                        processFindByMakeModelRequest();
                        break;
                    // TODO: Implement cases 3, 4, 5, 6
                    case 7:
                        // --- FIX: This case was missing. It is now implemented. ---
                        processListAllVehicles();
                        break;
                    // TODO: Implement cases 8, 9, 10
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

    /**
     * --- NEW METHOD ---
     * Gets all vehicles from the service and displays them.
     */
    private void processListAllVehicles() {
        System.out.println("\n--- Listing All Vehicles in Inventory ---");
        List<Vehicle> allVehicles = dealershipService.getAllVehicles();
        displayVehicles(allVehicles);
    }

    /**
     * Helper method to display a list of vehicles to the console.
     * @param vehicles The list of vehicles to display.
     */
    private void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found matching the criteria.");
        } else {
            // Using a formatted header for better alignment
            System.out.printf("%-5s | %-15s | %-15s | %-10s | %-10s%n", "Year", "Make", "Model", "VIN", "Price");
            System.out.println("---------------------------------------------------------------------");
            for (Vehicle vehicle : vehicles) {
                System.out.printf("%-5d | %-15s | %-15s | %-10s | $%,10.2f%n",
                        vehicle.getYear(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getVin(),
                        vehicle.getPrice());
            }
        }
    }
}