package org.chak;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record Metadata(String title,
                       String description,
                       String author,
                       LocalDate date,
                       Set<String> tags,
                       boolean draft) {

    protected static Metadata parse(final Map<String, List<String>> extractedMetadata) {
        final String title = getElement(extractedMetadata, "title");
        final String description = getElement(extractedMetadata, "description");
        final String author = getElement(extractedMetadata, "author");
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String date = getElement(extractedMetadata, "date");
        final LocalDate postDate =  date != null ? LocalDate.parse(date, dateTimeFormatter) : null;
        final boolean draft = Boolean.parseBoolean(getElement(extractedMetadata, "draft"));
        final List<String> tags = extractedMetadata.getOrDefault("tags", List.of());

        return new Metadata(title, description, author, postDate, new HashSet<>(tags), draft);
    }

    protected static boolean isDraft(final Map<String, List<String>> extractedMetadata) {
        return Boolean.parseBoolean(getElement(extractedMetadata, "draft"));
    }

    private static String getElement(final Map<String, List<String>> extractedMetadata, final String key) {
        final List<String> values = extractedMetadata.get(key);

        if(values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }
}
