package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    public void makeIndexList(final Path srcOutputDir, final List<MarkdownPage> markdownPageList) {

        final List<Map<String, Object>> links = new ArrayList<>();

        final List<MarkdownPage> indexes = markdownPageList.stream()
                .filter(markdownPage -> markdownPage.metadata().index())
                .filter(markdownPage -> !markdownPage.metadata().draft())
                .toList();

            for (final MarkdownPage index : indexes) {

                // TODO SETUP LINKS
                // TODO Ensure root index file does nothing

                final Context context = new Context();
                context.setVariable("content", index.html());
                context.setVariable("links", links);
                context.setVariable("metadata", index);

                final String html = templateEngine.process(index.metadata().template(), context);

                final Path outputDir = srcOutputDir.normalize();

                try {
                    Files.writeString(outputDir.resolve(index.metadata().slug()), html);
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to write index file " + index.metadata().template(), e);
                }
            }

//        final Path contentPath = Paths.get(blogDir);
//
//        try (final Stream<Path> files = Files.walk(contentPath)) {
//            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
//                final Metadata metadata;
//                try {
//                    final String markdownContent = Files.readString(file);
//                    metadata = markdownProcessor.getMetadata(markdownContent);
//                } catch (IOException e) {
//                    throw new UncheckedIOException(e);
//                }
//                if (!metadata.draft()) {
//                    // Get the parent of the markdown file ("blog/content") and resolve it with the slug title
//                    final String outputPath = file.getParent().resolve(metadata.slug()).toString();
//                    links.add(Map.of("href", outputPath,
//                            "title", metadata.title(),
//                            "description", metadata.description(),
//                            "date", metadata.date().toString(),
//                            "tags", metadata.tags()));
//                }
//            });
//        } catch (final IOException e) {
//            throw new RuntimeException("Failed to make index of " + template, e);
//        }
//
//        final Metadata metadata = new Metadata(null, null, null, null, null, false, SlugUtil.slugify(indexName), null, false, null, null);
//        final Context context = new Context();
//        context.setVariable("links", links);
//        context.setVariable("metadata", metadata);
//
//        final String html = templateEngine.process(template, context);
//
//        final Path outputDir = srcOutputDir.normalize();
//
//        try {
//            Files.writeString(outputDir.resolve(metadata.slug()), html);
//        } catch (final IOException e) {
//            throw new RuntimeException("Failed to write index file " + template, e);
//        }
    }
}
