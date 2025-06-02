package org.chak;

import org.thymeleaf.TemplateEngine;

import java.nio.file.Path;
import java.nio.file.Paths;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
//            final Path srcDir = null; TODO
            final Path propertiesFile = Paths.get("mysite/properties.yaml"); // here I should just look for a properties.yaml file
            final SiteProperties siteProperties = PropertiesLoader.loadProperties(propertiesFile);
            final Path outputDir = Paths.get("target/generated-site"); // this will be a random generated folder by date
            StaticAssetBuilder.copyStaticAssets(outputDir, siteProperties.getAssets());
            final MarkdownProcessor markdownProcessor = new MarkdownProcessor();
            final TemplateEngine templateEngine = TemplateEngineFactory.create(siteProperties.getTemplates());
            new PageBuilder(markdownProcessor, templateEngine).buildSite(outputDir);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}