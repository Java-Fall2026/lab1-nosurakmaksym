package ua.rental;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.rental.exception.InvalidDataException;
import ua.rental.io.LandlordLoader;
import ua.rental.model.ContractStatus;
import ua.rental.model.Landlord;
import ua.rental.model.LeaseContract;
import ua.rental.model.Property;
import ua.rental.model.PropertyType;
import ua.rental.util.RentalUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RentalSystemTest {

    private final LandlordLoader loader = new LandlordLoader();

    @Test
    @DisplayName("Should successfully create Landlord record and normalize fields")
    void shouldCreateValidLandlord() {
        Landlord landlord = new Landlord(" 1234567890 ", "іван петренко", "+380501234567");
        assertEquals("1234567890", landlord.taxCode());
        assertEquals("Іван Петренко", landlord.fullName());
    }

    @Test
    @DisplayName("Should parse valid CSV line into Landlord record")
    void shouldParseValidCsvLine() throws InvalidDataException {
        Landlord landlord = loader.parseLine("9876543210,олена коваленко,+380671112233", 2);
        assertEquals("9876543210", landlord.taxCode());
        assertEquals("Олена Коваленко", landlord.fullName());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when CSV line has missing columns")
    void shouldThrowExceptionOnMissingColumns() {
        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> loader.parseLine("1234567890,Іван Петренко", 4)
        );
        assertTrue(exception.getMessage().contains("line 4"));
        assertInstanceOf(IndexOutOfBoundsException.class, exception.getCause());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when CSV line contains blank tax code")
    void shouldThrowExceptionOnBlankFieldInCsv() {
        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> loader.parseLine("   ,Іван Петренко,+380501234567", 5)
        );
        assertTrue(exception.getMessage().contains("line 5"));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Should correctly calculate contract duration in months and total rent")
    void shouldCalculateContractMonthsAndTotalRent() {
        Landlord landlord = new Landlord("1234567890", "Іван Петренко", "+380501234567");
        Property property = Property.of("CAD-01", "Київ", landlord, PropertyType.APARTMENT, 10000.0);
        LeaseContract contract = new LeaseContract(
                "CNT-01", property, "Петро Іванов",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 7, 1),
                10000.0, ContractStatus.ACTIVE
        );

        assertEquals(6L, RentalUtils.contractMonths(contract));
        assertEquals(60000.0, RentalUtils.totalRent(contract), 0.001);
    }

    @Test
    @DisplayName("Should calculate base tax correctly using switch expression")
    void shouldCalculateBaseTaxByPropertyType() {
        assertEquals(1000.0, RentalUtils.calculateBaseTax(PropertyType.APARTMENT), 0.001);
        assertEquals(3000.0, RentalUtils.calculateBaseTax(PropertyType.COMMERCIAL), 0.001);
        assertEquals(200.0, RentalUtils.calculateBaseTax(PropertyType.GARAGE), 0.001);
    }

    @Test
    @DisplayName("Should reject negative monthly rent in Property setter")
    void shouldThrowExceptionOnNegativeRentInSetter() {
        Landlord landlord = new Landlord("1234567890", "Іван Петренко", "+380501234567");
        Property property = Property.of("CAD-01", "Київ", landlord, PropertyType.HOUSE, 15000.0);

        assertThrows(IllegalArgumentException.class, () -> property.setMonthlyRent(-100.0));
    }
}