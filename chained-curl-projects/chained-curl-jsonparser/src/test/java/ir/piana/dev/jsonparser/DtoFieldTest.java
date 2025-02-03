package ir.piana.dev.jsonparser;

import ir.piana.dev.TestConfig;
import ir.piana.dev.jsonparser.json.JsonParser;
import ir.piana.dev.jsonparser.json.JsonTarget;
import ir.piana.dev.jsonparser.json.validators.ValidatorProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Import(TestConfig.class)
public class DtoFieldTest {
    @Autowired
    private JsonParser jsonParser;

    @Autowired
    private ValidatorProvider validatorProvider;


    @Test
    void jsonTargetTest(
            @Value("classpath:api-dto/merchant-register-1.json") Resource json,
            @Value("classpath:dto-fields/merchant-register.txt") Resource fieldResource
    ) throws IOException {
        JsonTarget jsonTarget = jsonParser.fromBytes(json.getInputStream().readAllBytes(),
                fieldResource.getContentAsString(StandardCharsets.US_ASCII), true);
        assertThat(jsonTarget.asInteger("merchantAdditionalDataDtos[0].accountNumber")).isNotNull();
    }
}
