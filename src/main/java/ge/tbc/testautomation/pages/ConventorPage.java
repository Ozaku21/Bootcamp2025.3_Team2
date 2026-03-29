package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ConventorPage {
    public Locator inputField,
                   currencyDropdown,
                   currencyElements;
    public ConventorPage(Page page) {
        this.inputField = page.locator("form input").first();
        this.currencyDropdown = page.locator("button.tbcx-field");
        this.currencyElements = page.locator(".cdk-overlay-pane .tbcx-dropdown-popover-item__title");
    }
    public Locator currencyItem(String currency) {
        return currencyElements.getByText(currency, new Locator.GetByTextOptions().setExact(true));
    }
}
