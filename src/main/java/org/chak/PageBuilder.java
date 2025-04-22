package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
//        Files.createDirectories(outputDir);

        buildBlogPosts(outputDir);
        buildIndexPage(outputDir);
    }

    private void buildBlogPosts(final Path srcOutputDir) throws IOException {
        final Path contentPath = Paths.get("content/blog");

        try(final Stream<Path> files = Files.list(contentPath)) {
            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
                try {
                    final String markdownContent = Files.readString(file); //outputDir.resolve(file).normalize()
                    final String htmlContent = markdownProcessor.convertToHtml(markdownContent);

                    final Context context = new Context();
                    context.setVariable("content", htmlContent);
                    final String html = templateEngine.process("blog-post", context);

                    final Path blogOutputDir = srcOutputDir.resolve(file).normalize();
                    // same name as file for now
                    final Path finalOutputFile = blogOutputDir.resolveSibling(blogOutputDir.getFileName().toString().replace(".md", ".html"));
                    Files.createDirectories(finalOutputFile.getParent());

                    Files.writeString(finalOutputFile, html);
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to process blog posts " +e);
                }
            });
        }
    }

    private void buildIndexPage(final Path srcOutputDir) throws IOException {
        try(final Stream<Path> files = Files.list(Paths.get("content"))) {
            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
                try {
                    final String markdownContent = Files.readString(file);
                    final String htmlContent = markdownProcessor.convertToHtml(markdownContent);

                    final Context context = new Context();
                    context.setVariable("content", htmlContent);
                    final String html = templateEngine.process("index", context);

                    final Path blogOutputDir = srcOutputDir.resolve(file).normalize();
                    // same name as file for now
                    final Path finalOutputFile = blogOutputDir.resolveSibling(blogOutputDir.getFileName().toString().replace(".md", ".html"));
                    Files.createDirectories(finalOutputFile.getParent());

                    Files.writeString(finalOutputFile, html);
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to process index page " +e);
                }
            });
        }
    }

}
