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
                       boolean draft,
                       String slug,
                       String template) {

    protected static Metadata parse(final Map<String, List<String>> extractedMetadata) {
        final String title = getElement(extractedMetadata, "title");
        final String description = getElement(extractedMetadata, "description");
        final String author = getElement(extractedMetadata, "author");
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String date = getElement(extractedMetadata, "date");
        final LocalDate postDate =  date != null ? LocalDate.parse(date, dateTimeFormatter) : null;
        final boolean draft = Boolean.parseBoolean(getElement(extractedMetadata, "draft"));
        final List<String> tags = extractedMetadata.getOrDefault("tags", List.of());
        final String template = getElement(extractedMetadata, "template");

        return new Metadata(title == null ? null : title.replaceAll("^\"|\"$", ""),
                description == null ? null : description.replaceAll("^\"|\"$", ""),
                author == null ? null : author.replaceAll("^\"|\"$", ""),
                postDate, new HashSet<>(tags), draft, SlugUtil.slugify(title), template);
    }

    /**
     * Method used to extract a particular element from the map of extracted Metadata provided by the flexmark library
     * @param extractedMetadata metadata provided by flexmark library
     * @param key element to be retrieved
     * @return extracted value
     */
    private static String getElement(final Map<String, List<String>> extractedMetadata, final String key) {
        final List<String> values = extractedMetadata.get(key);

        if(values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }
}
