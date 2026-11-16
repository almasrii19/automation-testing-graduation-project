package com.orangehrm.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central access point for environment settings stored in {@code config.properties}.
 * JVM system properties can override any value, which makes the suite easy to run
 * locally, in CI, or in headed mode without changing source files.
 */
public final class ConfigReader {

    private static final Logger LOG = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found on classpath: " + CONFIG_FILE);
            }
            PROPERTIES.load(input);
            LOG.info("Loaded configuration from {}", CONFIG_FILE);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError("Unable to load " + CONFIG_FILE + ": " + exception.getMessage());
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        String value = (override != null && !override.isBlank()) ? override : PROPERTIES.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException("Missing required configuration key: " + key);
        }
        return value.trim();
    }

    public static int getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Configuration key '" + key + "' must be an integer, but was: " + value);
        }
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String getBaseUrl() {
        return get("base.url").replaceAll("/+$", "");
    }

    public static String getLoginUrl() {
        String path = get("login.path");
        return getBaseUrl() + (path.startsWith("/") ? path : "/" + path);
    }
}
