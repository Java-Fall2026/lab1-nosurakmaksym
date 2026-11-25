package ua.rental;

import ua.rental.exception.DuplicateIdentityException;
import ua.rental.io.LandlordLoader;
import ua.rental.model.ContractStatus;
import ua.rental.model.Landlord;
import ua.rental.model.LeaseContract;
import ua.rental.model.Property;
import ua.rental.model.PropertyType;
import ua.rental.repository.GenericRepository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        GenericRepository<Landlord> landlordRepo =
                new GenericRepository<>(Landlord.class, Landlord::taxCode);
        GenericRepository<Property> propertyRepo =
                new GenericRepository<>(Property.class, Property::getCadastralNumber);
        GenericRepository<LeaseContract> contractRepo =
                new GenericRepository<>(LeaseContract.class, LeaseContract::getContractNumber);

        System.out.println("1. Наповнення репозиторію Landlord із CSV-файла (включно з дублікатом)");
        LandlordLoader loader = new LandlordLoader();
        List<Landlord> csvLandlords = loader.loadFromFile(Path.of("data", "landlords.csv"));

        for (Landlord item : csvLandlords) {
            try {
                landlordRepo.add(item);
            } catch (DuplicateIdentityException e) {
                System.out.println("Перехоплено дублікат із CSV: " + e.getMessage());
            }
        }
        System.out.println("Усього унікальних орендодавців у репозиторії: " + landlordRepo.size() + "\n");

        System.out.println("2. Робота з іншими типами домену та пошук (знайдено / не знайдено)");
        Landlord owner = landlordRepo.findByIdentity("1234567890").orElseThrow();
        Property flat = Property.of(
                "CAD-73-101", "м. Чернівці, вул. Університетська, 15",
                owner, PropertyType.APARTMENT, 14000.0
        );
        Property office = Property.of(
                "CAD-73-202", "м. Чернівці, вул. Головна, 42",
                owner, PropertyType.COMMERCIAL, 28000.0
        );
        propertyRepo.add(flat);
        propertyRepo.add(office);

        flat.setMonthlyRent(15000.0);

        LeaseContract contract = new LeaseContract(
                "CNT-2026-01", flat, "Тарас Бондаренко",
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 8, 1),
                15000.0, ContractStatus.ACTIVE
        );
        contractRepo.add(contract);

        System.out.println("Пошук існуючого об'єкта CAD-73-101: " + propertyRepo.findByIdentity("CAD-73-101"));
        System.out.println("Пошук відсутнього об'єкта CAD-99-999: " + propertyRepo.findByIdentity("CAD-99-999"));
        System.out.println("Пошук договору CNT-2026-01: " + contractRepo.findByIdentity("CNT-2026-01") + "\n");

        System.out.println("3. Додавання дубліката через add() та addIfAbsent()");
        Landlord duplicateLandlord = new Landlord("0987654321", "Олена Коваленко (копія)", "+380670000000");
        try {
            landlordRepo.add(duplicateLandlord);
        } catch (DuplicateIdentityException e) {
            System.out.println("add() кинув виключення: " + e.getMessage());
        }

        boolean addedViaIfAbsent = landlordRepo.addIfAbsent(duplicateLandlord);
        System.out.println("addIfAbsent() повернув: " + addedViaIfAbsent + " (розмір репозиторію: " + landlordRepo.size() + ")\n");

        System.out.println("4. Видалення наявного та відсутнього елемента");
        boolean removedExisting = propertyRepo.removeByIdentity("CAD-73-202");
        boolean removedMissing = propertyRepo.removeByIdentity("CAD-00-000");
        System.out.println("Видалення CAD-73-202: " + removedExisting);
        System.out.println("Видалення CAD-00-000: " + removedMissing);
        System.out.println("Залишилось об'єктів нерухомості: " + propertyRepo.size());
    }
}