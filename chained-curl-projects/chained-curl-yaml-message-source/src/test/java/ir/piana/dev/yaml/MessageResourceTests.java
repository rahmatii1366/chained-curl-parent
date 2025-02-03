package ir.piana.dev.yaml;

import ir.piana.dev.yaml.bundle.YamlReloadableResourceBundleMessageSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Import(MessageResourceTests.TestConfig.class)
class MessageResourceTests {

	@Autowired
	private MessageSource messageSource;

	@SpringBootConfiguration
	@ComponentScan("ir.jibit.dev")
	public static class TestConfig {
		@Bean("messageSource")
		public MessageSource validatorMessageSource() {
			YamlReloadableResourceBundleMessageSource messageSource
					= new YamlReloadableResourceBundleMessageSource();
			messageSource.setBasename("classpath:messages/messages");
			messageSource.setDefaultEncoding("UTF-8");
			return messageSource;
		}
	}

	@Test
	void ymlTest() throws IOException {
		String message = messageSource.getMessage(
				"test.validationFailed.message", null, Locale.forLanguageTag("fa"));
		assertThat(message).isEqualTo("یک خطا");
		message = messageSource.getMessage(
				"test.validationFailed.message", null, Locale.forLanguageTag("fa"));
		assertThat(message).isEqualTo("یک خطا");
		message = messageSource.getMessage(
				"test.validationFailed.message", null, Locale.forLanguageTag("en"));
		assertThat(message).isEqualTo("an error");
	}

	void propertiesTest() throws IOException {
		String message = messageSource.getMessage(
				"test.validationFailed.message", null, Locale.forLanguageTag("en"));
		assertThat(message).isEqualTo("an error");
	}
}
