package com.skills4it.dealership.data.dao;


import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages the database connection pool using a Singleton pattern.
 * This class reads database configuration from a properties file and provides
 * a single, shared DataSource for the entire application.
 */
public class DatabaseConnector {

    private static BasicDataSource dataSource;

    // Private constructor to prevent instantiation
    private DatabaseConnector() {}

    /**
     * Returns the singleton instance of the DataSource.
     * The DataSource is initialized on the first call.
     *
     * @return The configured BasicDataSource instance.
     */
    public static BasicDataSource getDataSource() {
        if (dataSource == null) {
            initializeDataSource();
        }
        return dataSource;
    }

    private static void initializeDataSource() {
        try {
            // Load database properties from the classpath
            InputStream input = DatabaseConnector.class.getResourceAsStream("/db.properties");
            if (input == null) {
                throw new IOException("Unable to find db.properties in classpath");
            }
            Properties props = new Properties();
            props.load(input);

            // Create and configure the connection pool
            dataSource = new BasicDataSource();
            dataSource.setUrl(props.getProperty("db.url"));
            dataSource.setUsername(props.getProperty("db.user"));
            dataSource.setPassword(props.getProperty("db.password"));
            dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Configure pool settings
            dataSource.setMinIdle(5);
            dataSource.setMaxIdle(10);
            dataSource.setMaxOpenPreparedStatements(100);

        } catch (IOException e) {
            // Use a runtime exception because a missing db.properties is a fatal configuration error
            throw new RuntimeException("Failed to initialize DataSource", e);
        }
    }
}