package ge.tbc.testautomation.api;

import ge.tbc.testautomation.steps.apisteps.CommercialListApiCallSteps;
import ge.tbc.testautomation.steps.apisteps.ExchangeRateApiCallSteps;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.CURRENCY_NAME;

@Test(groups={"[Successful Api call of commercial list]", "ApiTests"})
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
