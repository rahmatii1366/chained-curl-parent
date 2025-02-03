package ir.piana.dev.chainedcurl.core.curl;

import java.util.*;
import java.util.stream.Collectors;

public class CurlHeaderBuilder {
    private Map<String, List<String>> headerMap;

    private CurlHeaderBuilder(Map<String, List<String>> headerMap) {
        this.headerMap = headerMap;
    }

    public static CurlHeaderBuilder builder() {
        return new CurlHeaderBuilder(new LinkedHashMap<>());
    }

    public void add(String key, String value) {
        if (!headerMap.containsKey(key))
            headerMap.put(key, new ArrayList<>());
        headerMap.get(key).add(value);
    }

    public CurlHeaders build() {
        LinkedHashMap<String, List<String>> collect = headerMap.entrySet().stream()
                .<Map.Entry<String, List<String>>>map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(), Collections.unmodifiableList(entry.getValue())))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> {
                            throw new RuntimeException();
                        }, LinkedHashMap::new)
                );

        return CurlHeaders.builder()
                .headers(collect)
                .build();
    }
}
