package com.orangehrm.components;

import com.orangehrm.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * The main left-hand navigation. Kept separate so any page that displays the
 * application shell can reuse the same navigation logic.
 */
public class SidebarComponent {

    private static final By MENU_ITEMS = By.cssSelector(".oxd-main-menu-item");
    private static final By PIM_MENU = By.cssSelector("a[href*='pim/viewPimModule']");
    private static final By ADMIN_MENU = By.cssSelector("a[href*='admin/viewAdminModule']");
    private static final By DASHBOARD_MENU = By.cssSelector("a[href*='dashboard/index']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public SidebarComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));
    }

    public List<String> getMenuItems() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(MENU_ITEMS));
        return driver.findElements(MENU_ITEMS).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .toList();
    }

    public void openPim() {
        wait.until(ExpectedConditions.elementToBeClickable(PIM_MENU)).click();
    }

    public void openAdmin() {
        wait.until(ExpectedConditions.elementToBeClickable(ADMIN_MENU)).click();
    }

    public void openDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(DASHBOARD_MENU)).click();
    }
}
