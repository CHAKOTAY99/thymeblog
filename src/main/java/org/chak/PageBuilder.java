package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PageBuilder {

    private final MarkdownProcessor markdownProcessor;
    private final TemplateEngine templateEngine;

    public PageBuilder() {
        markdownProcessor = new MarkdownProcessor();
        final FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setPrefix("templates/");
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setTemplateMode(TemplateMode.HTML);
        fileTemplateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(fileTemplateResolver);
    }

    public void buildSite() throws IOException {
        final Path outputDir = Paths.get("target/generated-site");

        buildPages(outputDir, "content", "index");
        buildPages(outputDir, "content/blog", "blog-post");
        buildPages(outputDir, "content/projects", "blog-post");
        makeIndexList(outputDir, "content/blog", "blog-index", "blog-index");
        makeIndexList(outputDir, "content/projects", "project-index", "project-index");
        copyStaticAssets(outputDir, "assets");
    }

    /**
     * Handles building the pages by going through the provided directory (subdirectories are ignored)
     * and creates a .html copy of the .md files.
     *
     * @param srcOutputDir - where to output the files
     * @param sourcePath   - where to retrieve the .md files from
     * @param template     - which template to use
     */
    private void buildPages(final Path srcOutputDir,
                            final String sourcePath,
                            final String template) throws IOException {
        final Path contentPath = Paths.get(sourcePath);

        try (final Stream<Path> files = Files.list(contentPath)) {
            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
                try {
                    final String markdownContent = Files.readString(file); //outputDir.resolve(file).normalize()
                    final MarkdownPage markdownPage = markdownProcessor.convertToHtml(markdownContent);

                    final Metadata metadata = markdownPage.metadata();
                    if(!metadata.draft()) {

                        final Context context = new Context();
                        context.setVariable("content", markdownPage.html());
                        context.setVariable("metadata", metadata);

                        final String html = templateEngine.process(template, context);

                        final Path blogOutputDir = srcOutputDir.resolve(file).normalize();
                        // same name as file for now
                        final Path finalOutputFile;
                        if (template.equals("index")) {
                            finalOutputFile = srcOutputDir.resolve(blogOutputDir.getFileName().toString().replace(".md", ".html"));
                            Files.createDirectories(finalOutputFile.getParent());
                        } else {
                            finalOutputFile = blogOutputDir.resolveSibling(blogOutputDir.getFileName().toString().replace(".md", ".html"));
                            Files.createDirectories(finalOutputFile.getParent());
                        }

                        Files.writeString(finalOutputFile, html);
                    }
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to process " + template + " " + e);
                }
            });
        }
    }


    /**
     * Handles building the indexes Handles through the provided directory (subdirectories are ignored)
     * and creates a list of links to the .md files relative to the parent path. They are all then placed in a .html page
     * with the provided {@param indexName}
     *
     * @param srcOutputDir - where to output the files
     * @param blogDir      - where to retrieve the .md files from
     * @param template     - which template to use
     * @param indexName    - what to call the index
     */
    private void makeIndexList(final Path srcOutputDir, final String blogDir, final String template, final String indexName) {

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
                    final String outputPath = file.toString().replace(".md", ".html");
                    links.add(Map.of("href", outputPath,
                            "title", metadata.title().replaceAll("^\"|\"$", ""),
                            "description", metadata.description().replaceAll("^\"|\"$", ""),
                            "date", metadata.date().toString(),
                            "tags", metadata.tags()));
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException("Failed to make index of " + template, e);
        }

        final Context context = new Context();
        context.setVariable("links", links);
        final String html = templateEngine.process(template, context);

        final Path outputDir = srcOutputDir.normalize();

        try {
            Files.writeString(Paths.get(outputDir + "/" + indexName + ".html"), html);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to write index file " + template, e);
        }
    }

    /**
     * Handles the copying of static assets such as css and images
     */
    private void copyStaticAssets(final Path srcOutputDir, final String assetDir) {

        final Path assetPath = Paths.get(assetDir);

        try (Stream<Path> paths = Files.walk(assetPath)) {
            paths.forEach(sourceFile -> {
                try {
                    final Path destination = srcOutputDir.resolve(sourceFile);

                    if (Files.isDirectory(sourceFile)) {
                        Files.createDirectories(destination);
                    } else {
                        Files.createDirectories(destination.getParent());
                        Files.copy(sourceFile, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy over static assets " + paths, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy over static assets ", e);
        }
    }
}
