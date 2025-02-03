package ir.piana.dev;

import ir.piana.dev.yaml.bundle.YamlReloadableResourceBundleMessageSource;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan("ir.piana.dev")
public class TestConfig {
    @Bean("messageSource")
    public MessageSource validatorMessageSource() {
        YamlReloadableResourceBundleMessageSource messageSource
                = new YamlReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
