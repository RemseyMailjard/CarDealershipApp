package com.skills4it.dealership.service;

import com.skills4it.dealership.io.ContractFileManager;
import com.skills4it.dealership.model.Contract;
import com.skills4it.dealership.model.LeaseContract;
import com.skills4it.dealership.model.SalesContract;
import com.skills4it.dealership.model.Vehicle;
import com.skills4it.dealership.model.VehicleType;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The central business logic service for the dealership.
 * This class manages the inventory of vehicles and the list of contracts,
 * acting as a bridge between the user interface and the data persistence layer.
 */
public class DealershipService {

    private final Map<String, Vehicle> inventory = new HashMap<>(); // Key = VIN, for fast lookups
    private final List<Contract> contracts = new ArrayList<>();
    private final ContractFileManager contractFileManager;

    /**
     * Constructs a DealershipService with its required dependency.
     *
     * @param contractFileManager The manager responsible for reading/writing contract data. Must not be null.
     */
    public DealershipService(ContractFileManager contractFileManager) {
        this.contractFileManager = Objects.requireNonNull(contractFileManager, "ContractFileManager cannot be null.");
    }

    /**
     * Loads all necessary data from persistence into memory.
     * This should be called once when the application starts.
     */
    public void loadAllData() {
        // Load contracts
        try {
            List<Contract> loadedContracts = contractFileManager.loadAll();
            this.contracts.addAll(loadedContracts);
            System.out.println(loadedContracts.size() + " contract(s) loaded successfully.");
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to load contracts from file: " + e.getMessage());
        }

        // TODO: In a real application, you would also load the vehicle inventory from a file here.
        // For now, we add them manually for the demo.
        addVehicle(new Vehicle("VIN123", 2021, "Honda", "Civic", VehicleType.CAR, "Black", 15000, new BigDecimal("22000.00")));
        addVehicle(new Vehicle("VIN456", 2023, "Ford", "F-150", VehicleType.TRUCK, "Red", 5000, new BigDecimal("45000.00")));
        addVehicle(new Vehicle("VIN789", 2022, "Toyota", "RAV4", VehicleType.SUV, "Blue", 25000, new BigDecimal("31000.00")));
    }

    // --- Inventory Operations ---

    public void addVehicle(Vehicle v) {
        inventory.put(v.getVin(), v);
    }

    public Optional<Vehicle> removeVehicle(String vin) {
        return Optional.ofNullable(inventory.remove(vin));
    }

    public Optional<Vehicle> findByVin(String vin) {
        return Optional.ofNullable(inventory.get(vin));
    }

    public List<Vehicle> getAllVehicles() {
        return List.copyOf(inventory.values()); // Return a defensive copy
    }

    public List<Vehicle> search(String makeOrModel, VehicleType type, BigDecimal minPrice, BigDecimal maxPrice) {
        return inventory.values().stream()
                .filter(v -> makeOrModel == null || (v.getMake() + " " + v.getModel()).toLowerCase().contains(makeOrModel.toLowerCase()))
                .filter(v -> type == null || v.getType() == type)
                .filter(v -> minPrice == null || v.getPrice().compareTo(minPrice) >= 0)
                .filter(v -> maxPrice == null || v.getPrice().compareTo(maxPrice) <= 0)
                .sorted(Comparator.comparing(Vehicle::getMake).thenComparing(Vehicle::getModel))
                .collect(Collectors.toList());
    }

    // --- Contract Operations ---

    /**
     * Handles the business logic of selling a vehicle.
     *
     * @return The created SalesContract.
     * @throws NoSuchElementException if the vehicle VIN is not found in the inventory.
     */
    public SalesContract sellVehicle(LocalDate date, String name, String email, String vin, boolean financed) {
        Vehicle vehicleToSell = requireVehicle(vin);
        SalesContract newContract = ContractFactory.createSale(date, name, email, vehicleToSell, financed);

        // 1. Update in-memory state
        contracts.add(newContract);
        inventory.remove(vin);

        // 2. Update persistent state
        try {
            contractFileManager.save(newContract);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to save the new sales contract! The data might be inconsistent. Error: " + e.getMessage());
            // In a real application, you might attempt to "rollback" the in-memory changes here.
        }

        return newContract;
    }

    /**
     * Handles the business logic of leasing a vehicle.
     *
     * @return The created LeaseContract.
     * @throws NoSuchElementException if the vehicle VIN is not found in the inventory.
     * @throws IllegalStateException if the vehicle is too old to be leased.
     */
    public LeaseContract leaseVehicle(LocalDate date, String name, String email, String vin) {
        Vehicle vehicleToLease = requireVehicle(vin);

        // Enforce business rule: cannot lease a vehicle older than 3 years.
        if (LocalDate.now().getYear() - vehicleToLease.getYear() > 3) {
            throw new IllegalStateException("Vehicle is too old to be leased. Must be 3 years old or newer.");
        }

        LeaseContract newContract = ContractFactory.createLease(date, name, email, vehicleToLease);

        // 1. Update in-memory state
        contracts.add(newContract);
        inventory.remove(vin);

        // 2. Update persistent state
        try {
            contractFileManager.save(newContract);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to save the new lease contract! The data might be inconsistent. Error: " + e.getMessage());
        }

        return newContract;
    }

    public List<Contract> getContracts() {
        return List.copyOf(contracts); // Return a defensive copy
    }

    // --- Private Helper Methods ---

    /**
     * Finds a vehicle by VIN or throws an exception if not found.
     * This avoids null checks in the public methods.
     *
     * @param vin The VIN of the vehicle to find.
     * @return The Vehicle object if found.
     * @throws NoSuchElementException if no vehicle with the given VIN exists in the inventory.
     */
    private Vehicle requireVehicle(String vin) {
        return findByVin(vin)
                .orElseThrow(() -> new NoSuchElementException("Vehicle with VIN " + vin + " not found in inventory."));
    }
}