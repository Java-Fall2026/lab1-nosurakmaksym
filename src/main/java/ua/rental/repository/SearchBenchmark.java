package ua.rental.repository;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ua.rental.model.Landlord;

public class SearchBenchmark {

    private static final int ENTITY_COUNT = 100_000;
    private static final int WARMUP_ITERATIONS = 5_000;
    private static final int MEASURED_TRIALS = 15;
    private static final int OPS_PER_TRIAL = 1_000;

    public static void main(String[] args) {
        
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        long mapFirstNs;
        long listFirstNs;
        long mapLastNs;
        long listLastNs;
        long mapMissingNs;
        long listMissingNs;

        try {
            IdentityExtractor<Landlord> extractor = Landlord::taxCode;
            GenericRepository<Landlord> repository = new GenericRepository<>(Landlord.class, extractor);

            for (int i = 0; i < ENTITY_COUNT; i++) {
                String taxCode = String.format("TAX%06d", i);
                repository.add(new Landlord(taxCode, "Landlord User", "+380501234567"));
            }

            List<Landlord> snapshotList = repository.findAll();

            String firstId = "TAX000000";
            String lastId = String.format("TAX%06d", ENTITY_COUNT - 1);
            String missingId = "TAX999999";

            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                repository.findByIdentity(firstId);
                repository.findByIdentity(lastId);
                repository.findByIdentity(missingId);
                linearSearch(snapshotList, firstId, extractor);
                linearSearch(snapshotList, lastId, extractor);
                linearSearch(snapshotList, missingId, extractor);
            }

            mapFirstNs = measureMedianMapLookup(repository, firstId);
            listFirstNs = measureMedianLinearLookup(snapshotList, firstId, extractor);

            mapLastNs = measureMedianMapLookup(repository, lastId);
            listLastNs = measureMedianLinearLookup(snapshotList, lastId, extractor);

            mapMissingNs = measureMedianMapLookup(repository, missingId);
            listMissingNs = measureMedianLinearLookup(snapshotList, missingId, extractor);
        } finally {
            System.setOut(originalOut);
        }

        System.out.println("Результати бенчмарку (100 000 елементів, медіана в наносекундах)");
        System.out.println("| Сценарій пошуку | Map (`GenericRepository`), нс | List (`linearSearch`), нс | Прискорення (разів) |");
        System.out.println("| :--- | :---: | :---: | :---: |");
        printRow("Перший елемент (`TAX000000`)", mapFirstNs, listFirstNs);
        printRow("Останній елемент (`TAX099999`)", mapLastNs, listLastNs);
        printRow("Відсутній елемент (`TAX999999`)", mapMissingNs, listMissingNs);
    }

    private static <E> Optional<E> linearSearch(
            List<E> items, String identity, IdentityExtractor<E> extractor) {
        for (E item : items) {
            if (identity.equals(extractor.identityOf(item))) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    private static long measureMedianMapLookup(GenericRepository<Landlord> repo, String targetId) {
        long[] samples = new long[MEASURED_TRIALS];
        for (int t = 0; t < MEASURED_TRIALS; t++) {
            long start = System.nanoTime();
            for (int i = 0; i < OPS_PER_TRIAL; i++) {
                repo.findByIdentity(targetId);
            }
            long elapsed = System.nanoTime() - start;
            samples[t] = elapsed / OPS_PER_TRIAL;
        }
        Arrays.sort(samples);
        return samples[MEASURED_TRIALS / 2];
    }

    private static long measureMedianLinearLookup(
            List<Landlord> list, String targetId, IdentityExtractor<Landlord> extractor) {
        long[] samples = new long[MEASURED_TRIALS];
        for (int t = 0; t < MEASURED_TRIALS; t++) {
            long start = System.nanoTime();
            for (int i = 0; i < OPS_PER_TRIAL; i++) {
                linearSearch(list, targetId, extractor);
            }
            long elapsed = System.nanoTime() - start;
            samples[t] = elapsed / OPS_PER_TRIAL;
        }
        Arrays.sort(samples);
        return samples[MEASURED_TRIALS / 2];
    }

    private static void printRow(String scenario, long mapNs, long listNs) {
        double speedup = mapNs > 0 ? (double) listNs / mapNs : 0.0;
        System.out.printf("| %s | %d | %d | %.1fx |%n", scenario, mapNs, listNs, speedup);
    }
}