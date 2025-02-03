package ir.piana.dev.chainedcurl.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "ir.piana.dev.chainedcurl", "ir.piana.dev.jsonparser" })
public class DirectDebitClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DirectDebitClientApplication.class, args);
	}

}
