package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;

import ge.tbc.testautomation.pages.ConvertorPage;
import io.qameta.allure.Step;
import org.testng.Assert;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ConvertorSteps extends CommonSteps {

    ConvertorPage convertorPage;

    public ConvertorSteps(Page page, String deviceType) {
        super(page, deviceType);
        convertorPage = new ConvertorPage(page, deviceType);
    }

    @Step("Select from currency: {currency}")
    public ConvertorSteps selectFromCurrency(String currency) {
        convertorPage.fromCurrencyButton.click();
        convertorPage.getCurrencyItem(currency).scrollIntoViewIfNeeded();
        convertorPage.getCurrencyItem(currency).click();
        return this;
    }

    @Step("Select to currency: {currency}")
    public ConvertorSteps selectToCurrency(String currency) {
        convertorPage.toCurrencyButton.click();
        convertorPage.getCurrencyItem(currency).scrollIntoViewIfNeeded();
        convertorPage.getCurrencyItem(currency).click();
        return this;
    }

    @Step("Enter currency amount: {amount}")
    public ConvertorSteps enterCurrencyAmount(double amount) {
        convertorPage.currencyInput1.fill(String.valueOf((int) amount));
        return this;
    }

    @Step("Swap the conversion")
    public ConvertorSteps clickSwap(){
        convertorPage.swapButton.click();
        return this;
    }

    @Step("Verify the currencies are swapped: {expectedFrom} -> {expectedTo}")
    public ConvertorSteps validateSwap(String expectedFrom, String expectedTo, double amount){
        assertThat(convertorPage.fromCurrencyButton).hasText(expectedFrom);
        assertThat(convertorPage.toCurrencyButton).hasText(expectedTo);
        verifyConversion(amount);
        return this;
    }

    @Step("Verify conversion for amount: {inputAmount}")
    public ConvertorSteps verifyConversion(double inputAmount) {
        try {
            assertThat(convertorPage.currencyRate).isVisible();
            assertThat(convertorPage.currencyInput2).isVisible();

            double rate = parseRate(convertorPage.currencyRate.textContent());
            double actual = parseAmount(convertorPage.currencyInput2.inputValue());
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