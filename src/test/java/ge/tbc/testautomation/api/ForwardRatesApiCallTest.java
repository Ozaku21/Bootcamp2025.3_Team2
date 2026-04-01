package ge.tbc.testautomation.api;

import ge.tbc.testautomation.steps.apisteps.ForwardRatesApiCallSteps;
import org.testng.annotations.Test;

@Test(groups={"[Successful Api call of forward rates]", "ApiTests"})
public class ForwardRatesApiCallTest {

    private ForwardRatesApiCallSteps apiSteps = new ForwardRatesApiCallSteps();

    @Test(description = "validate successful commercial list api call")
    public void forwardRatesApiCall(){
        apiSteps
                .getForwardCurrencyRates()
                .validateStatusCode(200)
                .validateForwardRatesNotEmpty()
                .validateForwardRateGroups()
                .validateForwardRateDetails()
                .validateForwardRatesUpdateDate();
    }
}
