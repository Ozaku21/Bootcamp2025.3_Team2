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

        BrowserType browserType = switch (browserName.toLowerCase()) {
            case CHROMIUM , CHROME  -> playwright.chromium();
            case FIREFOX -> playwright.firefox();
            case WEBKIT , SAFARI -> playwright.webkit();
            default -> throw new IllegalArgumentException(UNSUPPORTED + browserName);
        };

        browser = browserType.launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(1000)
        );
        Browser.NewContextOptions options =
                new Browser.NewContextOptions()
                        .setGeolocation(41.7151, 44.8271)
                        .setPermissions(List.of(GEO_LOCATION));
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

        commonSteps = new CommonSteps(page,deviceType);
        conventorSteps = new ConventorSteps(page);

        page.navigate(BASE_URI);
        commonSteps
                .AcceptCookiesIfPresent();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}