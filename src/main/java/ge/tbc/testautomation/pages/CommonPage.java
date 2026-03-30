package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class CommonPage {
    public Locator  acceptCookiesButton,
                    kebabMenuButton,
                    currencyLariIconButton;

    public CommonPage(Page page) {
        this.kebabMenuButton = page.getByText("kebab-menu-vertical-outlined").first();
        this.currencyLariIconButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("currency-lari-outlined"));
        this.acceptCookiesButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("accept"));
    }
}
