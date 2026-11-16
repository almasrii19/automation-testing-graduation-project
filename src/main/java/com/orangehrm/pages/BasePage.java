package com.orangehrm.pages;

import com.orangehrm.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Shared behaviour for all Page Objects: explicit waits, safe interactions,
 * scrolling and common OrangeHRM loading indicators.
 */
public abstract class BasePage {

    protected static final By LOADING_SPINNER = By.cssSelector(".oxd-loading-spinner");
    protected static final By FORM_LOADER = By.cssSelector(".oxd-form-loader");
    protected static final By TOAST_MESSAGE = By.cssSelector(".oxd-toast-content-text");

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log = LogManager.getLogger(getClass());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected void click(By locator) {
        log.info("Clicking element: {}", locator);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                element.click();
                return;
            } catch (org.openqa.selenium.ElementClickInterceptedException exception) {
                log.warn("Click intercepted by a loading overlay (attempt {}/3); waiting for it to clear", attempt);
                waitForSpinnerToDisappear();
                waitForFormLoaderToDisappear();
                element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            }
        }
        element.click();
    }

    protected void type(By locator, String text) {
        log.info("Typing '{}' into: {}", text, locator);
        waitForVisible(locator);
        enterText(locator, text);
    }

    /**
     * Types into a sensitive field without writing the value to the execution log.
     */
    protected void typeSecret(By locator, String text) {
        log.info("Typing [REDACTED] into: {}", locator);
        waitForVisible(locator);
        enterText(locator, text);
    }

    private void enterText(By locator, String text) {
        WebElement input = driver.findElement(locator);
        input.clear();
        if (text != null && !text.isEmpty()) {
            input.sendKeys(text);
        }
    }

    protected String textOf(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected List<WebElement> findAll(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    protected List<String> textsOf(By locator) {
        return findAll(locator).stream()
                .map(element -> element.getText().trim())
                .filter(text -> !text.isEmpty())
                .toList();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected String waitForInputValue(By locator) {
        String value = wait.until(driver -> {
            try {
                String current = driver.findElement(locator).getAttribute("value");
                return (current != null && !current.isBlank()) ? current : null;
            } catch (org.openqa.selenium.StaleElementReferenceException exception) {
                return null;
            }
        });
        log.info("Read input value '{}' from: {}", value, locator);
        return value;
    }

    protected void waitForUrlContains(String fragment) {
        log.info("Waiting for URL to contain: {}", fragment);
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected void waitForText(By locator, String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }

    protected void waitForTextToDisappear(By locator, String text) {
        wait.until(ExpectedConditions.invisibilityOfElementWithText(locator, text));
    }

    protected void waitForSpinnerToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_SPINNER));
        } catch (TimeoutException exception) {
            log.warn("OrangeHRM loading spinner was still visible after the configured wait");
        }
    }

    protected void waitForFormLoaderToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(FORM_LOADER));
        } catch (TimeoutException exception) {
            log.warn("OrangeHRM form loader was still visible after the configured wait");
        }
    }

    protected void scrollIntoView(By locator) {
        WebElement element = waitForPresence(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    protected void waitForNumberOfWindows(int expectedWindows) {
        wait.until(ExpectedConditions.numberOfWindowsToBe(expectedWindows));
    }

    /**
     * Builds a safe XPath string literal for values that may contain an apostrophe.
     */
    protected String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
