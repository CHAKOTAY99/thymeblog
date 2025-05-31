package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class IndexPageBuilder {

    private final MarkdownProcessor markdownProcessor;
    private final TemplateEngine templateEngine;
    private final Path srcOutputDir;
    private final String blogDir;
    private final String template;
    private final String indexName;


    public IndexPageBuilder(final MarkdownProcessor markdownProcessor,
                            final TemplateEngine templateEngine,
                            final Path srcOutputDir,
                            final String sourcePath,
                            final String template,
                            final String indexName) {
        this.markdownProcessor = markdownProcessor;
        this.templateEngine = templateEngine;
        this.srcOutputDir = srcOutputDir;
        this.blogDir = sourcePath;
        this.template = template;
        this.indexName = indexName;
    }

    /**
     * Handles building the indexes Handles through the provided directory (subdirectories are ignored)
     * and creates a list of links to the .md files relative to the parent path. They are all then placed in a .html page
     * with the provided {@param indexName}
     *
     */
    public void makeIndexList() {

        final List<Map<String, Object>> links = new ArrayList<>();
        final Path contentPath = Paths.get(blogDir);

        try (final Stream<Path> files = Files.walk(contentPath)) {
            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
                final Metadata metadata;
                try {
                    final String markdownContent = Files.readString(file);
                    metadata = markdownProcessor.getMetadata(markdownContent);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                if (!metadata.draft()) {
                    // Get the parent of the markdown file ("blog/content") and resolve it with the slug title
                    final String outputPath = file.getParent().resolve(metadata.slug()).toString();
                    links.add(Map.of("href", outputPath,
                            "title", metadata.title(),
                            "description", metadata.description(),
                            "date", metadata.date().toString(),
                            "tags", metadata.tags()));
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException("Failed to make index of " + template, e);
        }

        final Metadata metadata = new Metadata(null, null, null, null, null, false, SlugUtil.slugify(indexName), null, false);
        final Context context = new Context();
        context.setVariable("links", links);
        context.setVariable("metadata", metadata);

        final String html = templateEngine.process(template, context);

        final Path outputDir = srcOutputDir.normalize();

        try {
            Files.writeString(outputDir.resolve(metadata.slug()), html);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to write index file " + template, e);
        }
    }
}
