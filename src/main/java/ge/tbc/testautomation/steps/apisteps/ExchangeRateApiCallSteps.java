package ge.tbc.testautomation.steps.apisteps;

import ge.tbc.testautomation.api.client.BaseApi;
import ge.tbc.testautomation.data.models.responses.ExchangeRateResponse;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

public class ExchangeRateApiCallSteps extends BaseApi {

    private Response response;
    private ExchangeRateResponse exchangeRateResponseMapper;

    private String incomingCurrency;
    private String outcomeCurrency;

    @Step("Send GET request to get exchange rate ")
    public ExchangeRateApiCallSteps getExchangeRate(String incomingCurrency, String outcomeCurrency){
        this.response =given()
                .filter(new AllureRestAssured())
                .spec(REQ_EXCHANGE_RATES_EXCHANGE_RATE)
                .queryParam("Iso1", incomingCurrency)
                .queryParam("Iso2", outcomeCurrency)
                .queryParam("locale", "ka-GE")
//                .log().all()
                .when()
                .get()
//                .prettyPeek()
        ;

        this.incomingCurrency = incomingCurrency;
        this.outcomeCurrency = outcomeCurrency;

        if (response.getContentType().contains("application/json")) {
            this.exchangeRateResponseMapper = response.as(ExchangeRateResponse.class);
        } else {
            this.exchangeRateResponseMapper = null;
        }

        return this;
    }

    @Step("validate status code")
    public ExchangeRateApiCallSteps validateStatusCode(int expectedStatus) {
        Assert.assertEquals(response.statusCode(), expectedStatus);
        Assert.assertTrue(
                exchangeRateResponseMapper.getUpdateDate().isBefore(LocalDateTime.now().plusHours(5)),
                "Update date should be in the past");
        return this;
    }

    @Step("validate incoming currency")
    public ExchangeRateApiCallSteps validateIncomingCurrency(){
        Assert.assertEquals(exchangeRateResponseMapper.getIsoFirst(), this.incomingCurrency);
        return this;
    }

    @Step("validate outcome  currency")
    public ExchangeRateApiCallSteps validateOutcomeCurrency(){
        Assert.assertEquals(exchangeRateResponseMapper.getIsoSecond(), this.outcomeCurrency);
        return this;
    }

    @Step("validate buy rate")
    public ExchangeRateApiCallSteps validateBuyRate() {
        Assert.assertTrue(exchangeRateResponseMapper.getBuyRate() > 0);
        return this;
    }

    @Step("validate sell rate")
    public ExchangeRateApiCallSteps validateSellRate() {
        Assert.assertTrue(exchangeRateResponseMapper.getSellRate() > 0);
        return this;
    }

    @Step("validate sell rate greater than buy rate")
    public ExchangeRateApiCallSteps validateSellRateGreaterThanBuyRate() {
        Assert.assertTrue(
                exchangeRateResponseMapper.getSellRate() > exchangeRateResponseMapper.getBuyRate(),
                "Sell rate should be greater than buy rate. Sell: "
                        + exchangeRateResponseMapper.getSellRate()
                        + ", Buy: " + exchangeRateResponseMapper.getBuyRate());
        return this;
    }

    @Step("validate conversion type")
    public ExchangeRateApiCallSteps validateConversionType() {
        Assert.assertNotNull(exchangeRateResponseMapper.getConversionType());
        return this;
    }

    @Step("validate currency weight")
    public ExchangeRateApiCallSteps validateCurrencyWeight() {
        Assert.assertNotNull(exchangeRateResponseMapper.getCurrencyWeight());
        return this;
    }

    @Step("validate update date")
    public ExchangeRateApiCallSteps validateUpdateDate() {
        Assert.assertNotNull(exchangeRateResponseMapper.getUpdateDate(),
                "Update date should not be null");
        Assert.assertTrue(
                exchangeRateResponseMapper.getUpdateDate().isBefore(LocalDateTime.now()),
                "Update date should be in the past");
        return this;
    }


    @Step("validate HTML rejection message")
    public ExchangeRateApiCallSteps validateHtmlRejection() {
        String body = response.getBody().asString();
        Assert.assertTrue(response.getContentType().contains("text/html"));
        Assert.assertTrue(body.contains("The requested URL was rejected"));
        return this;
    }
}
