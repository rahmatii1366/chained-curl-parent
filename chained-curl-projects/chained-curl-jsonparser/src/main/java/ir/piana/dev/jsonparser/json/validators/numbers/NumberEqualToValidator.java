package ir.piana.dev.jsonparser.json.validators.numbers;

import ir.piana.dev.jsonparser.json.validators.Validator;
import ir.piana.dev.jsonparser.util.NumberParser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NumberEqualToValidator implements Validator<Number> {
    @Override
    public String fromValueType() {
        return "number";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("equal-to", "eq" );
    }

    @Override
    public boolean validate(Number value, String... options) {
        return NumberParser.equalTo(value, options[0]);
    }
}
