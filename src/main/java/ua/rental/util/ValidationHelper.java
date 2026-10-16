package ua.rental.util;

import java.time.LocalDate;
import java.util.List;

class ValidationHelper {

    static final List<String> ALLOWED_PROPERTY_TYPES = List.of(
            "APARTMENT", "HOUSE", "COMMERCIAL", "GARAGE"
    );

    static final List<String> ALLOWED_CONTRACT_STATUSES = List.of(
            "ACTIVE", "TERMINATED", "EXPIRED"
    );

    private ValidationHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static <T> T requireNotNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null, got: null");
        }
        return value;
    }

    static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank, got: " + (value == null ? "null" : "\"" + value + "\"")
            );
        }
        return value;
    }

    static double requirePositive(double value, String fieldName) {
        if (Double.isNaN(value) || value <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than 0, got: " + value
            );
        }
        return value;
    }

    static double requireInRange(double value, double min, double max, String fieldName) {
        if (Double.isNaN(value) || value < min || value > max) {
            throw new IllegalArgumentException(
                    fieldName + " must be between " + min + " and " + max + ", got: " + value
            );
        }
        return value;
    }

    static double requireAtLeast(double value, double min, String fieldName) {
        if (Double.isNaN(value) || value < min) {
            throw new IllegalArgumentException(
                    fieldName + " must be at least " + min + ", got: " + value
            );
        }
        return value;
    }

    static String requireAllowedValue(String normalizedValue, List<String> allowedValues, String fieldName) {
        if (!allowedValues.contains(normalizedValue)) {
            throw new IllegalArgumentException(
                    fieldName + " must be one of " + allowedValues + ", got: \"" + normalizedValue + "\""
            );
        }
        return normalizedValue;
    }

    static void requireAfter(LocalDate date, LocalDate referenceDate, String fieldName, String referenceName) {
        if (!date.isAfter(referenceDate)) {
            throw new IllegalArgumentException(
                    fieldName + " must be strictly after " + referenceName + " (" + referenceDate + "), got: " + date
            );
        }
    }

    static void requireNotBefore(LocalDate date, LocalDate referenceDate, String fieldName, String referenceName) {
        if (date.isBefore(referenceDate)) {
            throw new IllegalArgumentException(
                    fieldName + " must not be before " + referenceName + " (" + referenceDate + "), got: " + date
            );
        }
    }
}