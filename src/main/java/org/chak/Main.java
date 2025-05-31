package org.chak;

import java.nio.file.Path;
import java.nio.file.Paths;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
//            final Path srcDir = null; TODO
            final Path propertiesFile = Paths.get("mysite/properties.yml");
            final SiteProperties siteProperties = PropertiesLoader.loadProperties(propertiesFile);
            final Path outputDir = Paths.get("target/generated-site");
            StaticAssetBuilder.copyStaticAssets(outputDir, "mysite/assets");
            new PageBuilder().buildSite(outputDir);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}