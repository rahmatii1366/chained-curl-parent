package ir.piana.dev.chainedcurl.core.curl;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CurlResponse {
    private int status;
    private CurlHeaders curlHeaders;
    private String bodyAsString;
}
