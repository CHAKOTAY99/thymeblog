package org.chak;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    public static String createCanonicalUrl(final String slug,
                                            final String siteUrl,
                                            final Path sourcePath,
                                            final String directoryName) {
        return String.format("%s%s", siteUrl, Paths.get(sourcePath.toString().replace(directoryName, "")).resolveSibling(slug)); // Could be improved not to use string manipulation
    }

    // TODO most likely should not be a utility class
    public static String createUrlPath(final String slug,
                                            final String directoryName,
                                            final Path sourcePath) {
        return Paths.get(sourcePath.toString().replace(directoryName, "")).resolveSibling(slug).toString(); // Could be improved not to use string manipulation
    }
}
