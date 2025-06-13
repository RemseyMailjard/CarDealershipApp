package com.skills4it.dealership.model;

import com.skills4it.dealership.service.FinanceService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public final class SalesContract extends Contract {

    private final Integer salesContractId;

    private static final BigDecimal SALES_TAX_RATE = new BigDecimal("0.05");
    private static final BigDecimal RECORDING_FEE = new BigDecimal("100.00");
    private static final BigDecimal LOW_PRICE_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal PROCESSING_FEE_LOW = new BigDecimal("295.00");
    private static final BigDecimal PROCESSING_FEE_HIGH = new BigDecimal("495.00");
    private static final BigDecimal HIGH_INTEREST_RATE = new BigDecimal("0.0525");
    private static final int HIGH_INTEREST_TERM_MONTHS = 24;
    private static final BigDecimal LOW_INTEREST_RATE = new BigDecimal("0.0425");
    private static final int LOW_INTEREST_TERM_MONTHS = 48;

    private final boolean isFinanced;

    public SalesContract(LocalDate contractDate, String customerName, String customerEmail, Vehicle vehicleSold, boolean isFinanced) {
        this(null, contractDate, customerName, customerEmail, vehicleSold, isFinanced);
    }

    public SalesContract(Integer salesContractId, LocalDate contractDate, String customerName, String customerEmail, Vehicle vehicleSold, boolean isFinanced) {
        super(contractDate, customerName, customerEmail, vehicleSold);
        this.salesContractId = salesContractId;
        this.isFinanced = isFinanced;
    }

    public Integer getSalesContractId() {
        return salesContractId;
    }

    public boolean isFinanced() {
        return isFinanced;
    }

    public BigDecimal getSalesTaxAmount() {
        return getVehicleSold().getPrice().multiply(SALES_TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getRecordingFee() {
        return RECORDING_FEE;
    }

    public BigDecimal getProcessingFee() {
        return getVehicleSold().getPrice().compareTo(LOW_PRICE_THRESHOLD) < 0
                ? PROCESSING_FEE_LOW
                : PROCESSING_FEE_HIGH;
    }

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
            return BigDecimal.ZERO.setScale(2);
        }

        final BigDecimal loanAmount = getTotalPrice();
        final BigDecimal monthlyInterestRate;
        final int numberOfPayments;

        if (getVehicleSold().getPrice().compareTo(LOW_PRICE_THRESHOLD) >= 0) {
            monthlyInterestRate = LOW_INTEREST_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
            numberOfPayments = LOW_INTEREST_TERM_MONTHS;
        } else {
            monthlyInterestRate = HIGH_INTEREST_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
            numberOfPayments = HIGH_INTEREST_TERM_MONTHS;
        }

        return FinanceService.annuity(loanAmount, monthlyInterestRate, numberOfPayments);
    }

    /**
     * Serialises this contract into a pipe-separated string, suitable for CSV-style persistence.
     *
     * @return a pipe-separated data string that represents all contract fields
     */
    @Override
    public String toDataString() {
        return "";
    }

    @Override
    public ContractType getType() {
        return ContractType.SALE;
    }

    public SalesContract withId(int newId) {
        return new SalesContract(newId, getContractDate(), getCustomerName(), getCustomerEmail(), getVehicleSold(), isFinanced());
    }
}