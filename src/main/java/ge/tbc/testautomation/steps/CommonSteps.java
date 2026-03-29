package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;
import ge.tbc.testautomation.pages.CommonPage;
import io.qameta.allure.Step;
import static ge.tbc.testautomation.data.Constants.*;

public class CommonSteps {
    public CommonPage commonPage;
    public Page page;
    public String deviceType;

    public CommonSteps(Page page, String deviceType) {
        this.page = page;
        this.deviceType = deviceType;
        this.commonPage = new CommonPage(page, deviceType);
    }

    @Step("Open personal navigation context")
    public CommonSteps openPersonalNavigation() {
        if (DESKTOP.equalsIgnoreCase(deviceType)) {
            hoverPersonalNav();
        } else {
            clickMenuButton();
        }
        return this;
    }

    @Step("Accept cookies")
    public CommonSteps acceptCookiesIfPresent() {
        if (commonPage.acceptCookiesButton.isVisible()) {
            commonPage.acceptCookiesButton.click();
        }
        return this;
    }

    @Step("Hover personal navigation")
    public CommonSteps hoverPersonalNav() {
        if (DESKTOP.equalsIgnoreCase(deviceType)) {
            commonPage.PersonalNav().hover();
        }
        return this;
    }

    @Step("Click menu button")
    public CommonSteps clickMenuButton() {
        if (MOBILE.equalsIgnoreCase(deviceType)) {
            commonPage.MenuButton().click();
        }
        return this;
    }

    @Step("Click currency exchange link")
    public CommonSteps clickCurrencyExchangeLink() {
        if (DESKTOP.equalsIgnoreCase(deviceType)) {
            commonPage.currencyExchangeLink.click();
        } else {
            page.waitForTimeout(1000);
            commonPage.currencyExchangeLink.dispatchEvent("click");
        }
        return this;
    }
}