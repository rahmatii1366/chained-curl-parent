package ir.piana.dev.chainedcurl.core.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChainedCurlDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("show")
    private Map<String, Map<String, String>> showMap;
    @JsonProperty("provide")
    private ChainedCurlRequestProvideDto provideDto;
    @JsonProperty("chain")
    private Map<String, ChainedCurlRequestDto> chainMap;
}
