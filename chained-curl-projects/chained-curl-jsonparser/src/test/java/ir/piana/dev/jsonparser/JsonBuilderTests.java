package ir.piana.dev.jsonparser;

import ir.piana.dev.TestConfig;
import ir.piana.dev.jsonparser.json.JsonParser;
import ir.piana.dev.jsonparser.json.JsonTarget;
import ir.piana.dev.jsonparser.json.JsonTargetBuilder;
import ir.piana.dev.jsonparser.json.validators.ValidatorProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Import(TestConfig.class)
class JsonBuilderTests {

	@Autowired
	private JsonParser jsonParser;

	@Autowired
	private ValidatorProvider validatorProvider;

	@Test
	void createJson() throws IOException {

		JsonTarget jsonTarget = JsonTargetBuilder.asArray()
				.add("[0].addresses[1].title", "hasan")
				.add("[0].addresses[0].title", "ali")
				.add("[0].addresses[0].plaque", 2)
				.add("[0].addresses[1].plaque", 3)
				.add("[0].family", "ahmadi")
				.add("[0].parent.work.title", "ibm")
				.add("[0].parent.national-code", "1234567890")
				.build();

		assertThat(jsonTarget.toString()).isEqualTo(
				"[{" +
						"\"addresses\":[{\"title\":\"ali\",\"plaque\":2}," +
						"{\"title\":\"hasan\",\"plaque\":3}]," +
						"\"family\":\"ahmadi\"," +
						"\"parent\":{\"work\":{\"title\":\"ibm\"},\"national-code\":\"1234567890\"}" +
						"}]");

		assertThat(jsonTarget.asInteger("[0].addresses[0].title2")).isNull();
		assertThat(jsonTarget.asString("[0].addresses[0].title")).isEqualTo("ali");
		assertThat(jsonTarget.asString("[0].addresses[1].title")).isEqualTo("hasan");
		assertThat(jsonTarget.asInteger("[0].addresses[1].plaque")).isEqualTo(3);
		assertThat(jsonTarget.asString("[0].parent.work.title")).isEqualTo("ibm");
		assertThat(jsonTarget.asString("[0].family")).isEqualTo("ahmadi");
	}
}