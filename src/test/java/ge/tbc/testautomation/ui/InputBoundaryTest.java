package ge.tbc.testautomation.ui;

import ge.tbc.testautomation.BaseTest;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import static ge.tbc.testautomation.data.Constants.*;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups = {"Scenario - Validate Conventor Boundary - TP-T1"})
public class InputBoundaryTest extends BaseTest {

    protected InputBoundaryTest(String browserName, String deviceType) {
        super(browserName, deviceType);
    }

    @Story("Navigate to currency converter page")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 1)
    public void navigateToCurrencyConvertorPage() {
        commonSteps
                .clickKebabMenu()
                .clickCurrencyLariOutlined();
    }

    @Story("Select GEL to EUR currency conversion")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2)
    public void selectCurrency() {
        convertorSteps
                .openDropdown(0)
                .selectCurrency(GEL)
                .openDropdown(1)
                .selectCurrency(EUR);
    }

    @Story("Enter amount for conversion")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 3)
    public void enterAmount() {
        convertorSteps
                .fillInputField(AMOUNT);
    }

    @Story("Verify Boundary")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 4)
    public void verifyBoundary() {
        convertorSteps
                .getInputLengthAndValidate();
    }
}