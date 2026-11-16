package com.orangehrm.tests;

import com.orangehrm.components.FooterComponent;
import com.orangehrm.listeners.RetryAnalyzer;
import com.orangehrm.pages.AddUserPage;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.SystemUsersPage;
import com.orangehrm.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Epic("OrangeHRM Automation")
@Feature("Admin and Application Navigation")
public class AdminNavigationTests extends BaseTest {

    @Test(
            description = "TC09 - Verify Admin > Add User page",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Admin user management")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the Add User form exposes User Role, Employee Name, Username and Password.")
    public void verifyAddUserPage() {
        DashboardPage dashboard = loginAsAdmin();
        SystemUsersPage systemUsers = Allure.step("Navigate to Admin > User Management",
                dashboard::openAdminModule);
        SystemUsersPage users = Allure.step("Open User Management > Users",
                systemUsers::navigateToUsersViaMenu);
        AddUserPage addUserPage = Allure.step("Click Add", users::clickAdd);

        Allure.step("Verify the Add User page and required fields", () -> {
            Assert.assertTrue(addUserPage.isAddUserFormDisplayed(),
                    "The Add User form should be displayed");
            Assert.assertTrue(driver.getCurrentUrl().contains("/admin/saveSystemUser"),
                    "Expected URL to contain /admin/saveSystemUser but was: " + driver.getCurrentUrl());

            List<String> labels = addUserPage.getFieldLabels();
            for (String expectedField : List.of("User Role", "Employee Name", "Username", "Password")) {
                Assert.assertTrue(labels.contains(expectedField),
                        "Add User form should contain the '" + expectedField + "' field. Labels: " + labels);
                Assert.assertTrue(addUserPage.isFieldDisplayed(expectedField),
                        "Field label '" + expectedField + "' should be visible");
            }

            Assert.assertTrue(addUserPage.isUserRoleFieldDisplayed(), "User Role input should be displayed");
            Assert.assertTrue(addUserPage.isEmployeeNameFieldDisplayed(), "Employee Name input should be displayed");
            Assert.assertTrue(addUserPage.isUsernameFieldDisplayed(), "Username input should be displayed");
            Assert.assertTrue(addUserPage.isPasswordFieldDisplayed(), "Password input should be displayed");
        });

        ScreenshotUtils.captureAndAttach("Admin Add User form");
    }

    @Test(
            description = "TC10 - Verify footer branding link",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Footer branding")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the footer contains 'OrangeHRM, Inc' and its link opens orangehrm.com in a new tab.")
    public void verifyFooterBrandingLink() {
        DashboardPage dashboard = loginAsAdmin();
        FooterComponent footer = dashboard.footer();

        Allure.step("Scroll to the footer and verify branding text", () -> {
            footer.scrollToFooter();
            String footerText = footer.getFooterText();
            Assert.assertTrue(footerText.contains("OrangeHRM, Inc"),
                    "Footer should contain 'OrangeHRM, Inc' but was: " + footerText);
            Assert.assertTrue(footer.isBrandingLinkDisplayed(), "The OrangeHRM, Inc footer link should be displayed");
            ScreenshotUtils.captureAndAttach("OrangeHRM footer branding");
        });

        Allure.step("Click the footer link and verify the new tab URL", () -> {
            footer.clickBrandingLink();
            String newTabUrl = footer.switchToBrandingTabAndGetUrl();
            Assert.assertTrue(newTabUrl.contains("orangehrm.com"),
                    "The branding link should open an orangehrm.com page but was: " + newTabUrl);
            ScreenshotUtils.captureAndAttach("OrangeHRM website in a new tab");
            footer.closeBrandingTabAndReturn();
        });
    }

    @Test(
            description = "TC11 - Verify the sidebar menu UI",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Sidebar navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the authenticated sidebar contains all required OrangeHRM modules.")
    public void verifySidebarMenu() {
        DashboardPage dashboard = loginAsAdmin();

        Allure.step("Verify the required sidebar menu items", () -> {
            List<String> requiredItems = List.of(
                    "Admin", "PIM", "Leave", "Time", "Recruitment",
                    "My Info", "Performance", "Dashboard", "Directory"
            );
            List<String> actualItems = dashboard.getSidebarMenuItems();

            Assert.assertTrue(actualItems.containsAll(requiredItems),
                    "Sidebar should contain " + requiredItems + " but was: " + actualItems);

            List<String> requiredItemsInOrder = actualItems.stream()
                    .filter(requiredItems::contains)
                    .toList();
            Assert.assertEquals(requiredItemsInOrder, requiredItems,
                    "Required sidebar items should appear in the expected order");
        });

        ScreenshotUtils.captureAndAttach("OrangeHRM sidebar menu");
    }
}
