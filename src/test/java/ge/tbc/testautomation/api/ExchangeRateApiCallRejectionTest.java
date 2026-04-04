package ge.tbc.testautomation.api;
import io.qameta.allure.*;
import ge.tbc.testautomation.steps.apisteps.ExchangeRateApiCallSteps;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.EUR;
import static ge.tbc.testautomation.data.Constants.GEL;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Exchange Rate Rejection for Identical Parameters - SCRUM-T7", "ApiTests"})
public class ExchangeRateApiCallRejectionTest {

    private ExchangeRateApiCallSteps exchangeRateApiCallSteps = new ExchangeRateApiCallSteps();

    @Test(description = "validate rejected call for exchange rates with same params")
    public void exchangeRateApiCall(){
        exchangeRateApiCallSteps
                .getExchangeRate(GEL, GEL)
                .validateStatusCode(200)
                .validateHtmlRejection();
    }
}