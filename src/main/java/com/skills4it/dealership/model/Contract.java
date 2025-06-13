package com.skills4it.dealership.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an abstract contract for either a car sale or lease.
 * <p>
 *     Holds the common data shared by all contract types and defines the
 *     abstract methods that each concrete subclass must implement.
 * </p>
 */
public abstract class Contract {

    /** The date on which the contract was signed. */
    private final LocalDate contractDate;

    /** Customer’s full name. */
    private final String customerName;

    /** Customer’s e-mail address. */
    private final String customerEmail;

    /** The vehicle that is being sold or leased. */
    private final Vehicle vehicleSold;

    /**
     * Creates a new contract instance.
     *
     * @param contractDate  the date of the contract, must not be {@code null}
     * @param customerName  the customer’s name, must not be {@code null}
     * @param customerEmail the customer’s e-mail, must not be {@code null}
     * @param vehicleSold   the vehicle involved in the contract, must not be {@code null}
     */
    protected Contract(LocalDate contractDate,
                       String customerName,
                       String customerEmail,
                       Vehicle vehicleSold) {

        // Validate input
        this.contractDate  = Objects.requireNonNull(contractDate,  "Contract date cannot be null");
        this.customerName  = Objects.requireNonNull(customerName,  "Customer name cannot be null");
        this.customerEmail = Objects.requireNonNull(customerEmail, "E-mail cannot be null");
        this.vehicleSold   = Objects.requireNonNull(vehicleSold,   "Vehicle cannot be null");
    }

    /* ─────────── Getters ─────────── */

    public LocalDate getContractDate() { return contractDate; }

    public String getCustomerName()    { return customerName; }

    public String getCustomerEmail()   { return customerEmail; }

    public Vehicle getVehicleSold()    { return vehicleSold; }

    /* ─────────── Abstract methods to be implemented by subclasses ─────────── */

    /**
     * Calculates the total price for this contract.
     * Implementation differs between sale and lease contracts.
     *
     * @return the total price as {@link BigDecimal}
     */
    public abstract BigDecimal getTotalPrice();

    /**
     * Calculates the monthly payment.
     * Concrete contracts may return {@code BigDecimal.ZERO} if no financing applies.
     *
     * @return the monthly payment as {@link BigDecimal}
     */
    public abstract BigDecimal getMonthlyPayment();

    /**
     * Serialises this contract into a pipe-separated string, suitable for CSV-style persistence.
     *
     * @return a pipe-separated data string that represents all contract fields
     */
    public abstract String toDataString();

    /**
     * Returns the specific type of this contract (SALE or LEASE).
     *
     * @return {@link ContractType#SALE} or {@link ContractType#LEASE}
     */
    public abstract ContractType getType();
}
