package com.refactoringMatcher.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Map;

/**
 * @author Diptopol
 * @since 12/16/2020 10:42 PM
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static String readFile(Path p) {
        try {
            return new String(Files.readAllBytes(p));

        } catch (IOException ex) {
            logger.error("Couldn't read file", ex);
            return null;
        }
    }

    public static void deleteDirectory(Path p) {
        try {
            Files.walk(p)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception ex) {
            logger.error("Couldn't delete directory", ex);
        }
    }

    public static void materializeAtBase(Path basePath, Map<Path, String> fileContent) {
        createFolderIfAbsent(basePath);
        fileContent.forEach((k, v) -> {
            materializeFile(basePath.resolve(k), v);
        });
    }

    private static Path createFolderIfAbsent(Path p) {
        if (!p.toFile().exists()) {
            try {
                return Files.createDirectories(p);
            } catch (IOException ex) {
                logger.error("Couldn't create directory", ex);
            }
        }

        return p;
    }

    private static Path materializeFile(Path p, String content) {
        createFolderIfAbsent(p.getParent().toAbsolutePath());

        try {
            return Files.write(p.toAbsolutePath(), content.getBytes(), StandardOpenOption.CREATE);

        } catch (IOException ex) {
            logger.error("Couldn't write the content in File", ex);
            return p;
        }
    }

}
