package com.skills4it.dealership.model;

import com.skills4it.dealership.service.FinanceService;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

public final class LeaseContract extends Contract {

    private final Integer leaseContractId;

    private static final BigDecimal RECORDING_FEE = new BigDecimal("100");
    private static final BigDecimal EXPECTED_END_RATE = new BigDecimal("0.50");
    private static final BigDecimal LEASE_FEE_RATE = new BigDecimal("0.07");
    private static final BigDecimal ANNUAL_RATE = new BigDecimal("0.04");
    private static final int TERM_MONTHS = 36;
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    private final BigDecimal expectedEndValue;
    private final BigDecimal leaseFee;

    public LeaseContract(LocalDate contractDate, String customerName, String customerEmail, Vehicle vehicleSold) {
        this(null, contractDate, customerName, customerEmail, vehicleSold);
    }

    public LeaseContract(Integer leaseContractId, LocalDate contractDate, String customerName, String customerEmail, Vehicle vehicleSold) {
        super(contractDate, customerName, customerEmail, vehicleSold);
        this.leaseContractId = leaseContractId;

        BigDecimal price = vehicleSold.getPrice();
        this.expectedEndValue = price.multiply(EXPECTED_END_RATE, MC);
        this.leaseFee = price.multiply(LEASE_FEE_RATE, MC);
    }

    public Integer getLeaseContractId() {
        return leaseContractId;
    }

    public BigDecimal getExpectedEndValue() {
        return expectedEndValue;
    }

    public BigDecimal getLeaseFee() {
        return leaseFee;
    }

    @Override
    public BigDecimal getTotalPrice() {
        return expectedEndValue
                .add(leaseFee, MC)
                .add(RECORDING_FEE, MC);
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        BigDecimal monthlyRate = ANNUAL_RATE.divide(new BigDecimal("12"), MC);
        return FinanceService.annuity(getTotalPrice(), monthlyRate, TERM_MONTHS);
    }

    @Override
    public ContractType getType() {
        return ContractType.LEASE;
    }

    public LeaseContract withId(int newId) {
        return new LeaseContract(newId, getContractDate(), getCustomerName(), getCustomerEmail(), getVehicleSold());
    }
}