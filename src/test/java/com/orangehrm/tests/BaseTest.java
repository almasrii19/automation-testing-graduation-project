package com.orangehrm.tests;

import com.orangehrm.data.TestDataProvider;
import com.orangehrm.driver.DriverManager;
import com.orangehrm.models.LoginScenario;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Common test lifecycle. A fresh browser is created per test method and stored
 * in the driver ThreadLocal, which keeps parallel TestNG classes isolated.
 */
public abstract class BaseTest {

    private static final Logger LOG = LogManager.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected LoginPage loginPage;
    protected DashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        LOG.info("Preparing browser for test: {}", method.getName());
        DriverManager.initDriver();
        driver = DriverManager.getDriver();
        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String status = switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            default -> "SKIPPED";
        };

        try {
            if (DriverManager.hasDriver()) {
                ScreenshotUtils.captureAndAttach(result.getName() + " - " + status);
                if (result.getStatus() == ITestResult.FAILURE) {
                    ScreenshotUtils.attachPageSource(result.getName() + " - failure page source");
                }
            }
        } catch (RuntimeException exception) {
            LOG.warn("Unable to capture post-test evidence: {}", exception.getMessage());
        } finally {
            DriverManager.quitDriver();
        }
    }

    /**
     * Shared login step for all modules that require an authenticated session.
     * Credentials are read from the JSON data source, not hardcoded here.
     */
    protected DashboardPage loginAsAdmin() {
        LoginScenario validLogin = TestDataProvider.loginScenario("valid");
        return Allure.step("Log in with valid credentials", () ->
                loginPage.open()
                        .login(validLogin.username(), validLogin.password())
                        .waitUntilLoaded()
        );
    }
}
