package ge.tbc.testautomation.steps.apisteps;

import ge.tbc.testautomation.api.client.BaseApi;
import ge.tbc.testautomation.api.models.responses.ForwardRate;
import ge.tbc.testautomation.api.models.responses.ForwardRateDetail;
import ge.tbc.testautomation.api.models.responses.ForwardRatesResponse;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;

public class ForwardRatesApiCallSteps extends BaseApi {

    private Response response;
    private ForwardRatesResponse forwardRatesResponseMapper;

    @Step("Send GET request to get forward currency rates")
    public ForwardRatesApiCallSteps getForwardCurrencyRates(){

        this.response = given()
                .filter(new AllureRestAssured())
                .spec(REQ_EXCHANGE_RATES_FORWARD_RATES)
                .queryParam("locale", "ka-GE")
//                .log().all()
                .when()
                .get()
                .prettyPeek()
                ;

        this.forwardRatesResponseMapper = response.as(ForwardRatesResponse.class);

        return this;
    }

    @Step("validate status code")
    public ForwardRatesApiCallSteps validateStatusCode(int expectedStatus) {
        Assert.assertEquals(response.statusCode(), expectedStatus);
        return this;
    }

    @Step("validate forward rates list is not empty")
    public ForwardRatesApiCallSteps validateForwardRatesNotEmpty() {
        Assert.assertNotNull(forwardRatesResponseMapper.getRates());
        Assert.assertFalse(forwardRatesResponseMapper.getRates().isEmpty());
        return this;
    }

    @Step("validate each currency group has iso and forward rates")
    public ForwardRatesApiCallSteps validateForwardRateGroups() {
        for (ForwardRate group : forwardRatesResponseMapper.getRates()) {
            Assert.assertNotNull(group.getIso());
            Assert.assertFalse(group.getIso().isBlank());
            Assert.assertNotNull(group.getForwardRates());
            Assert.assertFalse(group.getForwardRates().isEmpty());
        }
        return this;
    }

    @Step("validate each forward rate entry fields")
    public ForwardRatesApiCallSteps validateForwardRateDetails() {
        for (ForwardRate group : forwardRatesResponseMapper.getRates()) {
            for (ForwardRateDetail detail : group.getForwardRates()) {

                Assert.assertNotNull(detail.getIsoFirst());
                Assert.assertNotNull(detail.getIsoSecond());
                Assert.assertNotNull(detail.getPeriod());
                Assert.assertFalse(detail.getPeriod().isBlank());

                Assert.assertNotNull(detail.getDay());
                Assert.assertTrue(detail.getDay() > 0);

                Assert.assertNotNull(detail.getBidForwardPoint());
                Assert.assertNotNull(detail.getBidForwardInterest());
                Assert.assertNotNull(detail.getBidForwardRate());
                Assert.assertNotNull(detail.getAskForwardPoint());
                Assert.assertNotNull(detail.getAskForwardInterest());
                Assert.assertNotNull(detail.getAskForwardRate());

                Assert.assertTrue(detail.getBidForwardRate() > 0);
                Assert.assertTrue(detail.getAskForwardRate() > 0);
                Assert.assertTrue(detail.getAskForwardRate() > detail.getBidForwardRate());

                Assert.assertEquals(detail.getIsoFirst(), group.getIso());
            }
        }
        return this;
    }

    @Step("validate forward rates update date")
    public ForwardRatesApiCallSteps validateForwardRatesUpdateDate() {
        Assert.assertNotNull(forwardRatesResponseMapper.getUpdateDate(),
                "Update date should not be null");
        Assert.assertTrue(
                forwardRatesResponseMapper.getUpdateDate().isBefore(OffsetDateTime.now()),
                "Update date should be in the past, but was: " + forwardRatesResponseMapper.getUpdateDate());
        return this;
    }
}
