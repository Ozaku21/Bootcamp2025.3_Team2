package ge.tbc.testautomation.data.models.responses;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.tbc.testautomation.data.models.deserializers.OffsetDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateResponse {

    @JsonProperty("iso1")
    private String isoFirst;

    @JsonProperty("iso2")
    private String isoSecond;

    @JsonProperty("buyRate")
    private double buyRate;

    @JsonProperty("sellRate")
    private double sellRate;

    @JsonProperty("conversionType")
    private int conversionType;

    @JsonProperty("currencyWeight")
    private int currencyWeight;

    @JsonProperty("updateDate")
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime updateDate;
}
