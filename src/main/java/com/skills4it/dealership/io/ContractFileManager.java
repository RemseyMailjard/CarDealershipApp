package com.skills4it.dealership.io;

import com.skills4it.dealership.model.*;
import com.skills4it.dealership.service.ContractFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Persists {@link Contract}s to a plain UTF-8 text file and loads them back.
 * <p>
 * This class handles reading from and writing to a data source, abstracting
 * the persistence details away from the main application logic.
 * </p>
 */
public class ContractFileManager {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final Path filePath;

    // --- Constructors ---

    /**
     * Default constructor. Automatically locates 'contracts.csv' in the resources folder.
     */
    public ContractFileManager() {
        this(getDefaultPath());
    }

    /**
     * Constructor for explicit path injection, mainly for testing purposes.
     * @param filePath The path to the contracts file.
     */
    public ContractFileManager(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath);
    }

    // --- Save Operations ---

    /**
     * Appends a single contract to the file, creating the file if it doesn't exist.
     * @param contract The contract to save.
     * @throws IOException if a writing error occurs.
     */
    public void save(Contract contract) throws IOException {
        Files.writeString(
                filePath,
                contract.toDataString() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    /**
     * Writes a collection of contracts, completely overwriting the existing file.
     * @param contracts The collection of contracts to save.
     * @throws IOException if a writing error occurs.
     */
    public void saveAll(Collection<Contract> contracts) throws IOException {
        String joined = contracts.stream()
                .map(Contract::toDataString)
                .collect(Collectors.joining(System.lineSeparator()));

        Files.writeString(
                filePath,
                joined,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    // --- Load Operations ---

    /**
     * Loads all contracts from the file.
     * @return A List of all valid contracts found in the file. Returns an empty list if the file is missing.
     * @throws IOException if a reading error occurs.
     */
    public List<Contract> loadAll() throws IOException {
        if (Files.notExists(filePath)) {
            return List.of(); // Return empty list, not an error.
        }

        try (var lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            return lines
                    .map(this::parseLineSafely)
                    .filter(Objects::nonNull) // Filter out any lines that failed to parse.
                    .toList();
        }
    }

    // --- Line-level Parsing Logic ---

    /**
     * A safe wrapper around the main parser that catches exceptions for a single line.
     * This prevents one corrupt line from crashing the entire loading process.
     * @param line The line of text from the file.
     * @return A Contract object if parsing succeeds, otherwise null.
     */
    private Contract parseLineSafely(String line) {
        try {
            // Skip empty or blank lines
            if (line == null || line.isBlank()) {
                return null;
            }
            return parseLine(line);
        } catch (Exception ex) {
            System.err.println("Skipping malformed line: \"" + line + "\" | Error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Parses a single pipe-separated line into a Contract object.
     * @param line The line of text to parse.
     * @return Either a SalesContract or a LeaseContract.
     * @throws IllegalArgumentException if the line format is invalid.
     */
    private Contract parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 2) {
            throw new IllegalArgumentException("Too few columns to determine contract type.");
        }

        // The first token is always the contract type in the provided data.
        ContractType type = ContractType.valueOf(p[0].toUpperCase());

        return switch (type) {
            case SALE  -> parseSale(p);
            case LEASE -> parseLease(p);
        };
    }

    // --- Specific Contract Parsers ---

    private SalesContract parseSale(String[] p) {
        if (p.length < 18) throw new IllegalArgumentException("SalesContract data has incorrect number of columns.");

        LocalDate   date       = LocalDate.parse(p[1], DATE_FMT);
        String      name       = p[2];
        String      email      = p[3];
        String      vin        = p[4];
        int         year       = Integer.parseInt(p[5]);
        String      make       = p[6];
        String      model      = p[7];
        VehicleType vType      = VehicleType.valueOf(p[8].toUpperCase());
        String      color      = p[9];
        int         odo        = Integer.parseInt(p[10]);
        BigDecimal  price      = new BigDecimal(p[11]);
        boolean     financed   = "YES".equalsIgnoreCase(p[16]);

        Vehicle vehicle = new Vehicle(vin, year, make, model, vType, color, odo, price);
        return ContractFactory.createSale(date, name, email, vehicle, financed);
    }

    private LeaseContract parseLease(String[] p) {
        if (p.length < 12) throw new IllegalArgumentException("LeaseContract data has incorrect number of columns.");

        LocalDate   date       = LocalDate.parse(p[1], DATE_FMT);
        String      name       = p[2];
        String      email      = p[3];
        String      vin        = p[4];
        int         year       = Integer.parseInt(p[5]);
        String      make       = p[6];
        String      model      = p[7];
        VehicleType vType      = VehicleType.valueOf(p[8].toUpperCase());
        String      color      = p[9];
        int         odo        = Integer.parseInt(p[10]);
        BigDecimal  price      = new BigDecimal(p[11]);

        Vehicle vehicle = new Vehicle(vin, year, make, model, vType, color, odo, price);
        return ContractFactory.createLease(date, name, email, vehicle);
    }

    // --- Helper for Resource Loading ---

    /**
     * Helper method to locate the default contracts file in the classpath resources.
     * @return The Path to the resource file.
     * @throws RuntimeException if the resource URL cannot be converted to a Path.
     */
    private static Path getDefaultPath() {
        try {
            URL fileUrl = ContractFileManager.class.getResource("/contracts.csv");
            if (fileUrl == null) {
                System.err.println("WARNING: 'contracts.csv' not found in resources. A new file will be created in the project root.");
                return Path.of("contracts.csv");
            }
            return Path.of(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not initialize a path from the resource URL.", e);
        }
    }
}