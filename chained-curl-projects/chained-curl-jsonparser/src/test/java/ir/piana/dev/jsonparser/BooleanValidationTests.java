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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Import(TestConfig.class)
class BooleanValidationTests {

	@Autowired
	private JsonParser jsonParser;

	@Autowired
	private ValidatorProvider validatorProvider;

	@Test
	void nationalCodeValidation(@Value("classpath:test.json") Resource resource) throws IOException {
		JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
				"""
                        [0].parent.man string
                        [0].parent.is-legal boolean itonn#$[0].parent.man$
                        """, true);
		assertThat(jsonTarget.asString("[0].parent.man")).isNull();
	}
}
