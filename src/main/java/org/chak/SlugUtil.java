package org.chak;

import java.nio.file.Path;
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

    // TODO most likely should not be a utility class
    // TODO Must be fixed for index due to source folder
    public static String createCanonicalUrl(final String slug,
                                            final String siteUrl,
                                            final Path sourcePath) {
        return String.format("%s%s", siteUrl, sourcePath.resolveSibling(slug).normalize());
    }
}
