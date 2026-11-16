package com.orangehrm.components;

import com.orangehrm.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Footer branding shown on every authenticated page.
 */
public class FooterComponent {

    private static final By FOOTER = By.cssSelector(".oxd-layout-footer");
    private static final By BRANDING_LINK = By.xpath("//a[normalize-space()='OrangeHRM, Inc']");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String originalWindow;

    public FooterComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));
        this.originalWindow = driver.getWindowHandle();
    }

    public FooterComponent scrollToFooter() {
        WebElement footer = wait.until(ExpectedConditions.presenceOfElementLocated(FOOTER));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", footer);
        return this;
    }

    public String getFooterText() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(FOOTER));
        return driver.findElement(FOOTER).getText().trim();
    }

    public boolean isBrandingLinkDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(BRANDING_LINK)).isDisplayed();
    }

    public void clickBrandingLink() {
        wait.until(ExpectedConditions.elementToBeClickable(BRANDING_LINK)).click();
    }

    public String switchToBrandingTabAndGetUrl() {
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        wait.until(ExpectedConditions.urlContains("orangehrm.com"));
        return driver.getCurrentUrl();
    }

    public void closeBrandingTabAndReturn() {
        driver.close();
        driver.switchTo().window(originalWindow);
        wait.until(ExpectedConditions.numberOfWindowsToBe(1));
    }
}
