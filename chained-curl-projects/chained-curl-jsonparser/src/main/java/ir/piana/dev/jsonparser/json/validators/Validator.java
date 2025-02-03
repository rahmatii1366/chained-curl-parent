package ir.piana.dev.jsonparser.json.validators;

import java.util.List;

public interface Validator<V> {
    default String getBaseMessageKey() {
        return "validationFailed";
    }

    String fromValueType();
    List<String> commandNames();

    default String originalName() {
        return commandNames().get(0);
    }

    boolean validate(V value, String... options);
}
