package com.skills4it.dealership.data.dao;



import com.skills4it.dealership.model.SalesContract;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSalesContractDao implements SalesContractDao {
    private final BasicDataSource dataSource;

    public JdbcSalesContractDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public SalesContract create(SalesContract contract) {
        String sql = "INSERT INTO GTA.sales_contracts_RM (vehicle_id, contract_date, customer_name, customer_email, is_financed, " +
                "sales_tax_amount, recording_fee, processing_fee, total_price, monthly_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, contract.getVehicleSold().getVehicleId());
            stmt.setDate(2, Date.valueOf(contract.getContractDate()));
            stmt.setString(3, contract.getCustomerName());
            stmt.setString(4, contract.getCustomerEmail());
            stmt.setBoolean(5, contract.isFinanced());
            stmt.setBigDecimal(6, contract.getSalesTaxAmount());
            stmt.setBigDecimal(7, contract.getRecordingFee());
            stmt.setBigDecimal(8, contract.getProcessingFee());
            stmt.setBigDecimal(9, contract.getTotalPrice());
            stmt.setBigDecimal(10, contract.getMonthlyPayment());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    return contract.withId(newId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create sales contract", e);
        }
        throw new RuntimeException("Failed to create sales contract, no ID obtained.");
    }

    @Override
    public List<SalesContract> getAll() {
        System.out.println("DAO: `getAll` for SalesContract not yet implemented.");
        return new ArrayList<>();
    }
}