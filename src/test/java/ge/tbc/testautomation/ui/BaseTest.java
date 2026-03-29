package ge.tbc.testautomation.ui;

import com.microsoft.playwright.*;
import ge.tbc.testautomation.steps.CommonSteps;
import ge.tbc.testautomation.steps.ConventorSteps;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.List;

import static ge.tbc.testautomation.data.Constants.*;
import static ge.tbc.testautomation.data.Constants.BASE_URI;

public class BaseTest {

    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    CommonSteps commonSteps;
    ConventorSteps conventorSteps;
    private final String browserName;
    private final String deviceType;

    protected BaseTest(String browserName, String deviceType) {
        this.browserName = browserName;
        this.deviceType = deviceType;
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        start(browserName, deviceType);
    }
    protected void start(String browserName, String deviceType) {
        playwright = Playwright.create();

        boolean isCI = System.getenv("CI") != null;

        BrowserType browserType = switch (browserName.toLowerCase()) {
            case CHROMIUM , CHROME  -> playwright.chromium();
            case FIREFOX -> playwright.firefox();
            case WEBKIT , SAFARI -> playwright.webkit();
            default -> throw new IllegalArgumentException(UNSUPPORTED + browserName);
        };

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions(); // ✅ added

        if (isCI) {
            launchOptions.setHeadless(true);
            launchOptions.setArgs(List.of(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu"
            ));
        } else {
            launchOptions.setHeadless(false);
        }

        browser = browserType.launch(launchOptions);

        Browser.NewContextOptions options =
                new Browser.NewContextOptions();

        if (MOBILE.equalsIgnoreCase(deviceType)) {
            options.setViewportSize(430, 932)
                    .setIsMobile(true)
                    .setHasTouch(true)
                    .setDeviceScaleFactor(3);
        } else {
            options.setViewportSize(1920, 1080);
        }

        context = browser.newContext(options);
        page = context.newPage();

        commonSteps = new CommonSteps(page, deviceType);
        conventorSteps = new ConventorSteps(page);

        page.navigate(BASE_URI);
        commonSteps.AcceptCookiesIfPresent();
    }
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}