package com.orangehrm.utils;

import com.orangehrm.config.ConfigReader;
import com.orangehrm.driver.DriverManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves screenshots to disk and attaches them to the Allure report.
 */
public final class ScreenshotUtils {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    public static Path capture(String label) {
        WebDriver driver = DriverManager.getDriver();
        Path screenshotDirectory = Paths.get(ConfigReader.get("screenshot.dir")).toAbsolutePath();

        try {
            Files.createDirectories(screenshotDirectory);
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = sanitize(label) + "_" + LocalDateTime.now().format(TIMESTAMP)
                    + "_" + Thread.currentThread().getId() + ".png";
            Path destination = screenshotDirectory.resolve(fileName);
            Files.write(destination, screenshot);
            LOG.info("Screenshot saved: {}", destination);
            return destination;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save screenshot: " + exception.getMessage(), exception);
        }
    }

    public static void captureAndAttach(String label) {
        Path screenshot = capture(label);
        try (ByteArrayInputStream input = new ByteArrayInputStream(Files.readAllBytes(screenshot))) {
            Allure.addAttachment(label, "image/png", input, "png");
        } catch (IOException exception) {
            LOG.warn("Screenshot could not be attached to Allure: {}", exception.getMessage());
        }
    }

    public static void attachPageSource(String label) {
        try {
            String source = DriverManager.getDriver().getPageSource();
            Allure.addAttachment(label, "text/html",
                    new ByteArrayInputStream(source.getBytes(java.nio.charset.StandardCharsets.UTF_8)), "html");
        } catch (RuntimeException exception) {
            LOG.warn("Page source could not be attached to Allure: {}", exception.getMessage());
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9-_]+", "_").replaceAll("_+", "_");
    }
}
