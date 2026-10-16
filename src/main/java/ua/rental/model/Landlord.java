package ua.rental.model;

import ua.common.BaseEntity;
import ua.rental.util.RentalUtils;

import java.util.Objects;

public class Landlord extends BaseEntity {

    private final String taxCode;
    private final String fullName;
    private final String phone;

    public Landlord(String taxCode, String fullName, String phone) {
        super();
        this.taxCode = RentalUtils.requireValidCode(taxCode, "Tax code");
        this.fullName = RentalUtils.requireValidName(fullName, "Full name");
        this.phone = RentalUtils.requireValidText(phone, "Phone");
    }

    public String getTaxCode() {
        return taxCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landlord landlord = (Landlord) o;
        return Objects.equals(taxCode, landlord.taxCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxCode);
    }

    @Override
    public String toString() {
        return "Landlord{" +
                "taxCode='" + taxCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}