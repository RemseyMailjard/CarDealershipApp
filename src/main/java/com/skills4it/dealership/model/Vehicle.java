package com.skills4it.dealership.model;

import java.math.BigDecimal;
import java.util.Objects;


/**
 * Immutable value object that represents a single vehicle in the dealership inventory.
 * <p>
 *     The class is <strong>final</strong> and all fields are <strong>private final</strong>
 *     to guarantee thread-safety and prevent accidental mutation.
 * </p>
 */
public final class Vehicle {

    /** Vehicle Identification Number (17 chars, unique). */
    private final String vin;

    /** Production year (e.g. 2024). */
    private final int year;

    /** Manufacturer brand (e.g. “Toyota”). */
    private final String make;

    /** Specific model name (e.g. “RAV4”). */
    private final String model;

    /** High-level vehicle category. */
    private final VehicleType type;

    /** Exterior colour (plain text). */
    private final String color;

    /** Recorded mileage in kilometres. */
    private final int odometer;

    /** Asking price (in EUR), tax-exclusive. */
    private final BigDecimal price;

    // ──────────────────────────────── Constructors ────────────────────────────────

    /**
     * Main constructor.
     *
     * @param vin      unique VIN, non-blank
     * @param year     1886 ≤ year ≤ currentYear + 1
     * @param make     manufacturer, non-blank
     * @param model    model name, non-blank
     * @param type     category, non-null
     * @param color    paint colour, non-blank
     * @param odometer kilometres ≥ 0
     * @param price    non-null, ≥ 0
     * @throws IllegalArgumentException if any argument is invalid
     * @throws NullPointerException     if type or price is {@code null}
     */
    public Vehicle(String vin,
                   int year,
                   String make,
                   String model,
                   VehicleType type,
                   String color,
                   int odometer,
                   BigDecimal price) {

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

        this.vin = vin;
        this.year = year;
        this.make = make;
        this.model = model;
        this.type = type;
        this.color = color;
        this.odometer = odometer;
        this.price = price;
    }

    // ──────────────────────────────── Getters ─────────────────────────────────────

    public String getVin()        { return vin;       }
    public int    getYear()       { return year;      }
    public String getMake()       { return make;      }
    public String getModel()      { return model;     }
    public VehicleType getType()  { return type;      }
    public String getColor()      { return color;     }
    public int    getOdometer()   { return odometer;  }
    public BigDecimal getPrice()  { return price;     }

    // ──────────────────────────────── Object overrides ────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle v)) return false;
        return vin.equals(v.vin);
    }

    @Override
    public int hashCode() {
        return vin.hashCode();
    }

    @Override
    public String toString() {
        return "%d %s %s <%s> €%s".formatted(year, make, model, vin, price);
    }


}
