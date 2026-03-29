package ge.tbc.testautomation;

import com.microsoft.playwright.*;
import ge.tbc.testautomation.steps.CommonSteps;
import ge.tbc.testautomation.steps.CurrencySteps;
import ge.tbc.testautomation.util.DeviceType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static ge.tbc.testautomation.data.Constants.*;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected CommonSteps commonSteps;
    protected CurrencySteps currencySteps;

    private final String browserName;
    private final DeviceType deviceType;

    protected BaseTest(String browserName, DeviceType deviceType) {
        this.browserName = browserName.toLowerCase();
        this.deviceType  = deviceType;
    }

    protected BaseTest() {
        this(CHROMIUM, DeviceType.DESKTOP);
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        playwright = Playwright.create();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(false);

        BrowserType browserType = switch (browserName) {
            case CHROMIUM, CHROME, EDGE -> playwright.chromium();
            case FIREFOX          -> playwright.firefox();
            case WEBKIT, SAFARI   -> playwright.webkit();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserName);
        };

        if (EDGE.equals(browserName)) {
            launchOptions.setChannel("msedge");
        }

        browser = browserType.launch(launchOptions);
        context = browser.newContext(buildContextOptions());
        page    = context.newPage();

        commonSteps   = new CommonSteps(page, deviceType);
        currencySteps = new CurrencySteps(page, deviceType);

        page.navigate(URL_TBC);
        commonSteps.acceptCookiesIfPresent();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (page       != null) page.close();
        if (context    != null) context.close();
        if (browser    != null) browser.close();
        if (playwright != null) playwright.close();
    }

    private Browser.NewContextOptions buildContextOptions() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        return switch (deviceType) {
            case DESKTOP -> options
                    .setViewportSize(1920, 1080);
            case MOBILE -> options
                    .setViewportSize(430, 932)
                    .setIsMobile(true)
                    .setHasTouch(true)
                    .setDeviceScaleFactor(3);
        };
    }
}