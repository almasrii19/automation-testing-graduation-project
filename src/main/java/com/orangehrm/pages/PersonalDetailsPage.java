package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Employee Personal Details page shown after a successful save.
 */
public class PersonalDetailsPage extends BasePage {

    private static final By PERSONAL_DETAILS_HEADING =
            By.xpath("//h6[contains(@class,'orangehrm-main-title')][normalize-space()='Personal Details']");
    private static final By FIRST_NAME_INPUT = By.name("firstName");
    private static final By LAST_NAME_INPUT = By.name("lastName");
    private static final By PIM_MENU = By.cssSelector("a[href*='pim/viewPimModule']");

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
    }

    public PersonalDetailsPage waitUntilLoaded() {
        waitForUrlContains("/pim/viewPersonalDetails");
        waitForVisible(PERSONAL_DETAILS_HEADING);
        waitForInputValue(FIRST_NAME_INPUT);
        waitForInputValue(LAST_NAME_INPUT);
        return this;
    }

    public boolean isPersonalDetailsDisplayed() {
        return isDisplayed(PERSONAL_DETAILS_HEADING) && getCurrentUrl().contains("/pim/viewPersonalDetails");
    }

    public String getFirstName() {
        return waitForInputValue(FIRST_NAME_INPUT);
    }

    public String getLastName() {
        return waitForInputValue(LAST_NAME_INPUT);
    }

    public PimEmployeeListPage openEmployeeList() {
        log.info("Returning to PIM > Employee List from Personal Details");
        click(PIM_MENU);
        return new PimEmployeeListPage(driver).waitUntilLoaded();
    }
}
