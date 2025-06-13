package com.skills4it.dealership.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class SalesContract extends Contract {

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
        super(contractDate, customerName, customerEmail, vehicleSold);
        this.isFinanced = isFinanced;
    }


    public BigDecimal getSalesTaxAmount() {
        return getVehicleSold().getPrice().multiply(SALES_TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getRecordingFee() {
        return RECORDING_FEE;
    }

    public BigDecimal getProcessingFee() {
        return getVehicleSold().getPrice().compareTo(LOW_PRICE_THRESHOLD) < 0 ? PROCESSING_FEE_LOW : PROCESSING_FEE_HIGH;
    }

    public boolean isFinanced() {
        return isFinanced;
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

        BigDecimal loanAmount = getTotalPrice();
        BigDecimal monthlyRate;
        int numberOfPayments;

        if (getVehicleSold().getPrice().compareTo(LOW_PRICE_THRESHOLD) >= 0) {
            monthlyRate = LOW_INTEREST_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
            numberOfPayments = LOW_INTEREST_TERM_MONTHS;
        } else {
            monthlyRate = HIGH_INTEREST_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
            numberOfPayments = HIGH_INTEREST_TERM_MONTHS;
        }


        BigDecimal rateFactor = monthlyRate.add(BigDecimal.ONE).pow(numberOfPayments);
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(rateFactor);
        BigDecimal denominator = rateFactor.subtract(BigDecimal.ONE);

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    @Override
    public String toDataString() {
        Vehicle v = getVehicleSold();
        return String.join("|",
                "SALE",
                getContractDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                getCustomerName(),
                getCustomerEmail(),
                String.valueOf(v.getVin()),
                String.valueOf(v.getYear()),
                v.getMake(),
                v.getModel(),
                String.valueOf(v.getType()),
                v.getColor(),
                String.valueOf(v.getOdometer()),
                v.getPrice().toPlainString(),
                getSalesTaxAmount().toPlainString(),
                getRecordingFee().toPlainString(),
                getProcessingFee().toPlainString(),
                getTotalPrice().toPlainString(),
                isFinanced() ? "YES" : "NO",
                getMonthlyPayment().toPlainString()
        );



    }
    @Override
    public ContractType getType() {
        return ContractType.SALE;
    }
}