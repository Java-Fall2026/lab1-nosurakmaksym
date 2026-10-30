package ua.rental.model;

import ua.common.BaseEntity;
import ua.rental.util.RentalUtils;

import java.util.Objects;

public class Property extends BaseEntity {

    private final String cadastralNumber;
    private final String address;
    private final Landlord landlord;
    private final PropertyType type;
    private double monthlyRent;

    private Property(String cadastralNumber, String address, Landlord landlord, PropertyType type, double monthlyRent) {
        super();
        this.cadastralNumber = RentalUtils.requireValidCode(cadastralNumber, "Cadastral number");
        this.address = RentalUtils.requireValidText(address, "Address");
        this.landlord = RentalUtils.requireNotNull(landlord, "Landlord");
        this.type = RentalUtils.requireNotNull(type, "Property type");
        setMonthlyRent(monthlyRent);
    }

    public static Property of(String cadastralNumber, String address, Landlord landlord, PropertyType type, double monthlyRent) {
        return new Property(cadastralNumber, address, landlord, type, monthlyRent);
    }

    public final void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = RentalUtils.requirePositive(monthlyRent, "Monthly rent");
    }

    public String getCadastralNumber() { return cadastralNumber; }
    public String getAddress() { return address; }
    public Landlord getLandlord() { return landlord; }
    public PropertyType getType() { return type; }
    public double getMonthlyRent() { return monthlyRent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(cadastralNumber, property.cadastralNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cadastralNumber);
    }

    @Override
    public String toString() {
        return "Property{" +
                "cadastralNumber='" + cadastralNumber + '\'' +
                ", address='" + address + '\'' +
                ", landlord=" + landlord.fullName() + " (" + landlord.taxCode() + ")" +
                ", type=" + type +
                ", monthlyRent=" + RentalUtils.formatMoney(monthlyRent) +
                ", createdAt=" + createdAt +
                '}';
    }
}