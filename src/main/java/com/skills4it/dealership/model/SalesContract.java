package com.skills4it.dealership.model;

import com.skills4it.dealership.service.FinanceService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a sales contract with specific rules for taxes,
 * administrative fees, and optional financing.
 * This class is immutable to ensure data consistency.
 */
public final class SalesContract extends Contract {

    // --- Business Rule Constants ---
    // These constants centralize the business rules, making them easy to find and modify.
    private static final BigDecimal SALES_TAX_RATE = new BigDecimal("0.05");
    private static final BigDecimal RECORDING_FEE = new BigDecimal("100.00");
    private static final BigDecimal LOW_PRICE_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal PROCESSING_FEE_LOW = new BigDecimal("295.00");
    private static final BigDecimal PROCESSING_FEE_HIGH = new BigDecimal("495.00");
    private static final BigDecimal HIGH_INTEREST_RATE = new BigDecimal("0.0525"); // For loans < $10k
    private static final int HIGH_INTEREST_TERM_MONTHS = 24;
    private static final BigDecimal LOW_INTEREST_RATE = new BigDecimal("0.0425");  // For loans >= $10k
    private static final int LOW_INTEREST_TERM_MONTHS = 48;

    private final boolean isFinanced;

    /**
     * Creates a new, immutable sales contract.
     *
     * @param contractDate  The date of the contract.
     * @param customerName  The name of the customer.
     * @param customerEmail The customer's email address.
     * @param vehicleSold   The vehicle being sold.
     * @param isFinanced    Indicates whether the sale is financed.
     */
    public SalesContract(LocalDate contractDate, String customerName, String customerEmail, Vehicle vehicleSold, boolean isFinanced) {
        super(contractDate, customerName, customerEmail, vehicleSold);
        this.isFinanced = isFinanced;
    }

    // --- Computed Properties ---

    /**
     * Calculates the sales tax (5% of the vehicle price).
     * @return The calculated sales tax, rounded to 2 decimal places.
     */
    public BigDecimal getSalesTaxAmount() {
        return getVehicleSold().getPrice().multiply(SALES_TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the fixed recording fee ($100).
     * @return The recording fee.
     */
    public BigDecimal getRecordingFee() {
        return RECORDING_FEE;
    }

    /**
     * Determines the processing fee based on the vehicle price.
     * ($295 for vehicles < $10,000, otherwise $495).
     * @return The applicable processing fee.
     */
    public BigDecimal getProcessingFee() {
        return getVehicleSold().getPrice().compareTo(LOW_PRICE_THRESHOLD) < 0
                ? PROCESSING_FEE_LOW
                : PROCESSING_FEE_HIGH;
    }

    public boolean isFinanced() {
        return isFinanced;
    }

    // --- Implementation of Abstract Methods ---

    @Override
    public BigDecimal getTotalPrice() {
        return getVehicleSold().getPrice()
                .add(getSalesTaxAmount())
                .add(getRecordingFee())
                .add(getProcessingFee());
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        if (!isFinanced) {
            return BigDecimal.ZERO.setScale(2); // No financing, no monthly payment.
        }

        final BigDecimal loanAmount = getTotalPrice();
        final BigDecimal monthlyInterestRate;
        final int numberOfPayments;

        // Determine interest rate and term based on the original vehicle price.
        if (getVehicleSold().getPrice().compareTo(LOW_PRICE_THRESHOLD) >= 0) {
            monthlyInterestRate = LOW_INTEREST_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
            numberOfPayments = LOW_INTEREST_TERM_MONTHS;
        } else {
            monthlyInterestRate = HIGH_INTEREST_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
            numberOfPayments = HIGH_INTEREST_TERM_MONTHS;
        }

        // DELEGATE the complex calculation to the specialized FinanceService.
        // This is a perfect example of the Single Responsibility Principle and DRY.
        return FinanceService.annuity(loanAmount, monthlyInterestRate, numberOfPayments);
    }

    @Override
    public ContractType getType() {
        return ContractType.SALE;
    }

    @Override
    public String toDataString() {
        Vehicle v = getVehicleSold();
        return String.join("|",
                getType().name(), // Use .name() for enums, it's more explicit.
                getContractDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                getCustomerName(),
                getCustomerEmail(),
                String.valueOf(v.getVin()),
                String.valueOf(v.getYear()),
                v.getMake(),
                v.getModel(),
                v.getType().name(), // Use .name() for enums.
                v.getColor(),
                String.valueOf(v.getOdometer()),
                v.getPrice().toPlainString(), // .toPlainString() avoids scientific notation.
                getSalesTaxAmount().toPlainString(),
                getRecordingFee().toPlainString(),
                getProcessingFee().toPlainString(),
                getTotalPrice().toPlainString(),
                isFinanced() ? "YES" : "NO",
                getMonthlyPayment().toPlainString()
        );
    }
}
