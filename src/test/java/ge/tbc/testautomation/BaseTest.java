package ge.tbc.testautomation;

import com.microsoft.playwright.*;
import ge.tbc.testautomation.steps.CommonSteps;
import ge.tbc.testautomation.steps.ConvertorSteps;
import ge.tbc.testautomation.util.ScreenshotUtil;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import static ge.tbc.testautomation.data.Constants.*;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected CommonSteps commonSteps;
    protected ConvertorSteps convertorSteps;

    private final String browserName;
    private final String deviceType;

    protected BaseTest(String browserName, String deviceType) {
        this.browserName = browserName.toLowerCase();
        this.deviceType  = deviceType.toLowerCase();
    }

    public BaseTest() {
        this.browserName = System.getProperty("browser", CHROMIUM).toLowerCase();
        this.deviceType = System.getProperty("device", DESKTOP).toLowerCase();
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        playwright = Playwright.create();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true);

        BrowserType browserType = switch (browserName) {
            case CHROMIUM, CHROME, EDGE -> playwright.chromium();
            case FIREFOX                -> playwright.firefox();
            case WEBKIT, SAFARI         -> playwright.webkit();
            default -> throw new IllegalArgumentException(UNSUPPORTED + browserName);
        };

        if (EDGE.equals(browserName)) {
            launchOptions.setChannel("msedge");
        }

        browser        = browserType.launch(launchOptions);
        context        = browser.newContext(buildContextOptions());
        page           = context.newPage();
        commonSteps    = new CommonSteps(page);
        convertorSteps = new ConvertorSteps(page);

        page.navigate(BASE_URI);
        commonSteps.acceptCookiesIfPresent();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (page       != null) page.close();
        if (context    != null) context.close();
        if (browser    != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @AfterMethod(alwaysRun = true)
    public void captureScreenshotOnFailure(ITestResult result) throws Exception {
        if (!result.isSuccess() && page != null && !page.isClosed()) {
            byte[] screenshotBytes = page.screenshot(
                    new Page.ScreenshotOptions().setFullPage(true)
            );

            ScreenshotUtil.attachFailureScreenshot(screenshotBytes);
        }
    }

    private Browser.NewContextOptions buildContextOptions() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        return switch (deviceType) {
            case DESKTOP -> options.setViewportSize(1920, 1080);
            case MOBILE  -> options
                    .setViewportSize(430, 932)
                    .setIsMobile(true)
                    .setHasTouch(true)
                    .setDeviceScaleFactor(3);
            default -> throw new IllegalArgumentException(UNSUPPORTED + deviceType);
        };
    }
}