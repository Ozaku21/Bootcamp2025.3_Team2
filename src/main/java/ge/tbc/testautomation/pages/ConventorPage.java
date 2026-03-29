package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ConventorPage {
    public Locator inputField,
            outputField,
            currencyDropdown,
            exchangeRateText,
            currencyElements;
    public ConventorPage(Page page) {
        this.inputField = page.locator("form input").first();
        this.currencyDropdown = page.locator("button.tbcx-field");
        this.currencyElements = page.locator(".cdk-overlay-pane .tbcx-dropdown-popover-item__title");
        this.exchangeRateText = page.locator(".tbcx-pw-exchange-rates-calculator__description");
        this.outputField = page.locator("form input").last();
    }
    public Locator currencyItem(String currency) {
        return currencyElements.getByText(currency, new Locator.GetByTextOptions().setExact(true));
    }
}
