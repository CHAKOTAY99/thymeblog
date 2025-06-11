package org.chak;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetadataBuilder {

    final MarkdownProcessor markdownProcessor;

    public MetadataBuilder(final MarkdownProcessor markdownProcessor) {
        this.markdownProcessor = markdownProcessor;
    }

    public List<MarkdownPage> compile(final Path sourcePath,
                                      final SiteProperties siteProperties) {

        final List<MarkdownPage> pages = new ArrayList<>();

        final Set<Path> dirsToSkip = Arrays.stream(new String[]{siteProperties.getAssets(), siteProperties.getTemplates()})
                .map(Paths::get)  // convert to full path
                .collect(Collectors.toSet());

        try (final Stream<Path> files = Files.walk(sourcePath)) {
            files
                    .filter(path -> dirsToSkip.stream().noneMatch(path::startsWith))
                    .filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {

                        final MarkdownPage page = markdownProcessor.getMarkdownPageFromFile(file, sourcePath, siteProperties);

                        // Do not include draft pages
                        if (!page.metadata().draft()) {
                            pages.add(page);
                        }
                    });
        } catch (final IOException e) {
            throw new RuntimeException("Failed to process content from properties file location: " + sourcePath + " " + e);
        }
        return pages;
    }
}
