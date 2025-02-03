package ir.piana.dev.jsonparser.json.converter;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NumberToStringConverter implements Converter<Number, String> {
    @Override
    public String fromValueType() {
        return "number";
    }

    @Override
    public String toValueType() {
        return "string";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("string");
    }

    @Override
    public String convert(Number value, String... options) {
        return value.toString();
    }
}
