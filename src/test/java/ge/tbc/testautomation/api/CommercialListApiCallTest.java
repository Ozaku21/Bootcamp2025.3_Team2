package ge.tbc.testautomation.api;
import io.qameta.allure.*;
import ge.tbc.testautomation.steps.apisteps.CommercialListApiCallSteps;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.CURRENCY_NAME;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Commercial Exchange Rates - SCRUM-T6", "ApiTests"})
public class CommercialListApiCallTest {

    private CommercialListApiCallSteps commercialListApiCallSteps = new CommercialListApiCallSteps();

    @Test(description = "validate successful commercial list api call")
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
