package ir.piana.dev.jsonparser.json.validators.strings;


import ir.piana.dev.jsonparser.json.validators.Validator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class PersianNationalCodeValidator implements Validator<String> {
    public static final String NATIONAL_CODE_PATTERN = "^(\\d{10})$";

    @Override
    public String fromValueType() {
        return "string";
    }

    @Override
    public List<String> commandNames() {
        return Arrays.asList("persian-national-code", "pnc");
    }

    @Override
    public boolean validate(String nationalCode, String... options) {
        if (nationalCode.matches(NATIONAL_CODE_PATTERN)) {
            int check = Integer.valueOf(
                    nationalCode.substring(nationalCode.length() - 1), nationalCode.length());
            int sum = IntStream.range(0, 9)
                    .map(n -> Integer.parseInt(nationalCode.substring(n, n + 1)) * (10 - n))
                    .sum() % 11;
            return sum < 2 && check == sum || sum >= 2 && check + sum == 11;
        }
        return false;
    }
}
