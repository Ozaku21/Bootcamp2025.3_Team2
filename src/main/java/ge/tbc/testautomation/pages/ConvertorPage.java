package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ConvertorPage extends CommonPage {

    public Locator currencyForm,
            currencyRate,
            currencyInputs,
            currencyInput1,
            currencyInput2,
            currencyButtons,
            fromCurrencyButton,
            toCurrencyButton,
            currencyList,
            swapButton;

    public ConvertorPage(Page page, String deviceType) {
        super(page, deviceType);

        this.currencyForm = page.locator(".tbcx-pw-exchange-rates-calculator");
        this.currencyRate = page.locator("div.tbcx-pw-exchange-rates-calculator__description");
        this.currencyInputs = page.locator("div.input-with-label input");
        this.currencyInput1 = currencyInputs.nth(0);
        this.currencyInput2 = currencyInputs.nth(1);
        this.currencyButtons = page.locator("button.tbcx-field.tbcx-bg-color-high");
        this.fromCurrencyButton = currencyButtons.nth(0);
        this.toCurrencyButton = currencyButtons.nth(1);
        this.currencyList = page.locator("div.tbcx-item-list");
        this.swapButton = currencyForm.locator(".tbcx-pw-exchange-rates-calculator__swap");
    }

    public Locator getVisibleCurrencyList() {
        currencyList.first().waitFor();
        for (Locator loc : currencyList.all()) {
            if (loc.isVisible()) {
                return loc;
            }
        }
        return currencyList.first();
    }

    public Locator getCurrencyItem(String currency) {
        return getVisibleCurrencyList().getByText(currency).first();
    }
}