package ir.piana.dev.chainedcurl.core.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChainedCurlRequestDto {
    @JsonProperty("show")
    private Map<String, Map<String, String>> showMap;
    @JsonProperty("provide")
    ChainedCurlRequestProvideDto provideDto;
    @JsonProperty("curl")
    private List<String> curl;
    @JsonProperty("continueConditions")
    private List<String> continueConditions;
    @JsonProperty("startConditions")
    private List<String> startConditions;
    @JsonProperty("extract")
    private Map<String, String> extractMap;
}
