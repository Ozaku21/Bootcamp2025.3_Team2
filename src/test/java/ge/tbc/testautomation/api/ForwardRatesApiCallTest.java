package ge.tbc.testautomation.api;
import ge.tbc.testautomation.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Successful Forward Rates Retrieval - SCRUM-T9", "ApiTests"})
public class ForwardRatesApiCallTest extends BaseTest {

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
