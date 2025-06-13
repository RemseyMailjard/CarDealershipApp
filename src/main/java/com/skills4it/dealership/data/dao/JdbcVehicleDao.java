package com.skills4it.dealership.data.dao;

import com.skills4it.dealership.model.Vehicle;
import com.skills4it.dealership.model.VehicleType;
import org.apache.commons.dbcp2.BasicDataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of the VehicleDao interface for the Marc schema.
 * Connects to the database to perform CRUD operations on vehicles.
 */
public class JdbcVehicleDao implements VehicleDao {

    private final BasicDataSource dataSource;

    public JdbcVehicleDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- Search Methods (Phase 1) ---

    @Override
    public List<Vehicle> searchByPriceRange(BigDecimal min, BigDecimal max) {
        String sql = "SELECT * FROM Marc.vehicles WHERE price BETWEEN ? AND ? ORDER BY price;";
        return queryForVehicleList(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByMakeModel(String make, String model) {
        String sql = "SELECT * FROM Marc.vehicles WHERE make LIKE ? AND model LIKE ? ORDER BY make, model;";
        return queryForVehicleList(sql, "%" + make + "%", "%" + model + "%");
    }

    @Override
    public List<Vehicle> searchByYearRange(int min, int max) {
        String sql = "SELECT * FROM Marc.vehicles WHERE year BETWEEN ? AND ? ORDER BY year DESC;";
        return queryForVehicleList(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByColor(String color) {
        String sql = "SELECT * FROM Marc.vehicles WHERE color LIKE ? ORDER BY make, model;";
        return queryForVehicleList(sql, "%" + color + "%");
    }

    @Override
    public List<Vehicle> searchByMileageRange(int min, int max) {
        String sql = "SELECT * FROM Marc.vehicles WHERE odometer BETWEEN ? AND ? ORDER BY odometer;";
        return queryForVehicleList(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByType(String type) {
        String sql = "SELECT * FROM Marc.vehicles WHERE vehicle_type LIKE ? ORDER BY make, model;";
        return queryForVehicleList(sql, "%" + type + "%");
    }

    @Override
    public List<Vehicle> getAll() {
        String sql = "SELECT * FROM Marc.vehicles ORDER BY make, model;";
        return queryForVehicleList(sql);
    }

    // --- FIX: Implementation for the missing method from the VehicleDao interface ---
    @Override
    public Optional<Vehicle> findByVin(String vin) {
        String sql = "SELECT * FROM Marc.vehicles WHERE vin = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, vin);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // Found a vehicle, map it and wrap it in an Optional
                    return Optional.of(mapRowToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query for vehicle by VIN failed", e);
        }
        // If we reach here, no vehicle was found
        return Optional.empty();
    }
    // --- End of Fix ---


    // --- CRUD Methods (Phase 2 & 3) ---

    @Override
    public Vehicle create(Vehicle vehicle) {
        System.out.println("DAO: `create` method called. This requires write permissions.");
        // TODO: Implement with a writer user and retrieve the generated key.
        return null;
    }

    @Override
    public void delete(int vehicleId) {
        System.out.println("DAO: `delete` method called. This requires write permissions.");
        // TODO: Implement with a writer user.
    }

    // --- Private Helper Methods ---

    private List<Vehicle> queryForVehicleList(String sql, Object... params) {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapRowToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query for vehicles failed", e);
        }
        return vehicles;
    }

    private Vehicle mapRowToVehicle(ResultSet rs) throws SQLException {
        int vehicleId = rs.getInt("vehicle_id");
        String vin = rs.getString("vin");
        int year = rs.getInt("year");
        String make = rs.getString("make");
        String model = rs.getString("model");
        String vehicleTypeStr = rs.getString("vehicle_type");
        String color = rs.getString("color");
        int odometer = rs.getInt("odometer");
        BigDecimal price = rs.getBigDecimal("price");

        VehicleType type = VehicleType.valueOf(vehicleTypeStr.toUpperCase().replace(" ", "_"));

        return new Vehicle(vehicleId, vin, year, make, model, type, color, odometer, price);
    }
}