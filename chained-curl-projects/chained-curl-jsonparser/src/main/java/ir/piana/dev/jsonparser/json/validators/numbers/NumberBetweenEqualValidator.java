package ir.piana.dev.jsonparser.json.validators.numbers;

import ir.piana.dev.jsonparser.json.validators.Validator;
import ir.piana.dev.jsonparser.util.NumberParser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NumberBetweenEqualValidator implements Validator<Number> {
    @Override
    public String fromValueType() {
        return "number";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("number-between-equal", "nbe");
    }

    @Override
    public boolean validate(Number value, String... options) {
        if(value instanceof Short || value instanceof Integer || value instanceof Long)
            return value.longValue() >= NumberParser.parseUp(value, options[0]).longValue() &&
                    value.longValue() <= NumberParser.parseUp(value, options[1]).longValue();
        else
            return value.doubleValue() >= NumberParser.parseUp(value, options[0]).doubleValue() &&
                    value.doubleValue() <= NumberParser.parseUp(value, options[1]).doubleValue();
    }
}
