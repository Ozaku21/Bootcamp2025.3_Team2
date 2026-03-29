package ge.tbc.testautomation;

import com.microsoft.playwright.*;
import ge.tbc.testautomation.pages.CurrencyPage;
import ge.tbc.testautomation.steps.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import ge.tbc.testautomation.util.DeviceConfig;
import ge.tbc.testautomation.util.DeviceType;

import static ge.tbc.testautomation.data.Constants.*;

public class BaseTest {

    public Playwright playwright;
    public Browser browser;
    public BrowserContext browserContext;
    public Page page;
    public DeviceType deviceType;

    public CommonSteps commonSteps;
    public CurrencySteps currencySteps;


    protected String getBaseUrl() {
        return URL_TBC;
    }

    @BeforeClass(alwaysRun = true)
    @Parameters({"deviceType", "browser"})
    public void setUp(@Optional String deviceTypeParam, @Optional String browserParam) {

        // ── Device Type ──────────────────────────────────────────────
        if (deviceTypeParam == null || deviceTypeParam.isEmpty()) {
            deviceType = DeviceType.DESKTOP;
        } else {
            try {
                deviceType = DeviceType.valueOf(deviceTypeParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown deviceType, defaulting to DESKTOP");
                deviceType = DeviceType.DESKTOP;
            }
        }
        System.out.println("Device: " + deviceType);

        // ── Browser ──────────────────────────────────────────────────
        if (browserParam == null || browserParam.isEmpty()) {
            browserParam = "chromium";
        }

        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(false);

        switch (browserParam.toLowerCase()) {
            case "edge":
                browser = playwright.chromium().launch(launchOptions.setChannel("msedge"));
                break;
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            default:
                browser = playwright.chromium().launch(launchOptions);
                break;
        }
        System.out.println("Browser: " + browserParam);

        // ── Context & Page ───────────────────────────────────────────
        browserContext = DeviceConfig.createContext(browser, deviceType);
        page = browserContext.newPage();
        page.navigate(getBaseUrl());
        System.out.println("Setup done.");

        commonSteps = new CommonSteps(page, deviceType);
        currencySteps = new CurrencySteps(page, deviceType);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (page != null) {
            page.close();
        }
        if (browserContext != null) {
            browserContext.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        System.out.println("Teardown done.");
    }
}