package ge.tbc.testautomation;

import ge.tbc.testautomation.api.CommercialListApiCallTest;
import ge.tbc.testautomation.api.ExchangeRateApiCallRejectionTest;
import ge.tbc.testautomation.api.ExchangeRateApiCallTest;
import ge.tbc.testautomation.api.ForwardRatesApiCallTest;
import ge.tbc.testautomation.ui.*;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import static ge.tbc.testautomation.data.Constants.*;

public class Executor {
    @Factory
    @Parameters({BROWSER, DEVICETYPE})
    public Object[] createTests(
            @Optional(CHROME) String browser,
            @Optional(DESKTOP) String deviceType
    ) {

        return new Object[]{
                new InvalidInputTest(browser, deviceType),
                new ConversionValidationTest(browser, deviceType),
                new ConversionSwapTest(browser, deviceType),
                new InputBoundaryTest(browser, deviceType),
                new NonNumericInputTest(browser, deviceType),
                new CommercialListApiCallTest(),
                new ForwardRatesApiCallTest(),
                new ExchangeRateApiCallTest(),
                new ExchangeRateApiCallRejectionTest()
        };
    }
}
