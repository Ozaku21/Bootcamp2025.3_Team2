package ge.tbc.testautomation.util;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ViewportSize;

public class DeviceConfig {

    public static Browser launchBrowser(Playwright playwright) {
        return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    public static BrowserContext createContext(Browser browser, DeviceType deviceType) {
        ViewportSize viewport;

        if (deviceType == DeviceType.MOBILE) {
            viewport = new ViewportSize(430, 932);
        } else {
            viewport = new ViewportSize(1280, 720);
        }

        return browser.newContext(new Browser.NewContextOptions().setViewportSize(viewport));
    }
}