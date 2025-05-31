package org.chak;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PropertiesLoader {

    private PropertiesLoader() {};

    public static Properties loadProperties(final Path propertiesFilePath) throws IOException {
        try(final InputStream in = Files.newInputStream(propertiesFilePath)) {
            final LoaderOptions options = new LoaderOptions();
            final Constructor constructor = new Constructor(Properties.class, options);
            final Yaml properties = new Yaml(constructor);
            return properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config from: " + propertiesFilePath, e);
        }
    }
}
