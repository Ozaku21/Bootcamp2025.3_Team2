package ge.tbc.testautomation.util;

import io.qameta.allure.Attachment;

public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    @Attachment(value = "Failure Screenshot", type = "image/png")
    public static byte[] attachFailureScreenshot(byte[] screenshotBytes) {
        return screenshotBytes;
    }
}