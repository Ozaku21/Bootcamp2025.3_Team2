package ge.tbc.testautomation.api;

import ge.tbc.testautomation.BaseTest;
import ge.tbc.testautomation.steps.apisteps.CommercialListApiCallSteps;
import ge.tbc.testautomation.steps.apisteps.ExchangeRateApiCallSteps;
import ge.tbc.testautomation.steps.apisteps.ForwardRatesApiCallSteps;
import org.testng.annotations.BeforeClass;

public class ApiBaseTest extends BaseTest {
    @BeforeClass(alwaysRun = true)
    @Override
    public void setUp() {
        commercialListApiCallSteps = new CommercialListApiCallSteps();
        exchangeRateApiCallSteps = new ExchangeRateApiCallSteps();
        apiSteps = new ForwardRatesApiCallSteps();
    }
}