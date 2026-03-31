package ge.tbc.testautomation.api.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardRateDetail {

    @JsonProperty("iso1")
    private String isoFirst;

    @JsonProperty("iso2")
    private String isoSecond;

    @JsonProperty("period")
    private String period;

    @JsonProperty("day")
    private int day;

    @JsonProperty("bidForwardPoint")
    private double bidForwardPoint;

    @JsonProperty("bidForwardInterest")
    private double bidForwardInterest;

    @JsonProperty("bidForwardRate")
    private double bidForwardRate;

    @JsonProperty("askForwardPoint")
    private double askForwardPoint;

    @JsonProperty("askForwardInterest")
    private double askForwardInterest;

    @JsonProperty("askForwardRate")
    private double askForwardRate;
}