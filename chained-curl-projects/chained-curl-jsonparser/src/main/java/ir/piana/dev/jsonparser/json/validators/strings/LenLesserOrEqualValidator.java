package ir.piana.dev.jsonparser.json.validators.strings;

import ir.piana.dev.jsonparser.json.validators.Validator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LenLesserOrEqualValidator implements Validator<String> {
    @Override
    public String fromValueType() {
        return "string";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("length-lesser-equal", "lle");
    }

    @Override
    public boolean validate(String value, String... options) {
        return (value.length() <= Integer.valueOf(options[0]));
    }
}
