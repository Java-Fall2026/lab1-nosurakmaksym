package ua.rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.rental.exception.DuplicateIdentityException;
import ua.rental.model.ContractStatus;
import ua.rental.model.Landlord;
import ua.rental.model.LeaseContract;
import ua.rental.model.Property;
import ua.rental.model.PropertyType;
import ua.rental.repository.GenericRepository;

class GenericRepositoryTest {

    @Test
    @DisplayName("Should add entity and find it by identity")
    void shouldAddAndFindByIdentity() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        Landlord landlord = new Landlord("1234567890", "Іван Петренко", "+380501234567");

        repo.add(landlord);
        Optional<Landlord> found = repo.findByIdentity("1234567890");

        assertTrue(found.isPresent());
        assertEquals(landlord, found.get());
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("Should return empty Optional when searching for absent identity")
    void shouldReturnEmptyOptionalWhenNotFound() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        repo.add(new Landlord("1234567890", "Іван Петренко", "+380501234567"));

        Optional<Landlord> result = repo.findByIdentity("9999999999");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw DuplicateIdentityException with identity and type name on duplicate add")
    void shouldThrowDuplicateIdentityExceptionOnDuplicateAdd() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        Landlord first = new Landlord("1234567890", "Іван Петренко", "+380501234567");
        Landlord duplicate = new Landlord("1234567890", "Інший Власник", "+380671112233");

        repo.add(first);

        DuplicateIdentityException ex = assertThrows(DuplicateIdentityException.class, () -> repo.add(duplicate));
        assertTrue(ex.getMessage().contains("1234567890"));
        assertTrue(ex.getMessage().contains("Landlord"));
    }

    @Test
    @DisplayName("Should return false and keep size unchanged when addIfAbsent receives duplicate")
    void shouldReturnFalseAndNotChangeSizeOnAddIfAbsentDuplicate() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        Landlord first = new Landlord("1234567890", "Іван Петренко", "+380501234567");
        Landlord duplicate = new Landlord("1234567890", "Дублікат", "+380671112233");

        assertTrue(repo.addIfAbsent(first));
        assertFalse(repo.addIfAbsent(duplicate));
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("Should remove existing entity and return false for missing identity")
    void shouldRemoveExistingAndReturnFalseForAbsent() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        repo.add(new Landlord("1234567890", "Іван Петренко", "+380501234567"));

        assertTrue(repo.removeByIdentity("1234567890"));
        assertEquals(0, repo.size());
        assertFalse(repo.removeByIdentity("1234567890"));
    }

    @Test
    @DisplayName("Should protect repository state when caller attempts to modify list returned by findAll")
    void shouldProtectRepositoryFromExternalModificationOfFindAll() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        repo.add(new Landlord("1234567890", "Іван Петренко", "+380501234567"));

        List<Landlord> snapshot = repo.findAll();

        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("Should work seamlessly with three different domain types (record and mutable classes)")
    void shouldWorkWithMultipleDomainTypes() {
        GenericRepository<Landlord> landlordRepo =
                new GenericRepository<>(Landlord.class, Landlord::taxCode);
        GenericRepository<Property> propertyRepo =
                new GenericRepository<>(Property.class, Property::getCadastralNumber);
        GenericRepository<LeaseContract> contractRepo =
                new GenericRepository<>(LeaseContract.class, LeaseContract::getContractNumber);

        Landlord landlord = new Landlord("1234567890", "Іван Петренко", "+380501234567");
        Property property = Property.of("CAD-01", "м. Чернівці", landlord, PropertyType.APARTMENT, 12000.0);
        LeaseContract contract = new LeaseContract(
                "CNT-01", property, "Олег Сидоренко",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 1),
                12000.0, ContractStatus.ACTIVE
        );

        landlordRepo.add(landlord);
        propertyRepo.add(property);
        contractRepo.add(contract);

        // Mutating non-identity field on Property does not break lookup by cadastralNumber
        property.setMonthlyRent(13500.0);

        assertEquals(13500.0, propertyRepo.findByIdentity("CAD-01").orElseThrow().getMonthlyRent(), 0.001);
        assertTrue(landlordRepo.findByIdentity("1234567890").isPresent());
        assertTrue(contractRepo.findByIdentity("CNT-01").isPresent());
    }

    @Test
    @DisplayName("Should support wildcard addAll and findAllMatching methods")
    void shouldSupportWildcardCollectionAndPredicate() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        List<Landlord> batch = List.of(
                new Landlord("1111111111", "Анна Коваль", "+380501111111"),
                new Landlord("2222222222", "Богдан Мельник", "+380502222222")
        );

        repo.addAll(batch);
        List<Landlord> filtered = repo.findAllMatching(l -> l.taxCode().startsWith("222"));

        assertEquals(2, repo.size());
        assertEquals(1, filtered.size());
        assertEquals("Богдан Мельник", filtered.get(0).fullName());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding null item or blank identity")
    void shouldRejectNullItemAndBlankIdentity() {
        GenericRepository<Landlord> repo = new GenericRepository<>(Landlord.class, Landlord::taxCode);
        GenericRepository<String> stringRepo = new GenericRepository<>(String.class, s -> s);

        assertThrows(IllegalArgumentException.class, () -> repo.add(null));
        assertThrows(IllegalArgumentException.class, () -> stringRepo.add("   "));
    }
}