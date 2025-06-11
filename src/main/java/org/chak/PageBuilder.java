package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PageBuilder {

    private final TemplateEngine templateEngine;

    public PageBuilder(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Handles building the pages by going through the provided metadata
     *
     * @param srcOutputDir - where to output the files
     */
    public void buildPagesFromMetadata(final Path srcOutputDir,
                                       final List<MarkdownPage> markdownPageList,
                                       final List<NavbarEntry> navbarEntries,
                                       final SiteProperties siteProperties) {


        final List<MarkdownPage> pagesToRender = markdownPageList.stream()
                .filter(page -> !page.metadata().index())
                .filter(page -> !page.metadata().draft())
                .toList();

        for (final MarkdownPage markdownPage : pagesToRender) {

            final Context context = new Context();
            context.setVariable("content", markdownPage.html());
            context.setVariable("metadata", markdownPage.metadata());
            context.setVariable("navbarEntries", navbarEntries);
            context.setVariable("copyright", siteProperties.getCopyright());
            context.setVariable("siteTitle", siteProperties.getTitle());

            final String html = templateEngine.process(markdownPage.metadata().template(), context);

            try {
                final Path pageOutputDir = srcOutputDir.resolve(markdownPage.metadata().sourcePath());
                final Path finalOutputDir = pageOutputDir.resolveSibling(markdownPage.metadata().slug());
                Files.createDirectories(finalOutputDir.getParent());
                Files.writeString(finalOutputDir, html);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to write page " + markdownPage.metadata().title(), e);
            }
        }
    }
}
