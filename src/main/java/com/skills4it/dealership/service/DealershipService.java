package com.skills4it.dealership.service;

import com.skills4it.dealership.data.dao.VehicleDao;
import com.skills4it.dealership.model.LeaseContract;
import com.skills4it.dealership.model.SalesContract;
import com.skills4it.dealership.model.Vehicle;
import com.skills4it.dealership.model.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * The central business logic service for the dealership, adapted for a database backend.
 * This class is STATELESS. It does not hold data (like in-memory lists). Instead, it orchestrates
 * business operations by delegating all data access to Data Access Objects (DAOs).
 */
public final class DealershipService {

    private final VehicleDao vehicleDao;
    // TODO: Add DAOs for contracts when they are created
    // private final SalesContractDao salesContractDao;
    // private final LeaseContractDao leaseContractDao;

    /**
     * Constructs a DealershipService with its required data access dependencies.
     *
     * @param vehicleDao The DAO responsible for vehicle data. Must not be null.
     */
    public DealershipService(VehicleDao vehicleDao) {
        this.vehicleDao = Objects.requireNonNull(vehicleDao, "VehicleDao cannot be null.");
        // this.salesContractDao = Objects.requireNonNull(salesContractDao, "SalesContractDao cannot be null.");
    }

    // --- Inventory Operations (Delegated to VehicleDao) ---

    public List<Vehicle> searchByPrice(BigDecimal min, BigDecimal max) {
        return vehicleDao.searchByPriceRange(min, max);
    }

    public List<Vehicle> searchByMakeModel(String make, String model) {
        return vehicleDao.searchByMakeModel(make, model);
    }

    public List<Vehicle> searchByYear(int min, int max) {
        return vehicleDao.searchByYearRange(min, max);
    }

    public List<Vehicle> searchByColor(String color) {
        return vehicleDao.searchByColor(color);
    }

    public List<Vehicle> searchByMileage(int min, int max) {
        return vehicleDao.searchByMileageRange(min, max);
    }

    public List<Vehicle> searchByType(String type) {
        return vehicleDao.searchByType(type);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleDao.getAll();
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        // The DAO returns the vehicle with the new ID
        return vehicleDao.create(vehicle);
    }

    public void removeVehicle(int vehicleId) {
        // The DAO handles the deletion
        vehicleDao.delete(vehicleId);
    }

    // --- Contract Operations (Orchestration) ---

    public SalesContract sellVehicle(LocalDate date, String name, String email, String vin, boolean financed) {
        Vehicle vehicleToSell = requireVehicleByVin(vin);
        SalesContract newContract = ContractFactory.createSale(date, name, email, vehicleToSell, financed);

        // --- TRANSACTIONAL BLOCK START ---
        try {
            // TODO: Persist the new contract using salesContractDao.create(newContract);
            vehicleDao.delete(vehicleToSell.getVehicleId());
            System.out.println("INFO: Vehicle " + vin + " sold and removed from inventory.");
            return newContract;
        } catch (Exception e) {
            System.err.println("CRITICAL: Transaction failed for selling vehicle " + vin + ". Rolling back changes.");
            throw new RuntimeException("Failed to complete sale transaction.", e);
        }
        // --- TRANSACTIONAL BLOCK END ---
    }

    public LeaseContract leaseVehicle(LocalDate date, String name, String email, String vin) {
        Vehicle vehicleToLease = requireVehicleByVin(vin);

        if (LocalDate.now().getYear() - vehicleToLease.getYear() > 3) {
            throw new IllegalStateException("Vehicle is too old to be leased. Must be 3 years old or newer.");
        }

        LeaseContract newContract = ContractFactory.createLease(date, name, email, vehicleToLease);

        // --- TRANSACTIONAL BLOCK START ---
        try {
            // TODO: Persist the lease contract using leaseContractDao.create(newContract);
            vehicleDao.delete(vehicleToLease.getVehicleId());
            System.out.println("INFO: Vehicle " + vin + " leased and removed from inventory.");
            return newContract;
        } catch (Exception e) {
            System.err.println("CRITICAL: Transaction failed for leasing vehicle " + vin + ". Rolling back changes.");
            throw new RuntimeException("Failed to complete lease transaction.", e);
        }
        // --- TRANSACTIONAL BLOCK END ---
    }


    // --- Private Helper Methods ---

    /**
     * Finds a vehicle by VIN or throws a clear exception if not found.
     * @param vin The VIN of the vehicle to find.
     * @return The Vehicle object if found.
     * @throws NoSuchElementException if no vehicle with the given VIN exists.
     */
    public Vehicle requireVehicleByVin(String vin) {
        return vehicleDao.findByVin(vin)
                .orElseThrow(() -> new NoSuchElementException("Vehicle with VIN '" + vin + "' not found in inventory."));
    }
}