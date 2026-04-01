package ge.tbc.testautomation.ui;

import ge.tbc.testautomation.BaseTest;
import ge.tbc.testautomation.data.CurrencyDataProvider;
import ge.tbc.testautomation.util.RetryAnalyzer;
import ge.tbc.testautomation.util.RetryCount;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups = {"Scenario - Validate Currency Conversion - SCRUM-T2"})
public class ConversionValidationTest extends BaseTest {

    protected ConversionValidationTest(String browser, String deviceType) {
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

    @Story("Validate conversion across currency pairs")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, dataProvider = "currencyPairs", dataProviderClass = CurrencyDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencyConversionPairs(String fromCurrency, String toCurrency, double amount) {
        convertorSteps
                .selectFromCurrency(fromCurrency)
                .selectToCurrency(toCurrency)
                .enterCurrencyAmount(amount)
                .verifyConversion(amount);
    }

    @Story("Validate conversion across various amounts")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 3, dataProvider = "currencyAmounts", dataProviderClass = CurrencyDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencyConversionAmounts(String fromCurrency, String toCurrency, double amount) {
        convertorSteps
                .selectFromCurrency(fromCurrency)
                .selectToCurrency(toCurrency)
                .enterCurrencyAmount(amount)
                .verifyConversion(amount);
    }
}