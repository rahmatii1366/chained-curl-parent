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
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Import(TestConfig.class)
class StringValidationTests {

	@Autowired
	private JsonParser jsonParser;

	@Autowired
	private ValidatorProvider validatorProvider;

	@Test
	void nationalCodeValidation(@Value("classpath:test.json") Resource resource) throws IOException {
		try {
			JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
					"""
							[0].parent.national-code string nn#iftt lb#2#3
							[0].parent.national-code string nn#iftt pnc
							[0].parent.name string nn#iftt eq#hasan
							""", true);
			assertThat(jsonTarget.asString("[0].parent.national-code")).isEqualTo("0082315310");
		} catch (JsonValidationRuntimeException e) {
			System.out.println(e.getMessage());
		}

	}
}
