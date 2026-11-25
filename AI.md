# Робота з AI

Використовув штучний інтелект Gemini
Промт до AI був таким: *"Оціни мій клас GenericRepository<T> на базі LinkedHashMap<String, T> з методами add, addIfAbsent, findAll через List.copyOf() та власним винятком DuplicateIdentityException — чи є тут архітектурні недоліки?"*
Я повністю погоджуюся з оцінкою AI щодо вдалого вибору `LinkedHashMap`, оскільки він гарантує пошук за $O(1)$ і водночас зберігає порядок додавання обʼєктів у методі `findAll()`.
Також добре було схвалення `List.copyOf(storage.values())`, адже незмінна копія надійно захищає внутрішній стан репозиторію від зовнішнього виклику `.clear()`.
Проте я не погодився з пропозицією AI додати метод лінійного пошуку `findByFieldLinear` безпосередньо в сам `GenericRepository<T>` та зробити `DuplicateIdentityException` checked-винятком.
За умовою завдання лінійний пошук має бути винесений в окремий клас `SearchBenchmark` без зміни репозиторію, а `DuplicateIdentityException` повинен наслідувати unchecked-клас `IllegalStateException`, тому я залишив своє рішення без змін.