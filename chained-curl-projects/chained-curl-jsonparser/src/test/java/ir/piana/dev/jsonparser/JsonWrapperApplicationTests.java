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
class JsonWrapperApplicationTests {

	@Autowired
	private JsonParser jsonParser;

	@Autowired
	private ValidatorProvider validatorProvider;

	@Test
	void loadJson(@Value("classpath:test.json") Resource resource) throws IOException {
		JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
				"""
						[0].addresses[0].title string nn le#4 lg#3 lle#4 lb#2#4
						[0].addresses[0].plaque
						[0].addresses[1].title string nn le#4 lg#3 lle#4 lb#2#4 sw#w
						[0].addresses[1].plaque number snl#$[0].addresses[0].plaque$#20
						[0].family string nn le#3
						""", true);
		assertThat(jsonTarget.asInteger("[0].addresses[0].title2")).isNull();
		assertThat(jsonTarget.asString("[0].addresses[0].title")).isEqualTo("home");
		assertThat(jsonTarget.asString("[0].addresses[1].title")).isEqualTo("work");
		assertThat(jsonTarget.asInteger("[0].addresses[1].plaque")).isEqualTo(2);
		assertThat(jsonTarget.asString("[0].family")).isEqualTo("Doi");
	}
}
