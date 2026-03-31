package ge.tbc.testautomation.api.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardRate {

    @JsonProperty("iso")
    private String iso;

    @JsonProperty("forwardRates")
    private List<ForwardRateDetail> forwardRates;
}