package ua.rental.model;

import ua.rental.util.RentalUtils;
import java.time.LocalDate;

public record RentPayment(LeaseContract contract, double amount, LocalDate paymentDate) {
    public RentPayment {
        RentalUtils.requireNotNull(contract, "Lease contract");
        RentalUtils.requirePositive(amount, "Payment amount");
        RentalUtils.requireNotNull(paymentDate, "Payment date");
        RentalUtils.validatePaymentDate(paymentDate, contract.getStartDate());
    }
}