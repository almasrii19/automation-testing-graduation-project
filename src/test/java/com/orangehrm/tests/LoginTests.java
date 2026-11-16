package com.orangehrm.tests;

import com.orangehrm.data.TestDataProvider;
import com.orangehrm.listeners.RetryAnalyzer;
import com.orangehrm.models.LoginScenario;
import com.orangehrm.pages.DashboardPage;
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
@Feature("Authentication")
public class LoginTests extends BaseTest {

    @Test(
            description = "TC01 - Login with valid credentials",
            dataProvider = "validLoginData",
            dataProviderClass = TestDataProvider.class,
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Valid login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that valid credentials open the Dashboard and display its header.")
    public void loginWithValidCredentials(LoginScenario scenario) {
        Allure.step("Open the OrangeHRM login page", () -> loginPage.open());
        Allure.step("Enter valid username", () -> loginPage.enterUsername(scenario.username()));
        Allure.step("Enter valid password", () -> loginPage.enterPassword(scenario.password()));
        Allure.step("Click Login", loginPage::clickLogin);

        DashboardPage dashboard = Allure.step("Verify the dashboard is displayed", () -> {
            DashboardPage page = new DashboardPage(driver).waitUntilLoaded();
            Assert.assertTrue(driver.getCurrentUrl().contains(scenario.expectedUrlFragment()),
                    "Expected URL to contain '" + scenario.expectedUrlFragment() + "' but was: " + driver.getCurrentUrl());
            Assert.assertTrue(page.isDashboardDisplayed(), "Dashboard header should be visible after a valid login");
            Assert.assertEquals(page.getHeaderText(), scenario.expectedMessage(),
                    "Dashboard header text should match the expected value");
            return page;
        });

        ScreenshotUtils.captureAndAttach("Dashboard after valid login");
        Assert.assertTrue(dashboard.isDashboardDisplayed(), "Dashboard should remain displayed at the end of the test");
    }

    @Test(
            description = "TC02 - Login with invalid credentials",
            dataProvider = "invalidLoginData",
            dataProviderClass = TestDataProvider.class,
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that invalid credentials are rejected with the 'Invalid credentials' alert.")
    public void loginWithInvalidCredentials(LoginScenario scenario) {
        Allure.step("Open the OrangeHRM login page", () -> loginPage.open());
        Allure.step("Enter an invalid username", () -> loginPage.enterUsername(scenario.username()));
        Allure.step("Enter an invalid password", () -> loginPage.enterPassword(scenario.password()));
        Allure.step("Click Login", loginPage::clickLogin);

        Allure.step("Verify the invalid credentials error", () -> {
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                    "The invalid credentials alert should be displayed");
            Assert.assertEquals(loginPage.getErrorMessage(), scenario.expectedMessage(),
                    "Unexpected login error message");
            Assert.assertTrue(driver.getCurrentUrl().contains(scenario.expectedUrlFragment()),
                    "User should remain on the login page after a failed login");
        });

        ScreenshotUtils.captureAndAttach("Invalid credentials error");
    }

    @Test(
            description = "TC03 - Login with empty fields",
            dataProvider = "emptyLoginData",
            dataProviderClass = TestDataProvider.class,
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Empty login validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that submitting an empty login form shows the Required validation message under both fields.")
    public void loginWithEmptyFields(LoginScenario scenario) {
        Allure.step("Open the OrangeHRM login page", () -> loginPage.open());
        Allure.step("Leave username and password empty", () -> {
            Assert.assertTrue(scenario.username().isEmpty());
            Assert.assertTrue(scenario.password().isEmpty());
        });
        Allure.step("Click Login", loginPage::clickLogin);

        Allure.step("Verify Required messages under both fields", () -> {
            List<String> errors = loginPage.getFieldErrors();
            Assert.assertEquals(errors.size(), 2, "A Required message should appear under both login fields");
            Assert.assertTrue(errors.stream().allMatch("Required"::equals),
                    "Both validation messages should read 'Required' but were: " + errors);
            Assert.assertTrue(loginPage.isRequiredErrorDisplayedFor("username"),
                    "The username field should display a Required message");
            Assert.assertTrue(loginPage.isRequiredErrorDisplayedFor("password"),
                    "The password field should display a Required message");
            Assert.assertTrue(driver.getCurrentUrl().contains(scenario.expectedUrlFragment()),
                    "Validation errors should be shown on the login page");
        });

        ScreenshotUtils.captureAndAttach("Required validation messages");
    }
}
