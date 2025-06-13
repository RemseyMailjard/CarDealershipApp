package com.skills4it.dealership.data.dao;

import com.skills4it.dealership.model.Vehicle;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for data access operations related to vehicles.
 * This interface abstracts the persistence mechanism, allowing for different
 * implementations (e.g., JDBC, JPA, in-memory mock) without changing the
 * service layer.
 */
public interface VehicleDao {

    /**
     * Finds all vehicles within a specified price range.
     *
     * @param min The minimum price.
     * @param max The maximum price.
     * @return A list of matching vehicles, sorted by price.
     */
    List<Vehicle> searchByPriceRange(BigDecimal min, BigDecimal max);

    /**
     * Finds all vehicles that match a given make and/or model.
     *
     * @param make  The make to search for (can be a partial match).
     * @param model The model to search for (can be a partial match).
     * @return A list of matching vehicles, sorted by make and model.
     */
    List<Vehicle> searchByMakeModel(String make, String model);

    /**
     * Finds all vehicles within a specified production year range.
     *
     * @param min The minimum year.
     * @param max The maximum year.
     * @return A list of matching vehicles, sorted by year descending.
     */
    List<Vehicle> searchByYearRange(int min, int max);

    /**
     * Finds all vehicles of a specific color.
     *
     * @param color The color to search for.
     * @return A list of matching vehicles.
     */
    List<Vehicle> searchByColor(String color);

    /**
     * Finds all vehicles within a specified mileage range.
     *
     * @param min The minimum odometer reading.
     * @param max The maximum odometer reading.
     * @return A list of matching vehicles, sorted by mileage.
     */
    List<Vehicle> searchByMileageRange(int min, int max);

    /**
     * Finds all vehicles of a specific type (e.g., "SUV", "TRUCK").
     *
     * @param type The vehicle type to search for.
     * @return A list of matching vehicles.
     */
    List<Vehicle> searchByType(String type);

    /**
     * Retrieves all vehicles from the data source.
     *
     * @return A list of all vehicles, typically sorted for consistent display.
     */
    List<Vehicle> getAll();

    /**
     * Finds a single vehicle by its unique business key (VIN).
     *
     * @param vin The Vehicle Identification Number.
     * @return An Optional containing the found vehicle, or an empty Optional if not found.
     */
    Optional<Vehicle> findByVin(String vin);

    /**
     * Persists a new vehicle to the data source.
     *
     * @param vehicle The vehicle object to create. It should not have an ID yet.
     * @return The created vehicle, now including the database-generated ID.
     */
    Vehicle create(Vehicle vehicle);

    /**
     * Deletes a vehicle from the data source using its primary key.
     *
     * @param vehicleId The database ID of the vehicle to delete.
     */
    void delete(int vehicleId);

    // Note: An update method is not strictly required by the current project scope,
    // but would be included in a full-featured DAO.
    // void update(int vehicleId, Vehicle vehicle);
}