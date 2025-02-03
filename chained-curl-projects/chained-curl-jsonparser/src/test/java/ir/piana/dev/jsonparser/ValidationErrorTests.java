package ir.piana.dev.jsonparser;

import ir.piana.dev.TestConfig;
import ir.piana.dev.jsonparser.json.JsonParser;
import ir.piana.dev.jsonparser.json.JsonTarget;
import ir.piana.dev.jsonparser.json.validators.JsonValidationRuntimeException;
import ir.piana.dev.jsonparser.json.validators.ValidatorProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Import(TestConfig.class)
class ValidationErrorTests {

    @Autowired
    private JsonParser jsonParser;

    @Autowired
    private ValidatorProvider validatorProvider;

    @Autowired
    MessageSource messageSource;

    @Test
    void nationalCodeValidation(@Value("classpath:error.json") Resource resource) throws IOException {
        try {
            JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
                    """
                            [0].parent.national-code string pnc
                            """, true);
        } catch (Throwable ex) {
            if (ex instanceof JsonValidationRuntimeException e) {
                String message = messageSource.getMessage(
                        e.getMessage(),
                        null,
                        Locale.forLanguageTag("fa"));
                assertThat(message).isEqualTo("یک خطا");
                assertThat(e.getMessage()).isEqualTo(
                        "$[0].parent.national-code.persian-national-code");
            }
        }
    }

    @Test
    void nationalCodeValidation2(@Value("classpath:error.json") Resource resource) throws IOException {
        JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
                """
                        [0].parent.national-code string pnc
                        """, false);
        System.out.println();
    }
}
