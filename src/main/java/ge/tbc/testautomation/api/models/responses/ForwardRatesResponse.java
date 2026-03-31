package ge.tbc.testautomation.api.models.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.tbc.testautomation.api.models.deserializers.OffsetDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardRatesResponse {

    @JsonProperty("rates")
    private List<ForwardRate> rates;

    @JsonProperty("updateDate")
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime updateDate;
}
