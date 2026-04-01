package ge.tbc.testautomation.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static ge.tbc.testautomation.data.Constants.*;

public class BaseApi {

    private static final RestAssuredConfig CONFIG = RestAssuredConfig.config()
            .objectMapperConfig(new ObjectMapperConfig()
                    .jackson2ObjectMapperFactory((cls, charset) ->
                            new ObjectMapper()
                                    .registerModule(new JavaTimeModule())
                                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)));



    public static final RequestSpecification REQ_EXCHANGE_RATES_COMMERCIAL_LIST = new RequestSpecBuilder()
            .setBaseUri(TBC_BASE_URL)
            .setBasePath(TBC_COMMERCIAL_LIST_BASE_PATH)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();

    public static final RequestSpecification REQ_EXCHANGE_RATES_EXCHANGE_RATE = new RequestSpecBuilder()
            .setBaseUri(TBC_BASE_URL)
            .setBasePath(TBC_EXCHANGE_RATE_BASE_PATH)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .setConfig(CONFIG)
            .build();

    public static final RequestSpecification REQ_EXCHANGE_RATES_FORWARD_RATES = new RequestSpecBuilder()
            .setBaseUri(TBC_BASE_URL)
            .setBasePath(TBC_FORWARD_RATES_BASE_PATH)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();
}
