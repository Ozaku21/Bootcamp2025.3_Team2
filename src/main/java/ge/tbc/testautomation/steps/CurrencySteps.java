package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;

import ge.tbc.testautomation.pages.CurrencyPage;
import ge.tbc.testautomation.util.DeviceType;
import io.qameta.allure.Step;
import org.testng.Assert;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CurrencySteps extends CommonSteps {

    CurrencyPage currencyPage;

    public CurrencySteps(Page page, DeviceType deviceType) {
        super(page, deviceType);
        currencyPage = new CurrencyPage(page, deviceType);
    }

    @Step("Select from currency: {currency}")
    public CurrencySteps selectFromCurrency(String currency) {
        currencyPage.fromCurrencyButton.click();
        currencyPage.getCurrencyItem(currency).scrollIntoViewIfNeeded();
        currencyPage.getCurrencyItem(currency).click();
        return this;
    }

    @Step("Select to currency: {currency}")
    public CurrencySteps selectToCurrency(String currency) {
        currencyPage.toCurrencyButton.click();
        currencyPage.getCurrencyItem(currency).scrollIntoViewIfNeeded();
        currencyPage.getCurrencyItem(currency).click();
        return this;
    }

    @Step("Enter currency amount: {amount}")
    public CurrencySteps enterCurrencyAmount(double amount) {
        currencyPage.currencyInput1.fill(String.valueOf((int) amount));
        return this;
    }

    @Step("Swap the conversion")
    public CurrencySteps clickSwap(){
        currencyPage.swapButton.click();
        return this;
    }

    @Step("Verify the currencies are swapped: {expectedFrom} -> {expectedTo}")
    public CurrencySteps validateSwap(String expectedFrom, String expectedTo, double amount){
        assertThat(currencyPage.fromCurrencyButton).hasText(expectedFrom);
        assertThat(currencyPage.toCurrencyButton).hasText(expectedTo);
        verifyConversion(amount);
        return this;
    }

    @Step("Verify conversion for amount: {inputAmount}")
    public CurrencySteps verifyConversion(double inputAmount) {
        try {
            assertThat(currencyPage.currencyRate).isVisible();
            assertThat(currencyPage.currencyInput2).isVisible();

            double rate = parseRate(currencyPage.currencyRate.textContent());
            double actual = parseAmount(currencyPage.currencyInput2.inputValue());
            double expected = inputAmount * rate;

            Assert.assertEquals(actual, expected, 0.5,
                    String.format("Conversion incorrect. Expected: %.2f, Actual: %.2f, Rate: %.4f",
                            expected, actual, rate));
        } catch (IllegalArgumentException e) {
            Assert.fail(e.getMessage(), e);
        }
        return this;
    }

    private static double parseRate(String rateText) {
        validate(rateText, "Currency rate text");
        String[] parts = rateText.split("=");
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Invalid rate format. Expected 'X = Y', got: " + rateText);
        }
        return parseDouble(parts[1].trim().split(" ")[0], "currency rate");
    }

    private static double parseAmount(String amountText) {
        validate(amountText, "Converted value");
        return parseDouble(amountText, "converted amount");
    }

    private static double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Failed to parse " + fieldName + " from: " + value, e);
        }
    }

    private static void validate(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is null or empty");
        }
    }
}