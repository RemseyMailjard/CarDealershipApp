package com.skills4it;

import com.skills4it.dealership.data.dao.*;
import com.skills4it.dealership.model.ContractType;
import com.skills4it.dealership.service.DealershipService;
import com.skills4it.dealership.ui.UserInterface;
import org.apache.commons.dbcp2.BasicDataSource;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Car Dealership Application with Database Backend...");

        BasicDataSource dataSource = DatabaseConnector.getDataSource();

        VehicleDao vehicleDao = new JdbcVehicleDao(dataSource);
        SalesContractDao salesContractDao = new JdbcSalesContractDao(dataSource);
        LeaseContractDao leaseContractDao = new JdbcLeaseContractDao(dataSource);

        DealershipService dealershipService = new DealershipService(vehicleDao, salesContractDao, leaseContractDao);

        UserInterface userInterface = new UserInterface(dealershipService);

        userInterface.display();
      //  ContractType.SALE;
        System.out.println("Car Dealership Application has shut down. Goodbye!");
    }
}