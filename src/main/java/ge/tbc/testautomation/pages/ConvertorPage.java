package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ConvertorPage extends CommonPage {

    public Locator currencyForm,
            currencyRate,
            currencyInput1,
            currencyInput2,
            fromCurrencyButton,
            toCurrencyButton,
            swapButton,
            currencyElements;

    public ConvertorPage(Page page) {
        super(page);

        this.currencyForm = page.locator(".tbcx-pw-exchange-rates-calculator");
        this.currencyRate = page.locator("div.tbcx-pw-exchange-rates-calculator__description");
        this.currencyInput1 = page.locator("div.input-with-label input").nth(0);
        this.currencyInput2 = page.locator("div.input-with-label input").nth(1);
        this.fromCurrencyButton = page.locator("button.tbcx-field.tbcx-bg-color-high").nth(0);
        this.toCurrencyButton = page.locator("button.tbcx-field.tbcx-bg-color-high").nth(1);
        this.swapButton = currencyForm.locator(".tbcx-pw-exchange-rates-calculator__swap");
        this.currencyElements = page.locator(".cdk-overlay-pane .tbcx-dropdown-popover-item__title");
    }

    public Locator currencyItem(String currency) {
        return currencyElements.getByText(currency, new Locator.GetByTextOptions().setExact(true));
    }
}