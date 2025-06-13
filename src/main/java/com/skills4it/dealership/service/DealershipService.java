package com.skills4it.dealership.service;

import com.skills4it.dealership.data.dao.LeaseContractDao;
import com.skills4it.dealership.data.dao.SalesContractDao;
import com.skills4it.dealership.data.dao.VehicleDao;
import com.skills4it.dealership.model.LeaseContract;
import com.skills4it.dealership.model.SalesContract;
import com.skills4it.dealership.model.Vehicle;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class DealershipService {
    private final VehicleDao vehicleDao;
    private final SalesContractDao salesContractDao;
    private final LeaseContractDao leaseContractDao;

    public DealershipService(VehicleDao vehicleDao, SalesContractDao salesContractDao, LeaseContractDao leaseContractDao) {
        this.vehicleDao = Objects.requireNonNull(vehicleDao, "VehicleDao cannot be null.");
        this.salesContractDao = Objects.requireNonNull(salesContractDao, "SalesContractDao cannot be null.");
        this.leaseContractDao = Objects.requireNonNull(leaseContractDao, "LeaseContractDao cannot be null.");
    }

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
        return vehicleDao.create(vehicle);
    }

    public void removeVehicle(int vehicleId) {
        vehicleDao.delete(vehicleId);
    }

    public SalesContract sellVehicle(LocalDate date, String name, String email, String vin, boolean financed) {
        Vehicle vehicleToSell = requireVehicleByVin(vin);
        SalesContract newContract = ContractFactory.createSale(date, name, email, vehicleToSell, financed);
        try {
            SalesContract savedContract = salesContractDao.create(newContract);
            vehicleDao.delete(vehicleToSell.getVehicleId());
            System.out.println("INFO: Vehicle " + vin + " sold and contract " + savedContract.getSalesContractId() + " created.");
            return savedContract;
        } catch (Exception e) {
            System.err.println("CRITICAL: Transaction failed for selling vehicle " + vin + ". A manual database rollback may be required.");
            throw new RuntimeException("Failed to complete sale transaction.", e);
        }
    }

    public LeaseContract leaseVehicle(LocalDate date, String name, String email, String vin) {
        Vehicle vehicleToLease = requireVehicleByVin(vin);
        if (LocalDate.now().getYear() - vehicleToLease.getYear() > 3) {
            throw new IllegalStateException("Vehicle is too old to be leased. Must be 3 years old or newer.");
        }
        LeaseContract newContract = ContractFactory.createLease(date, name, email, vehicleToLease);
        try {
            LeaseContract savedContract = leaseContractDao.create(newContract);
            vehicleDao.delete(vehicleToLease.getVehicleId());
            System.out.println("INFO: Vehicle " + vin + " leased and contract " + savedContract.getLeaseContractId() + " created.");
            return savedContract;
        } catch (Exception e) {
            System.err.println("CRITICAL: Transaction failed for leasing vehicle " + vin + ". A manual database rollback may be required.");
            throw new RuntimeException("Failed to complete lease transaction.", e);
        }
    }

    public Vehicle requireVehicleByVin(String vin) {
        return vehicleDao.findByVin(vin)
                .orElseThrow(() -> new NoSuchElementException("Vehicle with VIN '" + vin + "' not found in inventory."));
    }
}