package ir.piana.dev.jsonparser.json.validators.strings;

import ir.piana.dev.jsonparser.json.validators.Validator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StartWithValidator implements Validator<String> {
    @Override
    public String fromValueType() {
        return "string";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("start-with", "sw");
    }

    @Override
    public boolean validate(String value, String... options) {
        for (String opt : options) {
            if(value.startsWith(opt))
                return true;
        }
        return false;
    }
}
