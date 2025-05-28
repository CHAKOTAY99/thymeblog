package org.chak;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtil {

    private SlugUtil() {
    }

    public static String slugify(final String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Slugify input is null or empty");
        }

        // Normalize
        final String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Convert to a slug from chatgpt
        return normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")   // Remove non-alphanumeric characters
                .replaceAll("\\s+", "-")           // Replace spaces with hyphens
                .replaceAll("-{2,}", "-")          // Collapse multiple dashes
                .replaceAll("^-|-$", "") // Trim leading/trailing dashes
                + ".html";
    }
}
