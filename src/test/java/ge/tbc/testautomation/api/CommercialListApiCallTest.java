package ge.tbc.testautomation.api;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.CURRENCY_NAME;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Commercial Exchange Rates - SCRUM-T6", "ApiTests"})
public class CommercialListApiCallTest extends ApiBaseTest {

    @Story("Validate successful exchange rate API call")
    @Severity(SeverityLevel.CRITICAL)
    public void exchangeRateApiCall(){
        commercialListApiCallSteps
                .getCommercialList()
                .validateStatusCode(200)
                .validateRatesNotEmpty()
                .validateRateDetails()
                .validateOfficialCourseBetweenRates()
                .validateResponseContains(CURRENCY_NAME)
                .validateUpdateDateTime();
    }
}
