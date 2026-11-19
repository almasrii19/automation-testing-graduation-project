package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * PIM > Employee List: employee search and navigation to Add Employee.
 */
public class PimEmployeeListPage extends BasePage {

    private static final By EMPLOYEE_NAME_INPUT = By.cssSelector("input[placeholder='Type for hints...']");
    private static final By SEARCH_BUTTON = By.xpath("//button[@type='submit' and normalize-space()='Search']");
    private static final By AUTOCOMPLETE_OPTIONS = By.cssSelector(".oxd-autocomplete-option");
    private static final By AUTOCOMPLETE_DROPDOWN = By.cssSelector(".oxd-autocomplete-dropdown");
    private static final By ADD_EMPLOYEE_LINK = By.xpath("//a[normalize-space()='Add Employee']");
    private static final By TABLE_ROWS = By.cssSelector(".oxd-table-body .oxd-table-row");
    private static final By RECORDS_FOUND = By.xpath("//span[contains(normalize-space(),'Record') and contains(normalize-space(),'Found')]");
    private static final By NO_RECORDS_FOUND_IN_RESULTS = By.xpath(
            "//div[contains(@class,'orangehrm-paper-container')]"
                    + "//span[normalize-space()='No Records Found']"
    );

    public PimEmployeeListPage(WebDriver driver) {
        super(driver);
    }

    public PimEmployeeListPage waitUntilLoaded() {
        waitForUrlContains("/pim/viewEmployeeList");
        waitForVisible(EMPLOYEE_NAME_INPUT);
        waitForSpinnerToDisappear();
        return this;
    }

    public void enterEmployeeName(String name) {
        type(EMPLOYEE_NAME_INPUT, name);
    }

    public void selectEmployeeSuggestion(String fullName) {
        By suggestion = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-option')][normalize-space()="
                        + xpathLiteral(fullName) + "]"
        );
        click(suggestion);
        wait.until(driver -> driver.findElements(AUTOCOMPLETE_DROPDOWN).isEmpty());
    }

    public void clickSearch() {
        click(SEARCH_BUTTON);
    }

    public PimEmployeeListPage searchByEmployeeName(String firstName, String lastName) {
        String fullName = (firstName + " " + lastName).trim().replaceAll("\\s+", " ");
        enterEmployeeName(fullName);
        selectEmployeeSuggestion(fullName);
        clickSearch();
        waitForEmployeeInResults(firstName, lastName);
        return this;
    }

    public boolean waitForEmployeeInResults(String firstName, String lastName) {
        try {
            wait.until(driver -> employeeRowExists(firstName, lastName));
            return true;
        } catch (TimeoutException exception) {
            log.warn("Employee '{} {}' did not appear in the results table", firstName, lastName);
            return false;
        }
    }

    public boolean isEmployeeInResults(String firstName, String lastName) {
        return employeeRowExists(firstName, lastName);
    }

    public List<String> getResultRows() {
        return readResultRows();
    }

    /**
     * The Vue results table re-renders after search, so elements can become stale
     * while they are being read. Re-querying keeps the check deterministic.
     */
    private boolean employeeRowExists(String firstName, String lastName) {
        return readResultRows().stream()
                .anyMatch(row -> row.contains(firstName) && row.contains(lastName));
    }

    private List<String> readResultRows() {
        try {
            return driver.findElements(TABLE_ROWS).stream()
                    .map(WebElement::getText)
                    .map(text -> text.replace("\n", " | "))
                    .toList();
        } catch (org.openqa.selenium.StaleElementReferenceException exception) {
            log.debug("Results table re-rendered while being read; retrying the check");
            return List.of();
        }
    }

    public String getRecordsFoundText() {
        return textOf(RECORDS_FOUND);
    }

    /**
     * Waits for the post-search empty state in the results area. The Employee Name
     * autocomplete can also show "No Records Found"; this locator is scoped to the
     * results container, so it only matches after the Search request has completed.
     */
    public boolean waitForNoRecordsFoundInResults() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(NO_RECORDS_FOUND_IN_RESULTS));
            return true;
        } catch (TimeoutException exception) {
            log.warn("'No Records Found' was not displayed in the Employee List results");
            return false;
        }
    }

    public boolean hasAutocompleteOptions() {
        return !driver.findElements(AUTOCOMPLETE_OPTIONS).isEmpty();
    }

    public AddEmployeePage clickAddEmployee() {
        log.info("Opening PIM > Add Employee");
        click(ADD_EMPLOYEE_LINK);
        return new AddEmployeePage(driver).waitUntilLoaded();
    }
}
