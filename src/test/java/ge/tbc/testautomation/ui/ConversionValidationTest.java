package ge.tbc.testautomation.ui;

import ge.tbc.testautomation.BaseTest;
import ge.tbc.testautomation.data.CurrencyDataProvider;
import ge.tbc.testautomation.util.RetryAnalyzer;
import ge.tbc.testautomation.util.RetryCount;
import org.testng.annotations.Test;

public class ConversionValidationTest extends BaseTest {

    protected ConversionValidationTest(String browser, String deviceType) {
        super(browser, deviceType);
    }

    @Test(description = "Navigate to the currency exchange page", priority = 1, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 1)
    public void navigateToCurrencyConvertorPage() {
        commonSteps
                .clickKebabMenu()
                .clickCurrencyLariOutlined();
    }

    @Test(description = "Verify currency conversion across multiple currency pairs",
            priority = 2, dataProvider = "currencyPairs",
            dataProviderClass = CurrencyDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencyConversionPairs(String fromCurrency, String toCurrency, double amount) {
        convertorSteps
                .selectFromCurrency(fromCurrency)
                .selectToCurrency(toCurrency)
                .enterCurrencyAmount(amount)
                .verifyConversion(amount);
    }

    @Test(description = "Verify currency conversion across multiple amounts",
            priority = 3, dataProvider = "currencyAmounts",
            dataProviderClass = CurrencyDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencyConversionAmounts(String fromCurrency, String toCurrency, double amount) {
        convertorSteps
                .selectFromCurrency(fromCurrency)
                .selectToCurrency(toCurrency)
                .enterCurrencyAmount(amount)
                .verifyConversion(amount);
    }
}
