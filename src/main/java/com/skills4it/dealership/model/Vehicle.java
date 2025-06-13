package com.skills4it.dealership.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable value object that represents a single vehicle in the dealership inventory.
 * <p>
 * This version is adapted for database persistence by including a vehicleId.
 * The class remains immutable by using specific constructors and a 'wither' method.
 * </p>
 */
public final class Vehicle {

    // --- Fields ---

    /** The primary key from the database. Can be null for a new, unsaved vehicle. */
    private final Integer vehicleId;

    /** Vehicle Identification Number (unique business key). */
    private final String vin;

    /** Production year. */
    private final int year;

    /** Manufacturer brand (e.g., "Honda"). */
    private final String make;

    /** Specific model name (e.g., "Civic"). */
    private final String model;

    /** High-level vehicle category. */
    private final VehicleType type;

    /** Exterior color. */
    private final String color;

    /** Recorded mileage. */
    private final int odometer;

    /** Asking price, tax-exclusive. */
    private final BigDecimal price;

    // --- Constructors ---

    /**
     * Public constructor for creating a NEW vehicle within the application (e.g., via the UI).
     * This vehicle does not yet have a database ID.
     */
    public Vehicle(String vin, int year, String make, String model,
                   VehicleType type, String color, int odometer, BigDecimal price) {
        // Delegate to the main private constructor, passing null for the ID.
        this(null, vin, year, make, model, type, color, odometer, price);
    }

    /**
     * Public constructor for hydrating a vehicle object from the database (e.g., in a DAO).
     * This vehicle already has a database ID.
     */
    public Vehicle(Integer vehicleId, String vin, int year, String make, String model,
                   VehicleType type, String color, int odometer, BigDecimal price) {
        // --- Input Validation (Single Point of Truth) ---
        if (vin == null || vin.isBlank())
            throw new IllegalArgumentException("VIN must not be blank");
        int currentYear = java.time.Year.now().getValue();
        if (year < 1886 || year > currentYear + 1)
            throw new IllegalArgumentException("Year out of range: " + year);
        if (make == null || make.isBlank())
            throw new IllegalArgumentException("Make must not be blank");
        if (model == null || model.isBlank())
            throw new IllegalArgumentException("Model must not be blank");
        Objects.requireNonNull(type, "Vehicle type must not be null");
        if (color == null || color.isBlank())
            throw new IllegalArgumentException("Color must not be blank");
        if (odometer < 0)
            throw new IllegalArgumentException("Odometer cannot be negative");
        Objects.requireNonNull(price, "Price must not be null");
        if (price.signum() < 0)
            throw new IllegalArgumentException("Price cannot be negative");
        // --- End of Validation ---

        this.vehicleId = vehicleId;
        this.vin = vin;
        this.year = year;
        this.make = make;
        this.model = model;
        this.type = type;
        this.color = color;
        this.odometer = odometer;
        this.price = price;
    }

    // --- Getters ---

    public Integer getVehicleId() { return vehicleId; }
    public String getVin()        { return vin; }
    public int    getYear()       { return year; }
    public String getMake()       { return make; }
    public String getModel()      { return model; }
    public VehicleType getType()  { return type; }
    public String getColor()      { return color; }
    public int    getOdometer()   { return odometer; }
    public BigDecimal getPrice()  { return price; }


    // --- Wither Method for Immutability ---

    /**
     * Creates a new Vehicle instance with the specified database ID, while keeping all other fields the same.
     * This is used by the DAO after an INSERT operation to return a complete object without mutating state.
     *
     * @param newId The database-generated ID.
     * @return A new Vehicle object, identical to this one but with an ID.
     */
    public Vehicle withId(int newId) {
        return new Vehicle(newId, this.vin, this.year, this.make, this.model, this.type, this.color, this.odometer, this.price);
    }


    // --- Object Overrides ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle v)) return false;
        // Equality is based on the business key (VIN), not the database ID.
        return vin.equals(v.vin);
    }

    @Override
    public int hashCode() {
        // Hash code is based on the business key (VIN).
        return vin.hashCode();
    }

    @Override
    public String toString() {
        // Includes the vehicleId if it exists, for better debugging.
        String idStr = (vehicleId != null) ? "ID: " + vehicleId + ", " : "";
        return String.format("%s%d %s %s (VIN: %s), Price: $%,.2f",
                idStr, year, make, model, vin, price);
    }
}