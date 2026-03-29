package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import ge.tbc.testautomation.pages.CommonPage;
import ge.tbc.testautomation.util.DeviceType;
import io.qameta.allure.Step;

public class CommonSteps {
    protected CommonPage commonPage;
    protected Page page;
    protected DeviceType deviceType;

    public CommonSteps(Page page, DeviceType deviceType) {
        this.page = page;
        this.deviceType = deviceType;
        this.commonPage = new CommonPage(page, deviceType);
    }

    public void clickElement(Locator element, boolean waitForMenu) {
        if (deviceType == DeviceType.PHONE) {
            element.waitFor();
            if (waitForMenu) {
                page.waitForTimeout(1000);
            }
            element.dispatchEvent("click");
        } else {
            element.click();
        }
    }

    @Step("Open personal navigation context")
    public CommonSteps openPersonalNavigation() {
        if (deviceType == DeviceType.DESKTOP) {
            hoverPersonalNav();
        } else {
            clickMenuButton();
        }
        return this;
    }

    @Step("Accept cookies")
    public CommonSteps clickAcceptCookiesButton() {
        try {
            commonPage.acceptCookiesButton.click();
        } catch (Exception e) {
            // Cookie prompt not present, skip
        }
        return this;
    }


    @Step("Hover personal navigation")
    public CommonSteps hoverPersonalNav() {
        if(deviceType == DeviceType.DESKTOP){
            commonPage.PersonalNav().hover();
        }
        return this;
    }


    @Step("Click menu button")
    public CommonSteps clickMenuButton() {
        if(deviceType == DeviceType.PHONE){
            commonPage.MenuButton().click();
        }
        return this;
    }

    @Step("Click treasury products link")
    public CommonSteps clickCurrencyExchangeLink() {
        if (deviceType == DeviceType.DESKTOP) {
            commonPage.currencyExchangeLink.click();
        } else {
            clickElement(commonPage.currencyExchangeLink, true);
        }
        return this;
    }

}
