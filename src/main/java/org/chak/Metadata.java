package org.chak;

import java.nio.file.Path;
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
                       String template,
                       boolean index,
                       Path sourcePath,
                       Path outputPath) {

    public static Metadata parseFromYaml(final Map<String, List<String>> extractedMetadata) {
        final String title = getElement(extractedMetadata, "title");
        final String description = getElement(extractedMetadata, "description");
        final String author = getElement(extractedMetadata, "author");
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String date = getElement(extractedMetadata, "date");
        final LocalDate postDate =  date != null ? LocalDate.parse(date, dateTimeFormatter) : null;
        final boolean draft = Boolean.parseBoolean(getElement(extractedMetadata, "draft"));
        final List<String> tags = extractedMetadata.getOrDefault("tags", List.of());
        final String template = getElement(extractedMetadata, "template");
        final boolean index = Boolean.parseBoolean(getElement(extractedMetadata, "index"));

        return new Metadata(title == null ? null : title.replaceAll("^\"|\"$", ""),
                description == null ? null : description.replaceAll("^\"|\"$", ""),
                author == null ? null : author.replaceAll("^\"|\"$", ""),
                postDate,
                new HashSet<>(tags),
                draft,
                SlugUtil.slugify(title),
                template,
                index,
                null,
                null);
    }

    public static Metadata parseFromYamlAndFile(final Map<String, List<String>> extractedMetadata, final Path filePath, final Path sourcePath) {
        final Metadata metadata = parseFromYaml(extractedMetadata);


        return new Metadata(metadata.title(),
                metadata.description(),
                metadata.author(),
                metadata.date(),
                new HashSet<>(metadata.tags()),
                metadata.draft(),
                SlugUtil.slugify(metadata.title()),
                metadata.template(),
                metadata.index(),
                filePath,
                sourcePath.relativize(filePath));
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
