package ua.rental.util;

import ua.rental.model.LeaseContract;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class RentalUtils {

    private RentalUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static <T> T requireNotNull(T value, String fieldName) {
        return ValidationHelper.requireNotNull(value, fieldName);
    }

    public static String requireValidCode(String value, String fieldName) {
        ValidationHelper.requireNotBlank(value, fieldName);
        return FormatHelper.normalize(value, true);
    }

    public static String requireValidText(String value, String fieldName) {
        ValidationHelper.requireNotBlank(value, fieldName);
        return FormatHelper.normalizeText(value);
    }

    public static String requireValidName(String value, String fieldName) {
        ValidationHelper.requireNotBlank(value, fieldName);
        return FormatHelper.capitalize(value);
    }

    public static String normalizeLowerCase(String value, String fieldName) {
        ValidationHelper.requireNotBlank(value, fieldName);
        return FormatHelper.normalize(value, false);
    }

    public static double requirePositive(double value, String fieldName) {
        return ValidationHelper.requirePositive(value, fieldName);
    }

    public static double requireInRange(double value, double min, double max, String fieldName) {
        return ValidationHelper.requireInRange(value, min, max, fieldName);
    }

    public static double requireAtLeast(double value, double min, String fieldName) {
        return ValidationHelper.requireAtLeast(value, min, fieldName);
    }

    public static String validatePropertyType(String type) {
        ValidationHelper.requireNotBlank(type, "Property type");
        String normalized = FormatHelper.normalize(type, true);
        return ValidationHelper.requireAllowedValue(
                normalized, ValidationHelper.ALLOWED_PROPERTY_TYPES, "Property type"
        );
    }

    public static String validateContractStatus(String status) {
        ValidationHelper.requireNotBlank(status, "Contract status");
        String normalized = FormatHelper.normalize(status, true);
        return ValidationHelper.requireAllowedValue(
                normalized, ValidationHelper.ALLOWED_CONTRACT_STATUSES, "Contract status"
        );
    }

    public static void validateContractDates(LocalDate startDate, LocalDate endDate) {
        ValidationHelper.requireNotNull(startDate, "Start date");
        ValidationHelper.requireNotNull(endDate, "End date");
        ValidationHelper.requireAfter(endDate, startDate, "End date", "start date");
    }

    public static void validatePaymentDate(LocalDate paymentDate, LocalDate contractStartDate) {
        ValidationHelper.requireNotNull(paymentDate, "Payment date");
        ValidationHelper.requireNotNull(contractStartDate, "Contract start date");
        ValidationHelper.requireNotBefore(paymentDate, contractStartDate, "Payment date", "contract start date");
    }

    public static String formatMoney(double amount) {
        return FormatHelper.formatMoney(amount);
    }

    public static long contractMonths(LeaseContract contract) {
        ValidationHelper.requireNotNull(contract, "Lease contract");
        long months = ChronoUnit.MONTHS.between(contract.getStartDate(), contract.getEndDate());
        return Math.max(1L, months);
    }

    public static double totalRent(LeaseContract contract) {
        ValidationHelper.requireNotNull(contract, "Lease contract");
        return contractMonths(contract) * contract.getProperty().getMonthlyRent();
    }
}