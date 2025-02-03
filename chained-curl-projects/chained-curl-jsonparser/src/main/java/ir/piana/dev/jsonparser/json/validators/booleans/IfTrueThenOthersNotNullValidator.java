package ir.piana.dev.jsonparser.json.validators.booleans;

import ir.piana.dev.jsonparser.json.validators.Validator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class IfTrueThenOthersNotNullValidator implements Validator<Boolean> {
    @Override
    public String fromValueType() {
        return "boolean";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("if-true-then-others-not-null", "itonn");
    }

    @Override
    public boolean validate(Boolean value, String... options) {
        return !value || Arrays.stream(options)
                    .noneMatch(s -> s.equalsIgnoreCase("null"));
    }
}
