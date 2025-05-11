package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        makeIndexList(outputDir,"content/blog", "blog-index", "blog-index");
        makeIndexList(outputDir,"content/projects", "blog-index", "project-index");
    }

    /**
     * Handles building the pages
     * @param srcOutputDir - where to output the files
     * @param sourcePath - where to retrieve the .md files from
     * @param template - which template to use
     * @throws IOException
     */
    private void buildPages(final Path srcOutputDir,
                            final String sourcePath,
                            final String template) throws IOException {
        final Path contentPath = Paths.get(sourcePath);

        try(final Stream<Path> files = Files.list(contentPath)) {
            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
                try {
                    final String markdownContent = Files.readString(file); //outputDir.resolve(file).normalize()
                    final String htmlContent = markdownProcessor.convertToHtml(markdownContent);

                    final Context context = new Context();
                    context.setVariable("content", htmlContent);
                    final String html = templateEngine.process(template, context);

                    final Path blogOutputDir = srcOutputDir.resolve(file).normalize();
                    // same name as file for now
                    final Path finalOutputFile;
                    if(template.equals("index")) {
                        finalOutputFile = srcOutputDir.resolve(blogOutputDir.getFileName().toString().replace(".md", ".html"));
                        Files.createDirectories(finalOutputFile.getParent());
                    } else {
                        finalOutputFile = blogOutputDir.resolveSibling(blogOutputDir.getFileName().toString().replace(".md", ".html"));
                        Files.createDirectories(finalOutputFile.getParent());
                    }

                    Files.writeString(finalOutputFile, html);
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to process "+template+" " +e);
                }
            });
        }
    }

    private void makeIndexList(final Path srcOutputDir, final String blogDir, final String template, final String indexName) throws IOException {

        final List<Map<String, String>> links = new ArrayList<>();
        final Path contentPath = Paths.get(blogDir);
        try(final Stream<Path> files = Files.list(contentPath)) {
            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
                final String outputPath = file.toString().replace(".md", ".html");
                links.add(Map.of("href", outputPath, "title", file.getFileName().toString()));
            });
        }

        final Context context = new Context();
        context.setVariable("links", links);
        final String html = templateEngine.process(template, context);

        final Path outputDir = srcOutputDir.normalize();

        Files.writeString(Paths.get(outputDir + "/"+indexName+".html"), html);
    }
}
