package ge.tbc.testautomation.api.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRate {

    @JsonProperty("iso")
    private String iso;

    @JsonProperty("name")
    private String name;

    @JsonProperty("buyRate")
    private Double buyRate;

    @JsonProperty("sellRate")
    private Double sellRate;

    @JsonProperty("officialCourse")
    private Double officialCourse;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("diff")
    private Double diff;
}
