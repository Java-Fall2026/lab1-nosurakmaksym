package ua.rental.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.rental.exception.DuplicateIdentityException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class GenericRepository<T> {

    private static final Logger log = LoggerFactory.getLogger(GenericRepository.class);

    private final String entityTypeName;
    private final IdentityExtractor<T> extractor;
    private final Map<String, T> storage = new LinkedHashMap<>();

    public GenericRepository(Class<T> type, IdentityExtractor<T> extractor) {
        if (type == null) {
            throw new IllegalArgumentException("Entity type class must not be null");
        }
        if (extractor == null) {
            throw new IllegalArgumentException("IdentityExtractor must not be null");
        }
        this.entityTypeName = type.getSimpleName();
        this.extractor = extractor;
    }

    public void add(T item) {
        String identity = extractAndValidateIdentity(item);

        if (storage.containsKey(identity)) {
            String message = "Duplicate identity '" + identity + "' in " + entityTypeName + " repository";
            log.error(message);
            throw new DuplicateIdentityException(message);
        }

        storage.put(identity, item);
        log.info("Added {} with identity '{}'", entityTypeName, identity);
    }

    public boolean addIfAbsent(T item) {
        String identity = extractAndValidateIdentity(item);

        if (storage.containsKey(identity)) {
            log.warn("Rejected duplicate {} with identity '{}' in addIfAbsent", entityTypeName, identity);
            return false;
        }

        storage.put(identity, item);
        log.info("Added {} with identity '{}' via addIfAbsent", entityTypeName, identity);
        return true;
    }

    public void addAll(Collection<? extends T> items) {
        if (items == null) {
            log.error("Attempted to pass null collection to addAll in {} repository", entityTypeName);
            throw new IllegalArgumentException("Items collection must not be null");
        }
        for (T item : items) {
            add(item);
        }
    }

    public boolean removeByIdentity(String identity) {
        if (identity == null || identity.isBlank()) {
            log.warn("Cannot remove {} with null or blank identity", entityTypeName);
            return false;
        }
        T removed = storage.remove(identity);
        if (removed == null) {
            log.warn("{} with identity '{}' was not found for removal", entityTypeName, identity);
            return false;
        }
        log.info("Removed {} with identity '{}'", entityTypeName, identity);
        return true;
    }

    public Optional<T> findByIdentity(String identity) {
        if (identity == null || identity.isBlank()) {
            log.warn("Requested lookup for {} with null or blank identity", entityTypeName);
            return Optional.empty();
        }
        T found = storage.get(identity);
        if (found == null) {
            log.warn("{} with identity '{}' not found", entityTypeName, identity);
            return Optional.empty();
        }
        log.info("Found {} with identity '{}'", entityTypeName, identity);
        return Optional.of(found);
    }

    public List<T> findAll() {
        return List.copyOf(storage.values());
    }

    public List<T> findAllMatching(Predicate<? super T> predicate) {
        if (predicate == null) {
            log.error("Null predicate passed to findAllMatching in {} repository", entityTypeName);
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return storage.values().stream()
                .filter(predicate)
                .toList();
    }

    public int size() {
        return storage.size();
    }

    private String extractAndValidateIdentity(T item) {
        if (item == null) {
            String message = "Cannot add null item to " + entityTypeName + " repository";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        String identity = extractor.identityOf(item);
        if (identity == null || identity.isBlank()) {
            String message = "Extracted identity for " + entityTypeName + " must not be null or blank";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return identity;
    }
}