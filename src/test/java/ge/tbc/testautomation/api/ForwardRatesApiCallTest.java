package ge.tbc.testautomation.api;
import io.qameta.allure.*;
import ge.tbc.testautomation.steps.apisteps.ForwardRatesApiCallSteps;
import org.testng.annotations.Test;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Successful Forward Rates Retrieval - SCRUM-T9", "ApiTests"})
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
