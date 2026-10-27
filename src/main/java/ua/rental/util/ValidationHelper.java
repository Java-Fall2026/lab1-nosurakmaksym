package ua.rental.util;

import java.time.LocalDate;

class ValidationHelper {

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
            throw new IllegalArgumentException(fieldName + " must be greater than 0, got: " + value);
        }
        return value;
    }

    static double requireAtLeast(double value, double min, String fieldName) {
        if (Double.isNaN(value) || value < min) {
            throw new IllegalArgumentException(fieldName + " must be at least " + min + ", got: " + value);
        }
        return value;
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