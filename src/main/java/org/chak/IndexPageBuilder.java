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
     * and creates a list of links to the .md files relative to the parent path. They are all then placed in a .html page
     */
    public void makeIndexes(final Path srcOutputDir, final List<MarkdownPage> markdownPageList) {


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
                        .filter(page -> page.metadata().outputPath().toString().contains(index.metadata().outputPath().getParent().toString()))
                        .toList();
            }
            for (final MarkdownPage pageInIndexDirectory : pagesInIndexDirectory) {
                final Metadata metadata = pageInIndexDirectory.metadata();
                final String sourcePath = '/' + metadata.sourcePath().getParent().resolve(metadata.slug()).toString(); // TODO needs fixing - appending / for now
                links.add(Map.of("href", sourcePath,
                        "title", metadata.title(),
                        "description", metadata.description(),
                        "date", metadata.date().toString(),
                        "tags", metadata.tags()));
            }


            final Context context = new Context();
            context.setVariable("content", index.html());
            context.setVariable("links", links);
            context.setVariable("metadata", index.metadata());

            final String html = templateEngine.process(index.metadata().template(), context);


            try {
//                    Path.of(blogOutputDir.toString().replace(".md", ".html"));
                // TODO Create directories check
                final Path finalPath = Paths.get(srcOutputDir.resolve(index.metadata().sourcePath()).toString().replace(".md", ".html"));
                Files.createDirectories(finalPath.getParent());
                Files.writeString(finalPath
                        , html);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to write index file " + index.metadata().title(), e);
            }
        }
    }
}
