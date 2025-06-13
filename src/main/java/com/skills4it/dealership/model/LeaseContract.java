package com.skills4it.dealership.model;

import com.skills4it.dealership.service.FinanceService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Leasecontract met end‐value en leasefee conform workbook-regels.
 */
public final class LeaseContract extends Contract {

    /* ────── constants ────── */
    private static final BigDecimal RECORDING_FEE     = new BigDecimal("100");
    private static final BigDecimal EXPECTED_END_RATE = new BigDecimal("0.50");
    private static final BigDecimal LEASE_FEE_RATE    = new BigDecimal("0.07");
    private static final BigDecimal ANNUAL_RATE       = new BigDecimal("0.04");
    private static final int        TERM_MONTHS       = 36;
    private static final MathContext MC               = new MathContext(16, RoundingMode.HALF_UP);

    /* ────── derived fields ────── */
    private final BigDecimal expectedEndValue;
    private final BigDecimal leaseFee;

    /* ────── constructor ────── */
    public LeaseContract(LocalDate contractDate,
                         String customerName,
                         String customerEmail,
                         Vehicle vehicleSold) {

        super(contractDate, customerName, customerEmail, vehicleSold);

        BigDecimal price = vehicleSold.getPrice();
        this.expectedEndValue = price.multiply(EXPECTED_END_RATE, MC);
        this.leaseFee         = price.multiply(LEASE_FEE_RATE, MC);
    }

    /* ────── business logic ────── */
    @Override
    public BigDecimal getTotalPrice() {
        return expectedEndValue
                .add(leaseFee, MC)
                .add(RECORDING_FEE, MC);
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        BigDecimal monthlyRate = ANNUAL_RATE.divide(new BigDecimal("12"), MC);
        // GEBRUIK DE GECENTRALISEERDE SERVICE
        return FinanceService.annuity(getTotalPrice(), monthlyRate, TERM_MONTHS);
    }


    /* ────── getters ────── */
    public BigDecimal getExpectedEndValue() { return expectedEndValue; }
    public BigDecimal getLeaseFee()         { return leaseFee; }

    /* ────── persistence helpers ────── */
    @Override
    public ContractType getType() { return ContractType.LEASE; }

    /** Pipe-separated CSV-regel voor opslag. */
    @Override
    public String toDataString() {
        return "%s|%s|%s|%s|%s|%s|%s"
                .formatted(getContractDate(),             // 1: datum
                        getType(),                     // 2: LEASE
                        getCustomerName(),             // 3: naam
                        getCustomerEmail(),            // 4: email
                        getVehicleSold().getVin(),     // 5: VIN
                        getTotalPrice(),               // 6: totaal
                        getMonthlyPayment());          // 7: per maand
    }
}
