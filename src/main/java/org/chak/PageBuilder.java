package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PageBuilder {

    private final MarkdownProcessor markdownProcessor;
    private final TemplateEngine templateEngine;

    public PageBuilder(final MarkdownProcessor markdownProcessor, final TemplateEngine templateEngine) {
        this.markdownProcessor = markdownProcessor;
        this.templateEngine = templateEngine;
    }

//    public void buildSite(final Path outputDir, final String contentUrl) {
//        buildPages(outputDir, contentUrl);
//        makeIndexList(outputDir, "mysite/content/blog", "blog-index", "blog-index");
//        makeIndexList(outputDir, "mysite/content/projects", "project-index", "project-index");
//    }

    /**
     * Handles building the pages by going through the provided directory (subdirectories are ignored)
     * and creates a .html copy of the .md files.
     *
     * @param srcOutputDir - where to output the files
     * @param sourcePath   - where to retrieve the .md files from
     */
    public void buildPages(final Path srcOutputDir,
                           final Path sourcePath,
                           final String... omittedDirectories) {


        final Set<Path> dirsToSkip = Arrays.stream(omittedDirectories)
                .map(Paths::get)  // convert to full path
                .collect(Collectors.toSet());

        try (final Stream<Path> files = Files.walk(sourcePath)) {
            files
                    .filter(path -> dirsToSkip.stream().noneMatch(path::startsWith))
                    .filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {

                try {
                    final String markdownContent = Files.readString(file);
                    final MarkdownPage markdownPage = markdownProcessor.convertToMarkdownPage(markdownContent);

                    final Metadata metadata = markdownPage.metadata();
                    if (!metadata.draft()) {

                        final Context context = new Context();
                        context.setVariable("content", markdownPage.html());
                        context.setVariable("metadata", metadata);

                        final String html = templateEngine.process(metadata.template(), context);

                        final Path blogOutputDir = srcOutputDir.resolve(file);
                        final Path finalOutputFile;
                        // Place index file in the root otherwise follow the pattern
                        if (metadata.index()) {
                            finalOutputFile = Path.of(blogOutputDir.toString().replace(".md", ".html"));
                        } else {
                            finalOutputFile = blogOutputDir.resolveSibling(metadata.slug());
                        }

                        // Relative path for content directory to remove
//                        final Path relativePath = directoryToRemove.relativize(finalOutputFile);
//                        final Path newFile = srcOutputDir.resolve(Paths.get("mysite").resolve(relativePath)); // WIP on path

                        Files.createDirectories(finalOutputFile.getParent());
                        Files.writeString(finalOutputFile, html);
                    }
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to process " + file.getFileName() + " " + e);
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException("Failed to process content from properties file location: " + sourcePath + " " + e);
        }
    }


    /**
     * Handles building the indexes through the provided directory (subdirectories are ignored)
     * and creates a list of links to the .md files relative to the parent path. They are all then placed in a .html page
     * with the provided {@param indexName}
     *
     * @param srcOutputDir - where to output the files
     * @param blogDir      - where to retrieve the .md files from
     * @param template     - which template to use
     * @param indexName    - what to call the index
     */
    public void makeIndexList(final Path srcOutputDir, final String blogDir, final String template, final String indexName) {

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

        final Metadata metadata = new Metadata(null, null, null, null, null, false, SlugUtil.slugify(indexName), null, true, null, null);
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
