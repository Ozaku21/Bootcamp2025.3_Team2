package ge.tbc.testautomation.api;

import ge.tbc.testautomation.steps.apisteps.ExchangeRateApiCallSteps;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.EUR;
import static ge.tbc.testautomation.data.Constants.GEL;

@Test(groups={"[Unsuccessful Api call of exchange rates using same params]", "ApiTests"})
public class ExchangeRateApiCallRejectionTest {

    private ExchangeRateApiCallSteps exchangeRateApiCallSteps = new ExchangeRateApiCallSteps();

    @Test(description = "validate rejected call for exchange rates with same params")
    public void exchangeRateApiCall(){
        exchangeRateApiCallSteps
                .getExchangeRate(GEL, GEL)
                .validateStatusCode(200)
                .validateHtmlRejection();
    }//TODO: ask if this is a bug or a feature ;D
}