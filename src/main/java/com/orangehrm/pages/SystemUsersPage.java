package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Admin > User Management > Users.
 */
public class SystemUsersPage extends BasePage {

    private static final By USER_MANAGEMENT_MENU =
            By.xpath("//span[contains(@class,'oxd-topbar-body-nav-tab-item')][normalize-space()='User Management']");
    private static final By USERS_SUBMENU = By.xpath("//ul[contains(@class,'oxd-dropdown-menu')]//a[normalize-space()='Users']");
    private static final By ADD_BUTTON = By.xpath("//button[normalize-space()='Add']");
    private static final By SYSTEM_USERS_TITLE = By.cssSelector(".oxd-table-filter-title");

    public SystemUsersPage(WebDriver driver) {
        super(driver);
    }

    public SystemUsersPage waitUntilLoaded() {
        waitForUrlContains("/admin/viewSystemUsers");
        waitForText(SYSTEM_USERS_TITLE, "System Users");
        waitForVisible(ADD_BUTTON);
        return this;
    }

    /**
     * Follows the assignment path explicitly: Admin > User Management > Users.
     */
    public SystemUsersPage navigateToUsersViaMenu() {
        log.info("Opening User Management > Users");
        click(USER_MANAGEMENT_MENU);
        click(USERS_SUBMENU);
        waitForSpinnerToDisappear();
        return waitUntilLoaded();
    }

    public AddUserPage clickAdd() {
        click(ADD_BUTTON);
        return new AddUserPage(driver).waitUntilLoaded();
    }
}
