package com.skills4it.dealership.data.dao;

import com.skills4it.dealership.model.Vehicle;
import com.skills4it.dealership.model.VehicleType;
import org.apache.commons.dbcp2.BasicDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the VehicleDao interface for the GTA schema.
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
        String sql = "SELECT * FROM GTA.vehicles WHERE price BETWEEN ? AND ? ORDER BY price;";
        return queryForVehicles(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByMakeModel(String make, String model) {
        String sql = "SELECT * FROM GTA.vehicles WHERE make LIKE ? AND model LIKE ? ORDER BY make, model;";
        return queryForVehicles(sql, "%" + make + "%", "%" + model + "%");
    }

    @Override
    public List<Vehicle> searchByYearRange(int min, int max) {
        String sql = "SELECT * FROM GTA.vehicles WHERE year BETWEEN ? AND ? ORDER BY year DESC;";
        return queryForVehicles(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByColor(String color) {
        String sql = "SELECT * FROM GTA.vehicles WHERE color LIKE ? ORDER BY make, model;";
        return queryForVehicles(sql, "%" + color + "%");
    }

    @Override
    public List<Vehicle> searchByMileageRange(int min, int max) {
        String sql = "SELECT * FROM GTA.vehicles WHERE odometer BETWEEN ? AND ? ORDER BY odometer;";
        return queryForVehicles(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByType(String type) {
        String sql = "SELECT * FROM GTA.vehicles WHERE vehicle_type LIKE ? ORDER BY make, model;";
        return queryForVehicles(sql, "%" + type + "%");
    }

    @Override
    public List<Vehicle> getAll() {
        String sql = "SELECT * FROM GTA.vehicles ORDER BY make, model;";
        return queryForVehicles(sql);
    }

    // --- CRUD Methods (Phase 2 & 3) ---

    @Override
    public Vehicle create(Vehicle vehicle) {
        // NOTE: This will fail with 'gtareader' user. Requires a different user with write permissions.
        System.out.println("DAO: `create` method called. This requires write permissions.");
        // TODO: Implement with a writer user.
        return null;
    }

    @Override
    public void delete(int vehicleId) {
        // NOTE: This will fail with 'gtareader' user.
        System.out.println("DAO: `delete` method called. This requires write permissions.");
        // TODO: Implement with a writer user.
    }

    // --- Private Helper Methods ---

    /**
     * A generic helper method to execute a SQL query and map the results to a list of vehicles.
     * @param sql The SQL query to execute.
     * @param params The variable-length parameters to be set on the PreparedStatement.
     * @return A list of Vehicle objects.
     */
    private List<Vehicle> queryForVehicles(String sql, Object... params) {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set parameters
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

    /**
     * Maps a single row from the ResultSet to a Vehicle object.
     * @param rs The ResultSet to process.
     * @return A populated Vehicle object.
     * @throws SQLException if a column is not found.
     */
    private Vehicle mapRowToVehicle(ResultSet rs) throws SQLException {
        // Assuming column names from a typical 'vehicles' table. Adjust if necessary.
        int vehicleId = rs.getInt("vehicle_id"); // Or "dealership_id" depending on your schema
        String vin = rs.getString("vin");
        int year = rs.getInt("year");
        String make = rs.getString("make");
        String model = rs.getString("model");
        String vehicleTypeStr = rs.getString("vehicle_type");
        String color = rs.getString("color");
        int odometer = rs.getInt("odometer");
        BigDecimal price = rs.getBigDecimal("price");

        // Convert string from DB to Enum safely
        VehicleType type = VehicleType.valueOf(vehicleTypeStr.toUpperCase().replace(" ", "_"));

        // Use the constructor that accepts an ID
        return new Vehicle(vehicleId, vin, year, make, model, type, color, odometer, price);
    }
}