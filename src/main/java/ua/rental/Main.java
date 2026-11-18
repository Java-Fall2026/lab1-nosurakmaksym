package ua.rental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.rental.io.LandlordLoader;
import ua.rental.model.Landlord;
import ua.rental.model.Property;
import ua.rental.model.PropertyType;

import java.nio.file.Path;
import java.util.List;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        LandlordLoader loader = new LandlordLoader();

        System.out.println("=== 1. Читання CSV-файлу з коректними та некоректними рядками ===");
        Path validPath = Path.of("data", "landlords.csv");
        List<Landlord> landlords = loader.loadFromFile(validPath);
        System.out.println("Успішно завантажені орендодавці (" + landlords.size() + "):");
        landlords.forEach(l -> System.out.println(" - " + l));
        System.out.println();

        System.out.println("=== 2. Спроба прочитати відсутній файл ===");
        Path missingPath = Path.of("data", "non_existent_file.csv");
        List<Landlord> emptyList = loader.loadFromFile(missingPath);
        System.out.println("Програма продовжила роботу. Завантажено записів: " + emptyList.size() + "\n");

        System.out.println("=== 3. Некоректне значення в сеттері змінюваного класу ===");
        if (!landlords.isEmpty()) {
            Property property = Property.of(
                    "CAD-73-001",
                    "м. Чернівці, вул. Головна, 10",
                    landlords.get(0),
                    PropertyType.APARTMENT,
                    12000.0
            );
            try {
                System.out.println("Спроба встановити від'ємну орендну плату...");
                property.setMonthlyRent(-5000.0);
            } catch (IllegalArgumentException e) {
                log.warn("Validation failed during property update: {}", e.getMessage());
                System.out.println("Помилку перехоплено: " + e.getMessage() + "\n");
            }
        }

        System.out.println("=== 4. Перевірка логування всіх трьох рівнів ===");
        log.info("Demonstration info log: application finished all scenarios");
        log.warn("Demonstration warning log: sample warning message");
        log.error("Demonstration error log: simulated non-fatal error state");
    }
}