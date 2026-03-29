package ge.tbc.testautomation.ui;

import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import static ge.tbc.testautomation.data.Constants.*;

public class Executor {
    @Factory
    @Parameters({BROWSER, DeviceType})
    public Object[] createTests(
            @Optional(CHROME) String browser,
            @Optional(DESKTOP) String deviceType
    ) {

        return new Object[]{
                new InputBoundaryTest(browser, deviceType),
        };
    }
}
