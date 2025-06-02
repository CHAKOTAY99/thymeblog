package org.chak;

import org.thymeleaf.TemplateEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

            new PageBuilder(markdownProcessor, templateEngine).buildPages(outputDir, srcDir, siteProperties.getAssets(), siteProperties.getTemplates());
            new PageBuilder(markdownProcessor, templateEngine).makeIndexList(outputDir, "mysite/blog", "blog-index", "blog-index"); // this needs to go
            new PageBuilder(markdownProcessor, templateEngine).makeIndexList(outputDir, "mysite/projects", "project-index", "project-index"); // this needs to go - make a idnex file forit

            final List<MarkdownPage> metadataList = new MetadataBuilder(markdownProcessor).compile(srcDir, siteProperties.getAssets(), siteProperties.getTemplates());
            // first build the navbar
            final List<NavbarEntry> navbarEntries = NavbarUtil.createNavBar(metadataList);
            // build the pages
            // build the indexes

        } catch (final Exception e) {
            throw new RuntimeException("A failure has occurred, please re-run the program", e);
        }
    }
}