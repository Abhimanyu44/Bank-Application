package com.bankapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Bank App",
				description = "Developed in Rest APIs",
				version = "v1.0",
				contact = @Contact(
						name = "Vivek Kumar Agrahari",
						email = "agraharivivek44@gmail.com",
						url = "https://github.com/Abhimanyu44"
				),
				license = @License(
						name = "The Bank App",
						url = "https://github.com/Abhimanyu44"

				)
		),
		externalDocs = @ExternalDocumentation(
				description = "The Banking Application Documentation",
				url = "https://github.com/Abhimanyu44"
		)
)
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
