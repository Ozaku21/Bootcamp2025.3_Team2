package ge.tbc.testautomation.api;
import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Currency Module")
@Feature("Currency Converter")
@Test(groups={"Validate Successful Forward Rates Retrieval - SCRUM-T9", "ApiTests"})
public class ForwardRatesApiCallTest extends ApiBaseTest {

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
