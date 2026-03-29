package ge.tbc.testautomation.ui;

import ge.tbc.testautomation.BaseTest;

import ge.tbc.testautomation.data.CurrencyDataProvider;
import org.testng.annotations.Test;

import ge.tbc.testautomation.util.RetryAnalyzer;
import ge.tbc.testautomation.util.RetryCount;


public class CurrencyExchangeTest extends BaseTest {

    protected CurrencyExchangeTest(String browser, String deviceType) {
        super(browser, deviceType);
    }

    @Test(description = "Navigate to the currency exchange page", priority = 1, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 1)
    public void NavigateToCurrencyExchangePage() {
        commonSteps
            .openPersonalNavigation()
            .clickCurrencyExchangeLink();
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
            priority = 2, dataProvider = "currencyAmounts",
            dataProviderClass = CurrencyDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencyConversionAmounts(String fromCurrency, String toCurrency, double amount) {
        convertorSteps
                .selectFromCurrency(fromCurrency)
                .selectToCurrency(toCurrency)
                .enterCurrencyAmount(amount)
                .verifyConversion(amount);
    }

    @Test(description = "Verify swap button flips currencies and recalculates conversion",
        priority = 3, dataProvider = "currencyPairs",
        dataProviderClass = CurrencyDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    @RetryCount(count = 2)
    public void currencySwap(String fromCurrency, String toCurrency, double amount) {
        convertorSteps
            .selectFromCurrency(fromCurrency)
            .selectToCurrency(toCurrency)
            .enterCurrencyAmount(amount)
            .verifyConversion(amount)
            .clickSwap()
            .validateSwap(toCurrency, fromCurrency, amount);
    }
}
