package com.skills4it.dealership.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/** Financial utilities shared by multiple contract types. */
public final class FinanceService {

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    private FinanceService() { /* static only */ }

    /**
     * Calculates an annuity payment:  P × r / (1 − (1+r)^-n)
     *
     * @param principal   loan amount
     * @param monthlyRate monthly interest rate (e.g. 0.004375)
     * @param months      term length
     * @return rounded to cents
     */
    public static BigDecimal annuity(BigDecimal principal,
                                     BigDecimal monthlyRate,
                                     int months) {

        BigDecimal onePlusRPowerN = BigDecimal.ONE.add(monthlyRate, MC).pow(months, MC);
        BigDecimal numerator      = principal.multiply(monthlyRate, MC).multiply(onePlusRPowerN, MC);
        BigDecimal denominator    = onePlusRPowerN.subtract(BigDecimal.ONE, MC);

        return denominator.signum() == 0
                ? BigDecimal.ZERO
                : numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
