package ir.piana.dev.jsonparser.json.converter;

import ir.piana.dev.jsonparser.util.NumberParser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StringToNumberConverter implements Converter<String, Number> {
    @Override
    public String fromValueType() {
        return "string";
    }

    @Override
    public String toValueType() {
        return "number";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("number");
    }

    @Override
    public Number convert(String value, String... options) {
        return NumberParser.parseUp(value);
    }
}
