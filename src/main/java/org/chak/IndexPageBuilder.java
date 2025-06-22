package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IndexPageBuilder {

    private final TemplateEngine templateEngine;


    public IndexPageBuilder(
            final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Handles building the indexes through the provided metadata
     * Each Index has a list of links to the files located in their sub-directory
     * The files are searched by using the parent directory from the metadata located in {@param markdownPageList}
     *
     * @param destinationPath - used to determine where to write the file
     * @param markdownPageList - the metadata and html content of each page
     * @param navbarEntries - contents of the navigation bar
     * @param siteProperties - properties file
     */
    public void compileIndexes(final Path destinationPath,
                               final List<MarkdownPage> markdownPageList,
                               final List<NavbarEntry> navbarEntries,
                               final SiteProperties siteProperties) {


        final List<MarkdownPage> indexes = markdownPageList.stream()
                .filter(markdownPage -> markdownPage.metadata().index())
                .filter(markdownPage -> !markdownPage.metadata().draft())
                .toList();

        for (final MarkdownPage index : indexes) {

            final List<Map<String, Object>> links = new ArrayList<>();

            final List<MarkdownPage> pagesInIndexDirectory;

            if (index.metadata().outputPath().getParent() == null) {
                // If it is the root - there is no need to add links
                // This check is needed because checking for a parent will result in a NPE
                pagesInIndexDirectory = Collections.emptyList();
            } else {
                pagesInIndexDirectory = markdownPageList.stream()
                        .filter(page -> !indexes.contains(page))
                        .filter(page -> page.metadata().outputPath().getParent().toString().contains(index.metadata().outputPath().getParent().toString()))
                        .toList();
            }
            for (final MarkdownPage pageInIndexDirectory : pagesInIndexDirectory) {
                final Metadata metadata = pageInIndexDirectory.metadata();
                links.add(Map.of("href", metadata.urlPath(),
                        "title", metadata.title(),
                        "description", metadata.description(),
                        "date", metadata.date().toString(),
                        "tags", metadata.tags()));
            }


            final Context context = new Context();
            context.setVariable("content", index.html());
            context.setVariable("links", links);
            context.setVariable("metadata", index.metadata());
            context.setVariable("navbarEntries", navbarEntries);
            context.setVariable("copyright", siteProperties.getCopyright());
            context.setVariable("siteTitle", siteProperties.getTitle());

            final String html = templateEngine.process(index.metadata().template(), context);


            try {
                final Path finalPath = Paths.get(destinationPath.resolve(index.metadata().sourcePath()).toString().replace(".md", ".html"));
                Files.createDirectories(finalPath.getParent());
                Files.writeString(finalPath, html);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to write index file " + index.metadata().title(), e);
            }
        }
    }
}
