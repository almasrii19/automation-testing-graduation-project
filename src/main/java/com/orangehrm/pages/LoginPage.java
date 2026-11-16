package com.orangehrm.pages;

import com.orangehrm.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * OrangeHRM login screen.
 */
public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.name("username");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By ERROR_ALERT = By.cssSelector(".oxd-alert-content-text");
    private static final By FIELD_ERRORS = By.cssSelector(".oxd-input-field-error-message");
    private static final By LOGIN_LOGO = By.cssSelector(".orangehrm-login-branding img");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        log.info("Opening OrangeHRM login page: {}", ConfigReader.getLoginUrl());
        driver.get(ConfigReader.getLoginUrl());
        waitForUrlContains("/auth/login");

        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                waitForVisible(USERNAME_INPUT);
                return this;
            } catch (TimeoutException exception) {
                if (attempt == 2) {
                    throw new IllegalStateException("OrangeHRM login form did not render after two page loads", exception);
                }
                log.warn("Login form was not rendered (transient SPA load); refreshing the page once");
                driver.navigate().refresh();
                waitForUrlContains("/auth/login");
            }
        }
        return this;
    }

    public void enterUsername(String username) {
        type(USERNAME_INPUT, username);
    }

    public void enterPassword(String password) {
        typeSecret(PASSWORD_INPUT, password);
    }

    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    public DashboardPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new DashboardPage(driver);
    }

    public String getErrorMessage() {
        return textOf(ERROR_ALERT);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(ERROR_ALERT);
    }

    public List<String> getFieldErrors() {
        waitForVisible(FIELD_ERRORS);
        return textsOf(FIELD_ERRORS);
    }

    public int getFieldErrorCount() {
        return getFieldErrors().size();
    }

    public boolean isRequiredErrorDisplayedFor(String fieldName) {
        By requiredError = By.xpath(
                "//input[@name=" + xpathLiteral(fieldName) + "]"
                        + "/ancestor::div[contains(@class,'oxd-input-group')][1]"
                        + "//span[contains(@class,'oxd-input-field-error-message')]"
        );
        return isDisplayed(requiredError) && "Required".equals(textOf(requiredError));
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(LOGIN_LOGO) && isDisplayed(USERNAME_INPUT) && isDisplayed(PASSWORD_INPUT);
    }
}
