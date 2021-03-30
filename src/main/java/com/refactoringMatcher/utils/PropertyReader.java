package com.refactoringMatcher.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Diptopol
 * @since 11/7/2020 1:19 PM
 */
public class PropertyReader {

    private static Logger logger = LoggerFactory.getLogger(PropertyReader.class);

    private static Properties properties;

    static {
        InputStream inputStream;
        properties = new Properties();
        String fileName = "refactoring-matcher-config.properties";

        try {
            inputStream = PropertyReader.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);

        } catch (IOException ex) {
            logger.error("Couldn't load config properties", ex);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
