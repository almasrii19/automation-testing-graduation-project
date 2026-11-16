package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * PIM > Add Employee form.
 */
public class AddEmployeePage extends BasePage {

    private static final By FIRST_NAME_INPUT = By.name("firstName");
    private static final By MIDDLE_NAME_INPUT = By.name("middleName");
    private static final By LAST_NAME_INPUT = By.name("lastName");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");
    private static final By FIELD_ERRORS = By.cssSelector(".oxd-input-field-error-message");

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    public AddEmployeePage waitUntilLoaded() {
        waitForUrlContains("/pim/addEmployee");
        waitForVisible(FIRST_NAME_INPUT);
        waitForFormLoaderToDisappear();
        return this;
    }

    public void enterFirstName(String firstName) {
        type(FIRST_NAME_INPUT, firstName);
    }

    public void enterMiddleName(String middleName) {
        type(MIDDLE_NAME_INPUT, middleName);
    }

    public void enterLastName(String lastName) {
        type(LAST_NAME_INPUT, lastName);
    }

    public void clickSave() {
        click(SAVE_BUTTON);
    }

    public PersonalDetailsPage saveSuccessfully() {
        clickSave();
        return new PersonalDetailsPage(driver).waitUntilLoaded();
    }

    public boolean isFirstNameFieldDisplayed() {
        return isDisplayed(FIRST_NAME_INPUT);
    }

    public boolean isLastNameFieldDisplayed() {
        return isDisplayed(LAST_NAME_INPUT);
    }

    public boolean isFirstNameRequiredErrorDisplayed() {
        By requiredError = By.xpath(
                "//input[@name='firstName']"
                        + "/ancestor::div[contains(@class,'oxd-input-group')][1]"
                        + "//span[contains(@class,'oxd-input-field-error-message')]"
        );
        return isDisplayed(requiredError) && "Required".equals(textOf(requiredError));
    }

    public List<String> getVisibleValidationErrors() {
        return textsOf(FIELD_ERRORS);
    }
}
