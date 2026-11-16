package com.orangehrm.driver;

import com.orangehrm.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Owns one browser instance per execution thread. ThreadLocal storage is required
 * because TestNG runs the configured test classes in parallel.
 */
public final class DriverManager {

    private static final Logger LOG = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() {
        if (DRIVER.get() != null) {
            quitDriver();
        }

        String browser = ConfigReader.get("browser");
        if (!"chrome".equalsIgnoreCase(browser)) {
            throw new IllegalArgumentException("Unsupported browser '" + browser + "'. This suite is configured for Chrome.");
        }

        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addArguments(
                "--window-size=1920,1080",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--lang=en-US",
                "--remote-allow-origins=*"
        );

        if (ConfigReader.getBoolean("headless")) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        DRIVER.set(driver);
        LOG.info("Chrome started on thread '{}' (headless={})",
                Thread.currentThread().getName(), ConfigReader.getBoolean("headless"));
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized for thread: "
                    + Thread.currentThread().getName());
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
                LOG.info("Chrome closed on thread '{}'", Thread.currentThread().getName());
            } catch (RuntimeException exception) {
                LOG.warn("Browser session could not be closed cleanly: {}", exception.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }
}
