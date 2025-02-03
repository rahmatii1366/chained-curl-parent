package ir.piana.dev.jsonparser.json.validators.numbers;

import ir.piana.dev.jsonparser.json.validators.Validator;
import ir.piana.dev.jsonparser.util.NumberParser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LesserOrEqualValidator implements Validator<Number> {
    @Override
    public String fromValueType() {
        return "number";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("lesser-or-equal", "le" );
    }

    @Override
    public boolean validate(Number value, String... options) {
        return NumberParser.lesserOrEqual(value, options[0]);
    }
}
