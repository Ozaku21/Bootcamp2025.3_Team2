package ge.tbc.testautomation.api.models.responses;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    @JsonProperty("updateDate")
    private LocalDateTime updateDate;
}
