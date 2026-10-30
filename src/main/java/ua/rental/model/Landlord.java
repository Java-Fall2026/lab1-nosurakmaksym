package ua.rental.model;

import ua.rental.util.RentalUtils;

public record Landlord(String taxCode, String fullName, String phone) {
    public Landlord {
        taxCode = RentalUtils.requireValidCode(taxCode, "Tax code");
        fullName = RentalUtils.requireValidName(fullName, "Full name");
        phone = RentalUtils.requireValidText(phone, "Phone");
    }
}