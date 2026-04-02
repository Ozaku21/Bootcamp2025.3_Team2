package ge.tbc.testautomation.api;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.*;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Successful Exchange Rate Calculation - SCRUM-T8", "ApiTests"})
public class ExchangeRateApiCallTest extends ApiBaseTest {

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
