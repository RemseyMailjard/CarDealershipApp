package com.skills4it.dealership.data.dao;


import com.skills4it.dealership.model.LeaseContract;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcLeaseContractDao implements LeaseContractDao {
    private final BasicDataSource dataSource;

    public JdbcLeaseContractDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public LeaseContract create(LeaseContract contract) {
        String sql = "INSERT INTO GTA.lease_contracts_RM (vehicle_id, contract_date, customer_name, customer_email, " +
                "expected_ending_value, lease_fee, total_price, monthly_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, contract.getVehicleSold().getVehicleId());
            stmt.setDate(2, Date.valueOf(contract.getContractDate()));
            stmt.setString(3, contract.getCustomerName());
            stmt.setString(4, contract.getCustomerEmail());
            stmt.setBigDecimal(5, contract.getExpectedEndValue());
            stmt.setBigDecimal(6, contract.getLeaseFee());
            stmt.setBigDecimal(7, contract.getTotalPrice());
            stmt.setBigDecimal(8, contract.getMonthlyPayment());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    return contract.withId(newId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lease contract", e);
        }
        throw new RuntimeException("Failed to create lease contract, no ID obtained.");
    }

    @Override
    public List<LeaseContract> getAll() {
        System.out.println("DAO: `getAll` for LeaseContract not yet implemented.");
        return new ArrayList<>();
    }
}
