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
public class ChainedCurlRequestProvideDto {
    private Map<String, String> supplier;
    private Map<String, String> fix;
    private Map<String, Map<String, String>> input;
}
