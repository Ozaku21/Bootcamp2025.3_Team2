package ge.tbc.testautomation.steps.apisteps;

import ge.tbc.testautomation.api.client.BaseApi;
import ge.tbc.testautomation.data.models.responses.CommercialListResponse;
import ge.tbc.testautomation.data.models.responses.CurrencyRate;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;

public class CommercialListApiCallSteps extends BaseApi {

    private Response response;
    private CommercialListResponse commercialListResponseMapper;

    @Step("Send GET request to get commercial list")
    public CommercialListApiCallSteps getCommercialList(){
        this.response =given()
                .filter(new AllureRestAssured())
                .spec(REQ_EXCHANGE_RATES_COMMERCIAL_LIST)
                .queryParam("locale", "ka-GE")
//                .log().all()
                .when()
                .get()
//                .prettyPeek()
        ;

        this.commercialListResponseMapper = response.as(CommercialListResponse.class);

        return this;
    }

    @Step("validate status code")
    public CommercialListApiCallSteps validateStatusCode(int expectedStatus) {
        Assert.assertEquals(response.statusCode(), expectedStatus);
        return this;
    }

    @Step("validate rates list is not empty")
    public CommercialListApiCallSteps validateRatesNotEmpty() {
        Assert.assertNotNull(commercialListResponseMapper.getRates());
        Assert.assertFalse(commercialListResponseMapper.getRates().isEmpty());
        return this;
    }

    @Step("validate each currency rate detail")
    public CommercialListApiCallSteps validateRateDetails() {
        for (CurrencyRate rate : commercialListResponseMapper.getRates()) {

            Assert.assertNotNull(rate.getIso());
            Assert.assertFalse(rate.getIso().isBlank());

            Assert.assertNotNull(rate.getName());
            Assert.assertFalse(rate.getName().isBlank());

            Assert.assertNotNull(rate.getBuyRate());
            Assert.assertNotNull(rate.getSellRate());
            Assert.assertNotNull(rate.getOfficialCourse());

            Assert.assertTrue(rate.getBuyRate() > 0);
            Assert.assertTrue(rate.getSellRate() > 0);
            Assert.assertTrue(rate.getOfficialCourse() > 0);

            Assert.assertTrue(rate.getSellRate() > rate.getBuyRate());

            Assert.assertNotNull(rate.getWeight());
            Assert.assertTrue(rate.getWeight() >= 1);

            Assert.assertNotNull(rate.getDiff());
        }
        return this;
    }

    @Step("validate official course is between buy and sell rate")
    public CommercialListApiCallSteps validateOfficialCourseBetweenRates() {
        for (CurrencyRate rate : commercialListResponseMapper.getRates()) {
            Assert.assertTrue(
                    rate.getOfficialCourse() >= rate.getBuyRate() &&
                            rate.getOfficialCourse() <= rate.getSellRate());
        }
        return this;
    }

    @Step("validate that the response contains specific currency name")
    public CommercialListApiCallSteps validateResponseContains(String currencyName) {
        Assert.assertTrue(
                commercialListResponseMapper.getRates()
                        .stream()
                        .anyMatch(rate -> currencyName.equals(rate.getName()))
        );
        return this;
    }

    @Step("validate update date time")
    public CommercialListApiCallSteps validateUpdateDateTime() {
        Assert.assertNotNull(commercialListResponseMapper.getUpdateDate(),
                "Update date time should not be null");
        Assert.assertTrue(
                commercialListResponseMapper.getUpdateDate().isBefore(OffsetDateTime.now()),
                "Update date time should be in the past, but was: "
                        + commercialListResponseMapper.getUpdateDate());
        return this;
    }
}
