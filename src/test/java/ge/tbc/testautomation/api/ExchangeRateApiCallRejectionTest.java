package ge.tbc.testautomation.api;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.EUR;
import static ge.tbc.testautomation.data.Constants.GEL;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Exchange Rate Rejection for Identical Parameters - SCRUM-T7", "ApiTests"})
public class ExchangeRateApiCallRejectionTest extends ApiBaseTest {

    @Story("Validate rejected exchange rate call with identical parameters")
    @Severity(SeverityLevel.NORMAL)
    public void exchangeRateApiCall(){
        exchangeRateApiCallSteps
                .getExchangeRate(GEL, GEL)
                .validateStatusCode(200)
                .validateHtmlRejection();
    }
}