package ua.rental.model;

public enum PropertyType {
    APARTMENT(1.0),
    HOUSE(1.5),
    COMMERCIAL(2.5),
    GARAGE(0.5);

    private final double taxMultiplier;

    PropertyType(double taxMultiplier) {
        this.taxMultiplier = taxMultiplier;
    }

    public double getTaxMultiplier() {
        return taxMultiplier;
    }
}