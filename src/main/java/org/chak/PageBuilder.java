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

    private final MarkdownProcessor markdownProcessor = new MarkdownProcessor();
    private final TemplateEngine templateEngine;

    public PageBuilder() {
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
        Files.createDirectories(outputDir);

        try(final Stream<Path> files = Files.list(Paths.get("content"))) {
            files.filter(file -> file.getFileName().toString().endsWith(".md")).forEach(file -> {
                try {
                    final String markdownContent = Files.readString(file);
                    final String htmlContent = markdownProcessor.convertToHtml(markdownContent);

                    final Context context = new Context();
                    context.setVariable("content", htmlContent);
                    final String html = templateEngine.process("layout", context);

                    // same name as file
                    final Path output = outputDir.resolve(file.getFileName().toString().replace(".md", ".html"));
                    Files.writeString(output, html);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

}
