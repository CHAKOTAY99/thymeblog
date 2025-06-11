package org.chak;

import org.thymeleaf.TemplateEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            final Path srcDir = Paths.get("mysite");

            // Retrieve Properties
            final SiteProperties siteProperties = PropertiesLoader.loadProperties(srcDir.resolve("properties.yaml"));

            // Create output directory
            // FOR DEV ENV ADD "target/"
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            final String timestamp = LocalDateTime.now().format(formatter);

            final Path outputDir = Paths.get("target/", timestamp);
            Files.createDirectories(outputDir);

            StaticAssetBuilder.copyStaticAssets(outputDir, siteProperties.getAssets());

            final MarkdownProcessor markdownProcessor = new MarkdownProcessor();
            final TemplateEngine templateEngine = TemplateEngineFactory.create(siteProperties.getTemplates());

            final List<MarkdownPage> markdownPageList = new MetadataBuilder(markdownProcessor).compile(srcDir, siteProperties);
            final List<NavbarEntry> navbarEntries = NavbarUtil.createNavBar(markdownPageList);

            new PageBuilder(templateEngine).buildPagesFromMetadata(outputDir, markdownPageList, navbarEntries);

            new IndexPageBuilder(templateEngine).compileIndexes(outputDir, markdownPageList, navbarEntries);

        } catch (final Exception e) {
            throw new RuntimeException("A failure has occurred, please re-run the program", e);
        }
    }
}