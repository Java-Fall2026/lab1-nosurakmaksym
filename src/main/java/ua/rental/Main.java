package ua.rental;

import ua.rental.model.Landlord;
import ua.rental.model.LeaseContract;
import ua.rental.model.Property;
import ua.rental.model.RentPayment;
import ua.rental.util.RentalUtils;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        System.out.println("1. Створення об'єктів конструктором та фабричним методом");
        Landlord landlord = new Landlord("  1234567890 ", "іван петренко", "+380501234567");
        Property property = Property.of(
                "  cad-73-001 ",
                "м. Чернівці, вул. Університетська, 28",
                landlord,
                "  apartment ",
                15000.0
        );
        LeaseContract contract = new LeaseContract(
                "  cnt-2026-01 ",
                property,
                "максим коваленко",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 9, 1),
                15000.0,
                " active "
        );
        RentPayment payment = RentPayment.of(contract, 15000.0, LocalDate.of(2026, 3, 5));
        System.out.println("Усі об'єкти успішно створено.\n");

        System.out.println("2. Нормалізація в дії");
        System.out.println("Вхідний номер договору \"  cnt-2026-01 \" -> \"" + contract.getContractNumber() + "\"");
        System.out.println("Вхідний тип об'єкта \"  apartment \" -> \"" + property.getType() + "\"");
        System.out.println("Вхідне ім'я \"іван петренко\" -> \"" + landlord.getFullName() + "\"");
        System.out.println("Нормалізація email \"  Ivan@MAIL.com \" -> \""
                + RentalUtils.normalizeLowerCase("  Ivan@MAIL.com ", "Email") + "\"\n");

        System.out.println("3. Три різні порушення правил валідації");
        try {
            Property.of("CAD-002", "вул. Головна, 10", landlord, "HOUSE", -5000.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка 1 (очікувано): " + e.getMessage());
        }

        try {
            Property.of("CAD-003", "вул. Головна, 12", landlord, "CASTLE", 10000.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка 2 (очікувано): " + e.getMessage());
        }

        try {
            new LeaseContract(
                    "CNT-2026-02",
                    property,
                    "Олег Бойко",
                    LocalDate.of(2026, 4, 1),
                    LocalDate.of(2026, 10, 1),
                    10000.0,
                    "ACTIVE"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка 3 (очікувано): " + e.getMessage());
        }
        System.out.println();

        System.out.println("4. Спроба зіпсувати коректний об'єкт через сеттер");
        try {
            property.setMonthlyRent(0.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Спроба встановити monthlyRent = 0.0 відхилена: " + e.getMessage());
            System.out.println("Поточна орендна плата залишилась незмінною: " + property.getMonthlyRent());
        }
        try {
            contract.setStatus("PENDING");
        } catch (IllegalArgumentException e) {
            System.out.println("Спроба встановити некоректний статус відхилена: " + e.getMessage());
            System.out.println("Поточний статус договору: " + contract.getStatus());
        }
        System.out.println();

        System.out.println("5. Порівняння об'єктів: ==, equals() та hashCode()");
        RentPayment samePayment = RentPayment.of(contract, 18000.0, LocalDate.of(2026, 3, 5));
        RentPayment differentPayment = RentPayment.of(contract, 15000.0, LocalDate.of(2026, 4, 5));

        System.out.println("Два платежі з однаковим договором і датою:");
        System.out.println("payment == samePayment: " + (payment == samePayment));
        System.out.println("payment.equals(samePayment): " + payment.equals(samePayment));
        System.out.println("payment.hashCode() == samePayment.hashCode(): "
                + (payment.hashCode() == samePayment.hashCode()));
        System.out.println("Порівняння з платежем за іншу дату:");
        System.out.println("payment.equals(differentPayment): " + payment.equals(differentPayment) + "\n");

        System.out.println("6. Виклик обчислюваних методів");
        long months = RentalUtils.contractMonths(contract);
        double total = RentalUtils.totalRent(contract);
        System.out.println("Тривалість договору " + contract.getContractNumber() + ": " + months + " міс.");
        System.out.println("Загальна вартість оренди за договором: " + RentalUtils.formatMoney(total) + "\n");

        System.out.println("7. Вивід усіх сутностей через toString()");
        System.out.println(landlord);
        System.out.println(property);
        System.out.println(contract);
        System.out.println(payment);

        //8. Демонстрація обмеження доступу
        // ua.rental.util.ValidationHelper.requirePositive(100.0, "Amount");
        // Why it does not compile: ValidationHelper has package-private visibility in package ua.rental.util,
        // so it cannot be accessed from class Main located in package ua.rental.

        // Property directProperty = new Property("CAD-999", "Address", landlord, "GARAGE", 2000.0);
        // Why it does not compile: Property constructor is declared private to enforce creation via Property.of(...).
    }
}