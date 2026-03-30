package ge.tbc.testautomation.ui;

import ge.tbc.testautomation.BaseTest;
import ge.tbc.testautomation.util.RetryAnalyzer;
import ge.tbc.testautomation.util.RetryCount;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.*;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups = {"Scenario - Validate currency input doesn't accept non-numeric value - TP-T4"})
public class NonNumericInputTest extends BaseTest {

    protected NonNumericInputTest(String browser, String deviceType) {
        super(browser, deviceType);
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

    @Story("Validate input doesn't accept non-numeric value")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencyConversionPairs() {
        convertorSteps
                .enterNonNumericData(NON_NUMERIC)
                .validateInputDidNotAcceptNonNumericData();
    }
}