package ge.tbc.testautomation.ui;

import ge.tbc.testautomation.BaseTest;
import ge.tbc.testautomation.data.CurrencyDataProvider;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import ge.tbc.testautomation.util.RetryAnalyzer;
import ge.tbc.testautomation.util.RetryCount;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups = {"Scenario - Validate Currency Swap - TP-T2"})
public class ConversionSwapTest extends BaseTest {

    protected ConversionSwapTest(String browser, String deviceType) {
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

    @Story("Swap currencies and validate recalculation")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2, dataProvider = "currencyPairs", dataProviderClass = CurrencyDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
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