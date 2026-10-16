package ua.rental.util;

import java.util.Locale;

class FormatHelper {

    private FormatHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static String normalize(String input, boolean toUpperCase) {
        String trimmed = input.trim();
        return toUpperCase ? trimmed.toUpperCase(Locale.ROOT) : trimmed.toLowerCase(Locale.ROOT);
    }

    static String normalizeText(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    static String capitalize(String input) {
        String cleaned = normalizeText(input);
        if (cleaned.isEmpty()) {
            return cleaned;
        }
        String[] words = cleaned.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            String word = words[i];
            result.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                result.append(word.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return result.toString();
    }

    static String formatMoney(double amount) {
        return String.format(Locale.US, "%.2f UAH", amount);
    }
}