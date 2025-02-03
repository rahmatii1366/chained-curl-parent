package ir.piana.dev.chainedcurl.core.service;

import ir.piana.dev.chainedcurl.core.curl.CurlHeaders;
import ir.piana.dev.jsonparser.json.JsonTarget;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChainedCurlStepResponse {
    private String curlRequest;
    private int status;
    private CurlHeaders curlHeaders;
    private String bodyAsString;
    private JsonTarget jsonBody;
    private Map<String, String> extracted;
    private Map<String, Map<String, String>> showControls;
}
