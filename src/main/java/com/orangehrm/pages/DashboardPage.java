package com.orangehrm.pages;

import com.orangehrm.components.FooterComponent;
import com.orangehrm.components.SidebarComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * OrangeHRM dashboard, used as the post-login landing page and navigation hub.
 */
public class DashboardPage extends BasePage {

    private static final By HEADER = By.cssSelector(".oxd-topbar-header-breadcrumb-module");
    private static final By PIM_MENU = By.cssSelector("a[href*='pim/viewPimModule']");
    private static final By ADMIN_MENU = By.cssSelector("a[href*='admin/viewAdminModule']");

    private final SidebarComponent sidebar;

    public DashboardPage(WebDriver driver) {
        super(driver);
        this.sidebar = new SidebarComponent(driver);
    }

    public DashboardPage waitUntilLoaded() {
        waitForUrlContains("/dashboard/index");
        waitForText(HEADER, "Dashboard");
        return this;
    }

    public boolean isDashboardDisplayed() {
        return isDisplayed(HEADER) && "Dashboard".equals(textOf(HEADER));
    }

    public String getHeaderText() {
        return textOf(HEADER);
    }

    public List<String> getSidebarMenuItems() {
        return sidebar.getMenuItems();
    }

    public PimEmployeeListPage openPimModule() {
        log.info("Navigating to PIM > Employee List");
        click(PIM_MENU);
        return new PimEmployeeListPage(driver).waitUntilLoaded();
    }

    public SystemUsersPage openAdminModule() {
        log.info("Navigating to Admin > User Management");
        click(ADMIN_MENU);
        return new SystemUsersPage(driver).waitUntilLoaded();
    }

    public FooterComponent footer() {
        return new FooterComponent(driver);
    }
}
