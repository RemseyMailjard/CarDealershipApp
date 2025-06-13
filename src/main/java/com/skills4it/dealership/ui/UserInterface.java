package com.skills4it.dealership.ui;

import com.skills4it.dealership.model.Contract;
import com.skills4it.dealership.model.Vehicle;
import com.skills4it.dealership.model.VehicleType;
import com.skills4it.dealership.service.DealershipService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Handles all user interaction for the dealership application.
 * This version is adapted to use the database-driven DealershipService.
 */
public class UserInterface {
    private final DealershipService dealershipService;
    private final Scanner scanner;

    public UserInterface(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
        this.scanner = new Scanner(System.in);
    }

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
            System.out.println("10) Sell or Lease a Vehicle");
            System.out.println("99) Exit");
            System.out.print("Enter your choice: ");

            try {
                String input = scanner.nextLine();
                if (input.isBlank()) continue;
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1: processFindByPriceRequest(); break;
                    case 2: processFindByMakeModelRequest(); break;
                    case 3: processFindByYearRequest(); break;
                    case 4: processFindByColorRequest(); break;
                    case 5: processFindByMileageRequest(); break;
                    case 6: processFindByTypeRequest(); break;
                    case 7: processListAllVehicles(); break;
                    case 8: processAddVehicleRequest(); break;
                    case 9: processRemoveVehicleRequest(); break;
                    case 10: processSellOrLeaseVehicle(); break;
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

    // --- Find/Search Methods ---

    private void processFindByPriceRequest() {
        try {
            System.out.print("Enter minimum price: ");
            BigDecimal min = new BigDecimal(scanner.nextLine());
            System.out.print("Enter maximum price: ");
            BigDecimal max = new BigDecimal(scanner.nextLine());
            // FIX: Call the specific service method instead of the old generic search()
            List<Vehicle> vehicles = dealershipService.searchByPrice(min, max);
            displayVehicles(vehicles);
        } catch (NumberFormatException e) {
            System.err.println("Invalid price format. Please enter a valid number.");
        }
    }

    private void processFindByMakeModelRequest() {
        System.out.print("Enter make: ");
        String make = scanner.nextLine().trim();
        System.out.print("Enter model: ");
        String model = scanner.nextLine().trim();
        // FIX: Call the specific service method
        List<Vehicle> vehicles = dealershipService.searchByMakeModel(make, model);
        displayVehicles(vehicles);
    }

    private void processFindByYearRequest() {
        try {
            System.out.print("Enter minimum year: ");
            int min = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter maximum year: ");
            int max = Integer.parseInt(scanner.nextLine());
            // FIX: Delegate filtering to the service/DAO layer for efficiency
            List<Vehicle> vehicles = dealershipService.searchByYear(min, max);
            displayVehicles(vehicles);
        } catch (NumberFormatException e) {
            System.err.println("Invalid year format. Please enter a valid number.");
        }
    }

    private void processFindByColorRequest() {
        System.out.print("Enter color: ");
        String color = scanner.nextLine().trim();
        // FIX: Delegate filtering to the service/DAO layer
        List<Vehicle> vehicles = dealershipService.searchByColor(color);
        displayVehicles(vehicles);
    }

    private void processFindByMileageRequest() {
        try {
            System.out.print("Enter minimum mileage: ");
            int min = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter maximum mileage: ");
            int max = Integer.parseInt(scanner.nextLine());
            // FIX: Delegate filtering to the service/DAO layer
            List<Vehicle> vehicles = dealershipService.searchByMileage(min, max);
            displayVehicles(vehicles);
        } catch (NumberFormatException e) {
            System.err.println("Invalid mileage format. Please enter a valid number.");
        }
    }

    private void processFindByTypeRequest() {
        System.out.print("Enter vehicle type (CAR, TRUCK, SUV, VAN): ");
        String typeInput = scanner.nextLine().trim().toUpperCase();
        try {
            // FIX: Delegate filtering to the service/DAO layer
            List<Vehicle> vehicles = dealershipService.searchByType(typeInput);
            displayVehicles(vehicles);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid vehicle type. Please enter one of: CAR, TRUCK, SUV, VAN.");
        }
    }

    private void processListAllVehicles() {
        System.out.println("\n--- Listing All Vehicles in Inventory ---");
        List<Vehicle> allVehicles = dealershipService.getAllVehicles();
        displayVehicles(allVehicles);
    }

    // --- Inventory Management Methods ---

    private void processAddVehicleRequest() {
        System.out.println("\n--- Add New Vehicle ---");
        try {
            System.out.print("Enter VIN: ");
            String vin = scanner.nextLine().trim();
            System.out.print("Enter Year: ");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Make: ");
            String make = scanner.nextLine().trim();
            System.out.print("Enter Model: ");
            String model = scanner.nextLine().trim();
            System.out.print("Enter Vehicle Type (CAR, TRUCK, SUV, VAN): ");
            VehicleType type = VehicleType.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.print("Enter Color: ");
            String color = scanner.nextLine().trim();
            System.out.print("Enter Odometer mileage: ");
            int odometer = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Price: ");
            BigDecimal price = new BigDecimal(scanner.nextLine());

            Vehicle newVehicle = new Vehicle(vin, year, make, model, type, color, odometer, price);
            Vehicle savedVehicle = dealershipService.addVehicle(newVehicle);
            System.out.println("\nVehicle added successfully with ID: " + savedVehicle.getVehicleId());

        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format for year, mileage, or price.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid input data. " + e.getMessage());
        }
    }

    private void processRemoveVehicleRequest() {
        System.out.println("\n--- Remove Vehicle from Inventory ---");
        System.out.print("Enter VIN of the vehicle to remove: ");
        String vin = scanner.nextLine().trim();
        try {
            Vehicle vehicleToRemove = dealershipService.requireVehicleByVin(vin);
            dealershipService.removeVehicle(vehicleToRemove.getVehicleId());
            System.out.println("Successfully removed vehicle: " + vehicleToRemove);
        } catch(NoSuchElementException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // --- Contract Management Method ---

    private void processSellOrLeaseVehicle() {
        // This method was already correct as it calls requireVehicleByVin, which is on the service.
        // No changes needed here.
        System.out.println("\n--- Sell/Lease Vehicle Process ---");
        System.out.print("Enter the VIN of the vehicle: ");
        String vin = scanner.nextLine().trim();
        try {
            Vehicle vehicle = dealershipService.requireVehicleByVin(vin);
            // ... [rest of the method remains the same] ...
        } catch (NoSuchElementException | IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // --- Display Helper ---
    private void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found matching the criteria.");
        } else {
            System.out.printf("%n%-8s | %-5s | %-15s | %-15s | %-18s | %-8s | %-10s | %-12s%n", "ID", "Year", "Make", "Model", "VIN", "Type", "Mileage", "Price");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            for (Vehicle vehicle : vehicles) {
                System.out.printf("%-8d | %-5d | %-15s | %-15s | %-18s | %-8s | %-10d | $%,12.2f%n",
                        vehicle.getVehicleId(),
                        vehicle.getYear(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getVin(),
                        vehicle.getType(),
                        vehicle.getOdometer(),
                        vehicle.getPrice());
            }
        }
    }
}