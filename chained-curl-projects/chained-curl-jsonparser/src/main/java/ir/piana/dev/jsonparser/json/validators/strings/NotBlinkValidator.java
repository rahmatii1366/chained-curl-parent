package ir.piana.dev.jsonparser.json.validators.strings;

import ir.piana.dev.jsonparser.json.validators.Validator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class NotBlinkValidator implements Validator<String> {
    @Override
    public String fromValueType() {
        return "string";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("not-blink", "nb");
    }

    @Override
    public boolean validate(String value, String... options) {
        return (Objects.nonNull(value) && !value.isBlank());
    }
}
