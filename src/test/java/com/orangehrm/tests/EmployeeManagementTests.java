package com.orangehrm.tests;

import com.orangehrm.data.TestDataProvider;
import com.orangehrm.listeners.RetryAnalyzer;
import com.orangehrm.models.EmployeeData;
import com.orangehrm.pages.AddEmployeePage;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.PersonalDetailsPage;
import com.orangehrm.pages.PimEmployeeListPage;
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
@Feature("PIM - Employee Management")
public class EmployeeManagementTests extends BaseTest {

    @Test(
            description = "TC04 - Search for an existing employee",
            dataProvider = "existingEmployeeData",
            dataProviderClass = TestDataProvider.class,
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Employee search")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a known employee can be found through PIM Employee List.")
    public void searchForExistingEmployee(EmployeeData employee) {
        DashboardPage dashboard = loginAsAdmin();
        PimEmployeeListPage pimPage = Allure.step("Navigate to PIM > Employee List",
                dashboard::openPimModule);

        Allure.step("Search for known employee: " + employee.fullName(),
                () -> pimPage.searchByEmployeeName(employee.firstName(), employee.lastName()));

        Allure.step("Verify the employee appears in the results table", () -> {
            Assert.assertTrue(pimPage.isEmployeeInResults(employee.firstName(), employee.lastName()),
                    "Employee '" + employee.fullName() + "' was not found. Rows: " + pimPage.getResultRows());
            Assert.assertFalse(pimPage.getResultRows().isEmpty(),
                    "The employee results table should contain at least one row");
        });

        ScreenshotUtils.captureAndAttach("Existing employee search result");
    }

    @Test(
            description = "TC05 - Search for a non-existing employee",
            dataProvider = "nonExistingEmployeeData",
            dataProviderClass = TestDataProvider.class,
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Employee search - negative")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a random non-existing name produces the 'No Records Found' message.")
    public void searchForNonExistingEmployee(EmployeeData employee) {
        DashboardPage dashboard = loginAsAdmin();
        PimEmployeeListPage pimPage = Allure.step("Navigate to PIM > Employee List",
                dashboard::openPimModule);
        String randomName = TestDataProvider.uniqueNonExistingFullName();

        Allure.step("Enter a random non-existing employee name: " + randomName,
                () -> pimPage.enterEmployeeName(randomName));

        Allure.step("Verify the 'No Records Found' message is displayed", () -> {
            Assert.assertTrue(pimPage.isNoRecordsFoundMessageDisplayed(),
                    "OrangeHRM should display 'No Records Found' for the random name: " + randomName);
            ScreenshotUtils.captureAndAttach("No Records Found message");
        });

        Allure.step("Click Search", pimPage::clickSearch);

        Allure.step("Verify the random name is not present in the results", () -> {
            List<String> rows = pimPage.getResultRows();
            Assert.assertTrue(rows.stream().noneMatch(row -> row.contains(randomName)),
                    "A non-existing employee must not appear in the results table");
            Assert.assertTrue(pimPage.getCurrentUrl().contains("/pim/viewEmployeeList"),
                    "The user should remain on the Employee List page");
        });
    }

    @Test(
            description = "TC06 - Open the Add Employee page",
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Add Employee navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that PIM > Add Employee opens the expected page and required name fields.")
    public void openAddEmployeePage() {
        DashboardPage dashboard = loginAsAdmin();
        PimEmployeeListPage pimPage = Allure.step("Navigate to PIM > Employee List",
                dashboard::openPimModule);

        AddEmployeePage addEmployeePage = Allure.step("Click Add Employee", pimPage::clickAddEmployee);

        Allure.step("Verify Add Employee page URL and fields", () -> {
            Assert.assertTrue(driver.getCurrentUrl().contains("/pim/addEmployee"),
                    "Expected URL to contain /pim/addEmployee but was: " + driver.getCurrentUrl());
            Assert.assertTrue(addEmployeePage.isFirstNameFieldDisplayed(), "First Name field should be displayed");
            Assert.assertTrue(addEmployeePage.isLastNameFieldDisplayed(), "Last Name field should be displayed");
        });

        ScreenshotUtils.captureAndAttach("Add Employee page");
    }

    @Test(
            description = "TC07 - Add employee with First Name empty",
            dataProvider = "newEmployeeData",
            dataProviderClass = TestDataProvider.class,
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Add Employee validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that First Name is mandatory when creating an employee.")
    public void addEmployeeWithEmptyFirstName(EmployeeData employee) {
        DashboardPage dashboard = loginAsAdmin();
        PimEmployeeListPage pimPage = Allure.step("Navigate to PIM > Employee List",
                dashboard::openPimModule);
        AddEmployeePage addEmployeePage = Allure.step("Open PIM > Add Employee", pimPage::clickAddEmployee);

        Allure.step("Enter Last Name only", () -> addEmployeePage.enterLastName(employee.lastName()));
        Allure.step("Click Save", addEmployeePage::clickSave);

        Allure.step("Verify the First Name Required error", () -> {
            Assert.assertTrue(addEmployeePage.isFirstNameRequiredErrorDisplayed(),
                    "A Required validation message should be displayed under First Name");
            Assert.assertTrue(driver.getCurrentUrl().contains("/pim/addEmployee"),
                    "The employee must not be created without a First Name");
        });

        ScreenshotUtils.captureAndAttach("First Name Required validation");
    }

    @Test(
            description = "TC08 - End-to-end add employee",
            dataProvider = "newEmployeeData",
            dataProviderClass = TestDataProvider.class,
            retryAnalyzer = RetryAnalyzer.class
    )
    @Story("Add Employee end-to-end")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Create an employee, verify Personal Details, then find the employee in Employee List.")
    public void addEmployeeEndToEnd(EmployeeData employee) {
        String firstName = employee.firstName();
        String lastName = TestDataProvider.uniqueLastName();

        DashboardPage dashboard = loginAsAdmin();
        PimEmployeeListPage pimPage = Allure.step("Navigate to PIM > Employee List",
                dashboard::openPimModule);
        AddEmployeePage addEmployeePage = Allure.step("Open PIM > Add Employee", pimPage::clickAddEmployee);

        Allure.step("Enter a valid First Name and Last Name", () -> {
            addEmployeePage.enterFirstName(firstName);
            addEmployeePage.enterLastName(lastName);
        });

        PersonalDetailsPage personalDetails = Allure.step("Save the new employee",
                addEmployeePage::saveSuccessfully);

        Allure.step("Verify the Personal Details page for the new employee", () -> {
            Assert.assertTrue(personalDetails.isPersonalDetailsDisplayed(),
                    "The Personal Details page should open after saving a valid employee");
            Assert.assertEquals(personalDetails.getFirstName(), firstName,
                    "The saved First Name should be displayed on Personal Details");
            Assert.assertEquals(personalDetails.getLastName(), lastName,
                    "The saved Last Name should be displayed on Personal Details");
        });

        ScreenshotUtils.captureAndAttach("New employee Personal Details");

        PimEmployeeListPage employeeList = Allure.step("Navigate back to Employee List",
                personalDetails::openEmployeeList);
        Allure.step("Search for the newly created employee",
                () -> employeeList.searchByEmployeeName(firstName, lastName));

        Allure.step("Verify the new employee appears in the results", () -> {
            Assert.assertTrue(employeeList.isEmployeeInResults(firstName, lastName),
                    "The new employee should appear in Employee List. Rows: " + employeeList.getResultRows());
        });

        ScreenshotUtils.captureAndAttach("New employee in search results");
    }
}
