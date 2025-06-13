package com.skills4it.dealership.service;

import com.skills4it.dealership.model.LeaseContract;
import com.skills4it.dealership.model.SalesContract;
import com.skills4it.dealership.model.Vehicle;
import java.time.LocalDate;

/**
 * Een utility-klasse (Factory Pattern) die verantwoordelijk is voor het creëren
 * van specifieke Contract-subtypes.
 */
public final class ContractFactory { // <<--- FIX 1: Naam van de klasse is nu correct.

    /**
     * Private constructor om instantiatie te voorkomen.
     * De naam is nu 'ContractFactory', identiek aan de klassenaam.
     * Er is geen return type (zoals 'void').
     */
    private ContractFactory() { // <<--- FIX 2: Dit is nu een correcte constructor.
        /* Deze constructor blijft leeg. */
    }

    /**
     * Creëert een {@link SalesContract}.
     */
    public static SalesContract createSale(LocalDate date,
                                           String customerName,
                                           String customerEmail,
                                           Vehicle vehicleSold,
                                           boolean financed) {

        return new SalesContract(date, customerName, customerEmail, vehicleSold, financed);
    }

    /**
     * Creëert een {@link LeaseContract}.
     */
    public static LeaseContract createLease(LocalDate date,
                                            String customerName,
                                            String customerEmail,
                                            Vehicle vehicleSold) {

        return new LeaseContract(date, customerName, customerEmail, vehicleSold);
    }
}