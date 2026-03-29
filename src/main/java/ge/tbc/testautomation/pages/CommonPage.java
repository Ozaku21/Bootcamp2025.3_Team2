package ge.tbc.testautomation.Pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class CommonPage {
    public Locator acceptCookiesButton,
            burgerMenuButton,
            kebabMenuButton,
            currencyLariIconButton,
            locationButton,
            searchButton,
            searchInputField,
            forMeNavigationItem,
            offersMenuItem,
            scrollBox;
    public CommonPage(Page page) {
        this.kebabMenuButton = page.getByText("kebab-menu-vertical-outlined").first();
        this.burgerMenuButton = page.getByText("burger-menu-alt-outlined");
        this.currencyLariIconButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("currency-lari-outlined"));
        this.searchButton = page.getByText("search-outlined").first();
        this.searchInputField = page.getByText("ძებნა ");
        this.forMeNavigationItem = page.getByText(" Personal ").first();
        this.offersMenuItem = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("discount-outlined შეთავაზებები"));
        this.scrollBox = page.locator("div.global-search__bottom-content");
        this.acceptCookiesButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("accept"));
        this.locationButton = page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("location-pin-outlined"));

    }
}
