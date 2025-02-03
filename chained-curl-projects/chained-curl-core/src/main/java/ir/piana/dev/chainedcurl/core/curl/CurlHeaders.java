package ir.piana.dev.chainedcurl.core.curl;

import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CurlHeaders {
    private Map<String, List<String>> headers;

    public List<String> getHeader(String headerName) {
        return headers.getOrDefault(headerName, Collections.emptyList());
    }

    public Optional<String> getFirstHeader(String headerName) {
        return Optional.ofNullable(headers.containsKey(headerName) ? headers.get(headerName).getFirst() : null);
    }
}
