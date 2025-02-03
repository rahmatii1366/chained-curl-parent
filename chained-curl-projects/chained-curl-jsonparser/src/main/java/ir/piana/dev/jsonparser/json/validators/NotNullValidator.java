package ir.piana.dev.jsonparser.json.validators;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class NotNullValidator implements Validator<Object> {
    @Override
    public String getBaseMessageKey() {
        return Validator.super.getBaseMessageKey();
    }

    @Override
    public String fromValueType() {
        return "*";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("not-null", "nn");
    }

    @Override
    public boolean validate(Object value, String... options) {
        return Objects.nonNull(value);
    }
}
