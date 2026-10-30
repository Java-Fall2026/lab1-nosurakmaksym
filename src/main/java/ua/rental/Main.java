package ua.rental;

import ua.rental.model.ContractStatus;
import ua.rental.model.Landlord;
import ua.rental.model.LeaseContract;
import ua.rental.model.Property;
import ua.rental.model.PropertyType;
import ua.rental.model.RentPayment;
import ua.rental.util.RentalUtils;

import java.time.LocalDate;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.out.println("=== 1. Створення record-об'єктів та звернення до їхніх компонентів ===");
        Landlord landlord = new Landlord("  1234567890 ", "іван петренко", "+380501234567");
        System.out.println("Створено Landlord (Record): " + landlord.fullName() + " (ІПН: " + landlord.taxCode() + ")\n");

        System.out.println("=== 2. Робота з enum (values, власне поле, valueOf з помилкою) ===");
        System.out.print("Доступні типи нерухомості (enum values): ");
        for (PropertyType pt : PropertyType.values()) {
            System.out.print(pt.name() + "[коеф=" + pt.getTaxMultiplier() + "] ");
        }
        System.out.println();
        try {
            PropertyType.valueOf("CASTLE");
        } catch (IllegalArgumentException e) {
            System.out.println("Спроба valueOf(\"CASTLE\") відхилена: " + e.getMessage() + "\n");
        }

        System.out.println("=== 3. Демонстрація switch-виразів ===");
        Property property = Property.of(
                "CAD-73-001",
                "м. Чернівці, вул. Університетська, 28",
                landlord,
                PropertyType.COMMERCIAL,
                15000.0
        );
        LeaseContract contract = new LeaseContract(
                "CNT-2026-01",
                property,
                "максим коваленко",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 9, 1),
                15000.0,
                ContractStatus.TERMINATED
        );
        System.out.println("Switch 1 (переклад статусу): " + contract.getStatus() + " -> " + RentalUtils.translateStatus(contract.getStatus()));
        System.out.println("Switch 2 (розрахунок податку): для " + property.getType() + " податок = " + RentalUtils.calculateBaseTax(property.getType()) + "\n");

        System.out.println("=== 4. Record з невалідними даними (компактний конструктор) ===");
        try {
            new RentPayment(contract, -500.0, LocalDate.of(2026, 3, 5));
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка при створенні RentPayment: " + e.getMessage() + "\n");
        }

        System.out.println("=== 5. Порівняння двох однакових record (згенеровані equals та hashCode) ===");
        RentPayment payment1 = new RentPayment(contract, 15000.0, LocalDate.of(2026, 3, 5));
        RentPayment payment2 = new RentPayment(contract, 15000.0, LocalDate.of(2026, 3, 5));
        System.out.println("payment1.equals(payment2): " + payment1.equals(payment2));
        System.out.println("hashCode однакові: " + (payment1.hashCode() == payment2.hashCode()) + "\n");

        System.out.println("=== 6. Вивід record через згенерований toString() ===");
        System.out.println(landlord);
        System.out.println(payment1);
    }
}