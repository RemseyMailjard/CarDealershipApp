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
     * @param dealershipService De service laag die de UI zal gebruiken.
     */
    public UserInterface(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
        this.scanner = new Scanner(System.in);
        init(); // Laad wat testdata
    }

    /**
     * Laadt initiële data in de dealership voor demonstratiedoeleinden.
     */
    private void init() {
        // Voeg hier eventueel een paar standaard voertuigen toe om mee te testen
        dealershipService.addVehicle(new Vehicle("VIN123", 2021, "Honda", "Civic", com.skills4it.dealership.model.VehicleType.CAR, "Black", 15000, new BigDecimal("22000.00")));
        dealershipService.addVehicle(new Vehicle("VIN456", 2023, "Ford", "F-150", com.skills4it.dealership.model.VehicleType.TRUCK, "Red", 5000, new BigDecimal("45000.00")));
    }

    /**
     * Toont het hoofdmenu en verwerkt de gebruikerskeuzes in een loop.
     */
    public void display() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== Hoofdmenu =====");
            System.out.println("1) Zoek voertuig op prijs");
            System.out.println("2) Zoek voertuig op merk/model");
            // Voeg hier de andere menu opties toe...
            System.out.println("99) Afsluiten");
            System.out.print("Kies een optie: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        processFindByPriceRequest();
                        break;
                    case 2:
                        processFindByMakeModelRequest();
                        break;
                    // ... andere cases
                    case 99:
                        running = false;
                        System.out.println("Tot ziens!");
                        break;
                    default:
                        System.out.println("Ongeldige keuze, probeer opnieuw.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ongeldige invoer, voer een getal in.");
            }
        }
    }

    // VOORBEELD IMPLEMENTATIE:
    private void processFindByPriceRequest() {
        System.out.print("Voer minimumprijs in: ");
        BigDecimal min = new BigDecimal(scanner.nextLine());
        System.out.print("Voer maximumprijs in: ");
        BigDecimal max = new BigDecimal(scanner.nextLine());

        List<Vehicle> vehicles = dealershipService.search(null, null, min, max);
        displayVehicles(vehicles);
    }

    private void processFindByMakeModelRequest() {
        System.out.print("Voer merk of model in: ");
        String makeModel = scanner.nextLine();
        List<Vehicle> vehicles = dealershipService.search(makeModel, null, null, null);
        displayVehicles(vehicles);
    }

    private void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("Geen voertuigen gevonden die aan de criteria voldoen.");
        } else {
            System.out.println("\n--- Gevonden Voertuigen ---");
            vehicles.forEach(System.out::println); // Gebruikt de toString() van Vehicle
        }
    }
}
