package ge.tbc.testautomation.ui;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import static ge.tbc.testautomation.data.Constants.*;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups = {"Scenario - Validate Invalid Input - TP-T1"})
public class InvalidInputTest extends BaseTest {

    protected InvalidInputTest(String browserName, String deviceType) {
        super(browserName, deviceType);
    }

    @Story("Navigate to currency converter page")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 1)
    public void navigateToCurrencyConventorPage() {
        commonSteps
                .clickKebabMenu()
                .clickCurrencyLariOutlined();
    }

    @Story("Select GEL to EUR currency conversion")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2)
    public void selectCurrency() {
        conventorSteps
                .openDropdown(0)
                .selectCurrency(GEL)
                .openDropdown(1)
                .selectCurrency(EUR);
    }

    @Story("Enter amount for conversion")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 3)
    public void enterAmount() {
        conventorSteps
                .fillInputField(INVALID_AMOUNT);
    }

    @Story("Validate Invalid Input")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 4)
    public void validateInvalidInput() {
        conventorSteps
                .compareInputAmountAndValidate();
    }
}