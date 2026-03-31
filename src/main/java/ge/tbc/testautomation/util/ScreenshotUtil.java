package ge.tbc.testautomation.util;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    public static void attachFailureScreenshot(Page page, String testName, String browserName) throws Exception {
        Path screenshotDir = Paths.get("test-output", "screenshots");
        Files.createDirectories(screenshotDir);

        Path screenshotPath = screenshotDir.resolve(testName + "-" + browserName + ".png");

        byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));

        Allure.addAttachment(
                "Failure Screenshot - " + testName + " - " + browserName,
                "image/png",
                new ByteArrayInputStream(screenshotBytes),
                ".png"
        );
    }
}