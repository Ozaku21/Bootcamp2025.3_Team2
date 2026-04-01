package ge.tbc.testautomation.api;

import ge.tbc.testautomation.steps.apisteps.ExchangeRateApiCallSteps;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.*;

@Test(groups={"[Successful Api call of exchange rates]", "ApiTests"})
public class ExchangeRateApiCallTest {

    private ExchangeRateApiCallSteps exchangeRateApiCallSteps = new ExchangeRateApiCallSteps();

    @Test(description = "validate successful exchange rates api call")
    public void exchangeRateApiCall(){
        exchangeRateApiCallSteps
                .getExchangeRate(EUR, GEL)
                .validateStatusCode(200)
                .validateIncomingCurrency()
                .validateOutcomeCurrency()
                .validateBuyRate()
                .validateSellRate()
                .validateSellRateGreaterThanBuyRate()
                .validateConversionType()
                .validateCurrencyWeight()
                .validateUpdateDate();
    }
}
