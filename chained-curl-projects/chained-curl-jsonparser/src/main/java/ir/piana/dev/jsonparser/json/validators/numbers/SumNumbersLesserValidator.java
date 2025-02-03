package ir.piana.dev.jsonparser.json.validators.numbers;

import ir.piana.dev.jsonparser.json.validators.Validator;
import ir.piana.dev.jsonparser.util.NumberParser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SumNumbersLesserValidator implements Validator<Number> {
    @Override
    public String fromValueType() {
        return "number";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("sum-numbers-lesser", "snl");
    }

    @Override
    public boolean validate(Number value, String... options) {
        if (value instanceof Short || value instanceof Integer || value instanceof Long) {
            long sum = value.longValue();
            for (int i = 0; i < options.length - 1; i++) {
                sum += NumberParser.parseUp(value, options[0]).longValue();
            }
            return sum < NumberParser.parseUp(value, options[options.length - 1]).longValue();
        } else {
            double sum = value.longValue();
            for (int i = 0; i < options.length - 1; i++) {
                sum += NumberParser.parseUp(value, options[0]).doubleValue();
            }
            return sum < NumberParser.parseUp(value, options[options.length - 1]).doubleValue();
        }

    }
}
