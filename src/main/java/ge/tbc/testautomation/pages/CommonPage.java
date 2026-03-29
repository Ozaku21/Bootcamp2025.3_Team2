package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import static ge.tbc.testautomation.data.Constants.*;

public class CommonPage {
    public Locator currencyExchangeLink,
            acceptCookiesButton;

    public Page page;
    public String deviceType;

    public CommonPage(Page page, String deviceType) {
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

    public Locator PersonalNav() {
        if (DESKTOP.equalsIgnoreCase(deviceType)) {
            return page.getByRole(
                            AriaRole.BANNER)
                    .getByRole(
                            AriaRole.LINK,
                            new Locator.GetByRoleOptions().setName("Personal")
                    ).first();
        }
        return null;
    }

    public Locator MenuButton() {
        if (MOBILE.equalsIgnoreCase(deviceType)) {
            return page.locator(".tbcx-pw-hamburger-menu__button");
        }
        return null;
    }
}
