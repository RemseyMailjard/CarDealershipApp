package com.skills4it.dealership.service;

import com.skills4it.dealership.model.*;   // ← haalt o.a. Vehicle, SalesContract, LeaseContract binnen
import java.time.LocalDate;

public final class ContractFactoryManager {

    private ContractFactory() { /* utility class – no instances */ }

    /** Creates a SalesContract (SALE). */
    public static SalesContract createSale(LocalDate date,
                                           String  customerName,
                                           String  customerEmail,
                                           Vehicle vehicleSold,
                                           boolean financed) {

        return new SalesContract(date, customerName, customerEmail, vehicleSold, financed);
    }

    /** Creates a LeaseContract (LEASE). */
    public static LeaseContract createLease(LocalDate date,
                                            String  customerName,
                                            String  customerEmail,
                                            Vehicle vehicleSold) {

        return new LeaseContract(date, customerName, customerEmail, vehicleSold);
    }
}
