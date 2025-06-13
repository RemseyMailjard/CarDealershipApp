package com.skills4it.dealership.service;

import com.skills4it.dealership.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central business-service that manages inventory and contracts.
 */
public class DealershipService {

    private final Map<String, Vehicle> inventory = new HashMap<>();   // key = VIN
    private final List<Contract> contracts = new ArrayList<>();

    /* ───────── Inventory operations ───────── */

    public void addVehicle(Vehicle v)     { inventory.put(v.getVin(), v); }

    public Optional<Vehicle> removeVehicle(String vin) {
        return Optional.ofNullable(inventory.remove(vin));
    }

    public Optional<Vehicle> findByVin(String vin) {
        return Optional.ofNullable(inventory.get(vin));
    }

    public List<Vehicle> search(String makeOrModel, VehicleType type,
                                BigDecimal minPrice, BigDecimal maxPrice) {

        return inventory.values().stream()
                .filter(v -> makeOrModel == null
                        || (v.getMake() + v.getModel())
                        .toLowerCase().contains(makeOrModel.toLowerCase()))
                .filter(v -> type == null || v.getType() == type)
                .filter(v -> minPrice == null || v.getPrice().compareTo(minPrice) >= 0)
                .filter(v -> maxPrice == null || v.getPrice().compareTo(maxPrice) <= 0)
                .sorted(Comparator.comparing(Vehicle::getPrice))
                .collect(Collectors.toList());
    }

    /* ───────── Contract operations ───────── */

    public SalesContract sellVehicle(LocalDate date, String name, String email,
                                     String vin, boolean financed) {

        Vehicle v = requireVehicle(vin);
        SalesContract c = ContractFactory.createSale(date, name, email, v, financed);
        contracts.add(c);
        inventory.remove(vin);
        return c;
    }

    public LeaseContract leaseVehicle(LocalDate date, String name, String email,
                                      String vin) {

        Vehicle v = requireVehicle(vin);
        LeaseContract c = ContractFactory.createLease(date, name, email, v);
        contracts.add(c);
        inventory.remove(vin);
        return c;
    }

    public List<Contract> getContracts() { return List.copyOf(contracts); }

    /* ───────── Helpers ───────── */

    private Vehicle requireVehicle(String vin) {
        return Optional.ofNullable(inventory.get(vin))
                .orElseThrow(() -> new NoSuchElementException("Vehicle " + vin + " not in stock"));
    }
}
