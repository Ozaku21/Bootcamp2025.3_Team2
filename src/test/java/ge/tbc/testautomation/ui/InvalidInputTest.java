package ge.tbc.testautomation.ui;

import ge.tbc.testautomation.BaseTest;
import ge.tbc.testautomation.util.RetryAnalyzer;
import ge.tbc.testautomation.util.RetryCount;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import static ge.tbc.testautomation.data.Constants.*;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups = {"Scenario - Validate Invalid Input in Currency Converter - SCRUM-T4"})
public class InvalidInputTest extends BaseTest {

    protected InvalidInputTest(String browserName, String deviceType) {
        super(browserName, deviceType);
    }

    @Story("Navigate to currency converter page")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 1)
    public void navigateToCurrencyConvertorPage() {
        commonSteps
                .clickKebabMenu()
                .clickCurrencyLariOutlined();
    }

    @Story("Select GEL to EUR currency conversion")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void selectCurrency() {
        convertorSteps
                .selectFromCurrency(GEL)
                .selectToCurrency(EUR);
    }

    @Story("Enter amount for conversion")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 3, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 1)
    public void enterAmount() {
        convertorSteps
                .fillInputField(INVALID_AMOUNT);
    }

    @Story("Validate Invalid Input")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 4, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void validateInvalidInput() {
        convertorSteps
                .compareInputAmountAndValidate();
    }
}