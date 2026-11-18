package ua.rental.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.rental.exception.InvalidDataException;
import ua.rental.model.Landlord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LandlordLoader {

    private static final Logger log = LoggerFactory.getLogger(LandlordLoader.class);

    public List<Landlord> loadFromFile(Path filePath) {
        List<Landlord> landlords = new ArrayList<>();
        log.info("Starting to load landlords from file: {}", filePath);

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                log.warn("File {} is completely empty", filePath);
                return landlords;
            }

            String line;
            int lineNumber = 1; 

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Landlord landlord = parseLine(line, lineNumber);
                    landlords.add(landlord);
                } catch (InvalidDataException e) {
                    log.warn("Skipping invalid record: {}", e.getMessage());
                }
            }
            log.info("Successfully loaded {} valid landlords from {}", landlords.size(), filePath);

        } catch (IOException e) {
            log.error("Failed to read file {}: {}", filePath, e.getMessage(), e);
        } finally {
            log.info("Finished file processing routine for: {}", filePath);
        }

        return landlords;
    }

    public Landlord parseLine(String line, int lineNumber) throws InvalidDataException {
        try {
            String[] parts = line.split(",", -1);
            if (parts.length < 3) {
                throw new IndexOutOfBoundsException("Expected 3 columns, got " + parts.length);
            }
            return new Landlord(parts[0], parts[1], parts[2]);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidDataException("Invalid data at line " + lineNumber + ": " + e.getMessage(), e);
        }
    }
}