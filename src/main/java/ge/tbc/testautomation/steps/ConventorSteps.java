package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;
import ge.tbc.testautomation.pages.ConventorPage;
import io.qameta.allure.Step;
import org.testng.Assert;

public class ConventorSteps {
    Page page;
    ConventorPage conventorPage;

    public ConventorSteps(Page page) {
        this.page = page;
        this.conventorPage = new ConventorPage(page);
    }

    @Step("Fill input field with amount: {amount}")
    public ConventorSteps fillInputField(String amount) {
        conventorPage
                .inputField
                .clear();
        conventorPage
                .inputField
                .fill(amount);
        return this;
    }

    public ConventorSteps getInputLengthAndValidate() {
        int length =   conventorPage
                .inputField
                .textContent()
                .trim()
                .length();
        Assert.assertTrue(length <= 16);
        return this;
    }


    @Step("Open currency dropdown at index: {dropdownIndex}")
    public ConventorSteps openDropdown(int dropdownIndex) {
        conventorPage
                .currencyDropdown
                .nth(dropdownIndex)
                .click();
        return this;
    }

    @Step("Select currency: {currency}")
    public ConventorSteps selectCurrency(String currency) {
        conventorPage
                .currencyItem(currency)
                .click();
        return this;
    }
}