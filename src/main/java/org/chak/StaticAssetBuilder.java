package org.chak;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Handles the copying of static assets such as css and images
 */
public class StaticAssetBuilder {

    private StaticAssetBuilder() {}

    public static void copyStaticAssets(final Path srcOutputDir,
                                         final String assetDir) {
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
