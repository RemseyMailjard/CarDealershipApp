package com.skills4it.dealership.io;

import com.skills4it.dealership.model.*;
import com.skills4it.dealership.service.ContractFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Persists {@link Contract}s to a plain UTF-8 text file and loads them back.
 * <p>
 *     Each line is a pipe-separated record produced by
 *     {@link Contract#toDataString()}. Two layouts are recognised:
 * </p>
 * <ul>
 *     <li>TYPE-first &nbsp;→ {@code SALE|YYYYMMDD|…}</li>
 *     <li>Date-first → {@code 20250101|LEASE|…}</li>
 * </ul>
 */
public class ContractFileManager {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final Path file;   // Path is configurable for easy testing

    /* ───────── constructors ───────── */

    public ContractFileManager() {
        this(Path.of("contracts.txt"));
    }

    public ContractFileManager(Path file) {
        this.file = Objects.requireNonNull(file);
    }

    /* ───────── save operations ───────── */

    /** Appends a single contract to the file. */
    public void save(Contract c) throws IOException {
        Files.writeString(
                file,
                c.toDataString() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    /** Writes all contracts, replacing the existing file. */
    public void saveAll(Collection<Contract> contracts) throws IOException {
        String joined = contracts.stream()
                .map(Contract::toDataString)
                .collect(Collectors.joining(System.lineSeparator()));

        Files.writeString(
                file,
                joined,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /* ───────── load operations ───────── */

    /** Loads every contract in the file. Returns an empty list if the file is missing. */
    public List<Contract> loadAll() throws IOException {
        if (Files.notExists(file)) return List.of();

        try (var lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines
                    .map(this::parseLineSafely)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }

    /* ───────── line-level parsing ───────── */

    private Contract parseLineSafely(String line) {
        try {
            return parseLine(line);
        } catch (Exception ex) {
            // Corrupt line: log & skip
            System.err.println("Skipping malformed line: " + line + " (" + ex.getMessage() + ")");
            return null;
        }
    }

    private Contract parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 2) throw new IllegalArgumentException("Too few columns");

        boolean typeFirst = p[0].equalsIgnoreCase("SALE") || p[0].equalsIgnoreCase("LEASE");
        ContractType type = ContractType.valueOf(typeFirst ? p[0] : p[1]);

        return switch (type) {
            case SALE  -> parseSale(p, typeFirst);
            case LEASE -> parseLease(p, typeFirst);
        };
    }

    /* ───────── SALE parser ───────── */

    private SalesContract parseSale(String[] p, boolean typeFirst) {
        int base = typeFirst ? 0 : 1;         // column offset if date comes first
        int dateIdx = typeFirst ? 1 : 0;

        LocalDate   date       = LocalDate.parse(p[dateIdx], DATE_FMT);
        String      name       = p[base + 2];
        String      email      = p[base + 3];
        String      vin        = p[base + 4];
        int         year       = Integer.parseInt(p[base + 5]);
        String      make       = p[base + 6];
        String      model      = p[base + 7];
        VehicleType vType      = VehicleType.valueOf(p[base + 8]);
        String      color      = p[base + 9];
        int         odo        = Integer.parseInt(p[base + 10]);
        BigDecimal  price      = new BigDecimal(p[base + 11]);
        boolean     financed   = "YES".equalsIgnoreCase(p[base + 16]);

        Vehicle vehicle = new Vehicle(vin, year, make, model, vType, color, odo, price);
        return ContractFactory.createSale(date, name, email, vehicle, financed);
    }

    /* ───────── LEASE parser ───────── */

    private LeaseContract parseLease(String[] p, boolean typeFirst) {
        int dateIdx = typeFirst ? 1 : 0;
        int base    = typeFirst ? 0 : 1;

        LocalDate date   = LocalDate.parse(p[dateIdx], DATE_FMT);
        String    name   = p[base + 2];
        String    email  = p[base + 3];
        String    vin    = p[base + 4];

        // Vehicle details are not stored – create a placeholder with VIN only
        Vehicle placeholder = new Vehicle(
                vin,
                0,
                "UNKNOWN",
                "UNKNOWN",
                VehicleType.CAR,
                "UNKNOWN",
                0,
                BigDecimal.ZERO);

        return ContractFactory.createLease(date, name, email, placeholder);
    }
}
