package ir.piana.dev.jsonparser.json.converter;

import java.util.List;

public interface Converter<V, R> {
    default String getBaseMessageKey() {
        return "castFailed";
    }

    String fromValueType();
    String toValueType();
    List<String> commandNames();

    default String originalName() {
        return commandNames().get(0);
    }

    R convert(V value, String... options);
}
