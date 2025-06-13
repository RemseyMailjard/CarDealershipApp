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
 * JDBC implementation of the VehicleDao interface for the GTA schema.
 * Connects to the database to perform CRUD operations on the 'vehicles_RM' table.
 */
public class JdbcVehicleDao implements VehicleDao {

    private final BasicDataSource dataSource;

    public JdbcVehicleDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- Search Methods (Phase 1) ---

    @Override
    public List<Vehicle> searchByPriceRange(BigDecimal min, BigDecimal max) {
        String sql = "SELECT * FROM GTA.vehicles_RM WHERE price BETWEEN ? AND ? ORDER BY price;";
        return queryForVehicleList(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByMakeModel(String make, String model) {
        String sql = "SELECT * FROM GTA.vehicles_RM WHERE make LIKE ? AND model LIKE ? ORDER BY make, model;";
        return queryForVehicleList(sql, "%" + make + "%", "%" + model + "%");
    }

    @Override
    public List<Vehicle> searchByYearRange(int min, int max) {
        String sql = "SELECT * FROM GTA.vehicles_RM WHERE year BETWEEN ? AND ? ORDER BY year DESC;";
        return queryForVehicleList(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByColor(String color) {
        String sql = "SELECT * FROM GTA.vehicles_RM WHERE color LIKE ? ORDER BY make, model;";
        return queryForVehicleList(sql, "%" + color + "%");
    }

    @Override
    public List<Vehicle> searchByMileageRange(int min, int max) {
        String sql = "SELECT * FROM GTA.vehicles_RM WHERE odometer BETWEEN ? AND ? ORDER BY odometer;";
        return queryForVehicleList(sql, min, max);
    }

    @Override
    public List<Vehicle> searchByType(String type) {
        String sql = "SELECT * FROM GTA.vehicles_RM WHERE vehicle_type LIKE ? ORDER BY make, model;";
        return queryForVehicleList(sql, "%" + type + "%");
    }

    @Override
    public List<Vehicle> getAll() {
        String sql = "SELECT * FROM GTA.vehicles_RM ORDER BY make, model;";
        return queryForVehicleList(sql);
    }

    @Override
    public Optional<Vehicle> findByVin(String vin) {
        String sql = "SELECT * FROM GTA.vehicles_RM WHERE vin = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, vin);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query for vehicle by VIN failed", e);
        }
        return Optional.empty();
    }

    // --- CRUD Methods (Phase 2 & 3) ---

    @Override
    public Vehicle create(Vehicle vehicle) {
        // NOTE: This will fail with the 'gtareader' user. Requires a different user with write permissions.
        String sql = "INSERT INTO GTA.vehicles_RM (vin, year, make, model, vehicle_type, color, odometer, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, vehicle.getVin());
            statement.setInt(2, vehicle.getYear());
            statement.setString(3, vehicle.getMake());
            statement.setString(4, vehicle.getModel());
            statement.setString(5, vehicle.getType().name());
            statement.setString(6, vehicle.getColor());
            statement.setInt(7, vehicle.getOdometer());
            statement.setBigDecimal(8, vehicle.getPrice());

            statement.executeUpdate();

            // Retrieve the auto-generated ID from the database
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    // Return a new Vehicle instance that includes the database ID
                    return vehicle.withId(newId);
                } else {
                    throw new SQLException("Creating vehicle failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create vehicle", e);
        }
    }

    @Override
    public void delete(int vehicleId) {
        // NOTE: This will fail with the 'gtareader' user. Requires write permissions.
        String sql = "DELETE FROM GTA.vehicles_RM WHERE vehicle_id = ?;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, vehicleId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                System.err.println("WARN: Deleting vehicle with ID " + vehicleId + " affected 0 rows. It might not have existed.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete vehicle with ID " + vehicleId, e);
        }
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