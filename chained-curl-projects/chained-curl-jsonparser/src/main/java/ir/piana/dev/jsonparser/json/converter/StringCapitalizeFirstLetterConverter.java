package ir.piana.dev.jsonparser.json.converter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class StringCapitalizeFirstLetterConverter implements Converter<String, String> {
    @Override
    public String fromValueType() {
        return "string";
    }

    @Override
    public String toValueType() {
        return "string";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("capitalize-first-letter");
    }

    @Override
    public String convert(String value, String... options) {
        return StringUtils.capitalize(value.toLowerCase());
    }
}
