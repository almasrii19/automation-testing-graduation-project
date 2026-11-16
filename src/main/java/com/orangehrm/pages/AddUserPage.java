package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Admin > Add User form.
 */
public class AddUserPage extends BasePage {

    private static final By ADD_USER_HEADING =
            By.xpath("//h6[contains(@class,'orangehrm-main-title')][normalize-space()='Add User']");
    private static final By FIELD_LABELS = By.cssSelector(".oxd-label");
    private static final String INPUT_GROUP =
            "ancestor::div[contains(concat(' ', normalize-space(@class), ' '), ' oxd-input-group ')][1]";
    private static final By USER_ROLE_SELECT = By.xpath("//label[normalize-space()='User Role']/"
            + INPUT_GROUP + "//div[contains(@class,'oxd-select-text')]");
    private static final By EMPLOYEE_NAME_INPUT = By.xpath("//label[normalize-space()='Employee Name']/"
            + INPUT_GROUP + "//input");
    private static final By USERNAME_INPUT = By.xpath("//label[normalize-space()='Username']/"
            + INPUT_GROUP + "//input");
    private static final By PASSWORD_INPUT = By.xpath("//label[normalize-space()='Password']/"
            + INPUT_GROUP + "//input");

    public AddUserPage(WebDriver driver) {
        super(driver);
    }

    public AddUserPage waitUntilLoaded() {
        waitForUrlContains("/admin/saveSystemUser");
        waitForVisible(ADD_USER_HEADING);
        waitForVisible(FIELD_LABELS);
        waitForFormLoaderToDisappear();
        return this;
    }

    public boolean isAddUserFormDisplayed() {
        return isDisplayed(ADD_USER_HEADING) && getCurrentUrl().contains("/admin/saveSystemUser");
    }

    public List<String> getFieldLabels() {
        return textsOf(FIELD_LABELS);
    }

    public boolean isFieldDisplayed(String label) {
        By fieldLabel = By.xpath("//label[normalize-space()=" + xpathLiteral(label) + "]");
        return isDisplayed(fieldLabel);
    }

    public boolean isUserRoleFieldDisplayed() {
        return isDisplayed(USER_ROLE_SELECT);
    }

    public boolean isEmployeeNameFieldDisplayed() {
        return isDisplayed(EMPLOYEE_NAME_INPUT);
    }

    public boolean isUsernameFieldDisplayed() {
        return isDisplayed(USERNAME_INPUT);
    }

    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(PASSWORD_INPUT);
    }
}
