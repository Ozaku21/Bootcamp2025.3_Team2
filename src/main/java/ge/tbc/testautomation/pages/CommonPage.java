package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import ge.tbc.testautomation.util.DeviceType;
import com.microsoft.playwright.options.AriaRole;

public class CommonPage {
    public Locator currencyExchangeLink,
            acceptCookiesButton;

    public Page page;
    public DeviceType deviceType;

    public CommonPage(Page page, DeviceType deviceType) {
        this.page = page;
        this.deviceType = deviceType;

        this.acceptCookiesButton = page.locator("button.primary.size-s.state-initial");
        this.currencyExchangeLink = page.getByRole(
                        AriaRole.BANNER)
                .getByRole(
                        AriaRole.LINK,
                        new Locator.GetByRoleOptions().setName("Currency Exchange")
                ).first();
    }

    public Locator PersonalNav(){
        if(deviceType == DeviceType.DESKTOP){
            return page.getByRole(
                            AriaRole.BANNER)
                    .getByRole(
                            AriaRole.LINK,
                            new Locator.GetByRoleOptions().setName("Personal")
                    ).first();
        }
        return null;
    }

    public Locator MenuButton(){
        if(deviceType == DeviceType.MOBILE){
            return page.locator(".tbcx-pw-hamburger-menu__button");
        }
        return null;
    }
}
