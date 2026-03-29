package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;
import ge.tbc.testautomation.Pages.CommonPage;
import io.qameta.allure.Step;

public class CommonSteps {
    Page page;
    CommonPage commonPage;

    public CommonSteps(Page page, String deviceType) {
        this.page = page;
        this.commonPage = new CommonPage(page);
    }

    @Step("Accept cookies if present")
    public CommonSteps AcceptCookiesIfPresent() {
        commonPage
                .acceptCookiesButton
                .click();
        return this;
    }

    @Step("Click kebab menu")
    public CommonSteps clickKebabMenu() {
        commonPage
                .kebabMenuButton
                .click();
        return this;
    }

    @Step("Click currency lari icon")
    public CommonSteps clickCurrencyLariOutlined() {
        commonPage
                .currencyLariIconButton
                .click();
        return this;
    }
}