package com.fffc.csvmaker;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CsvmakerApplication {
	private static final String API_TITLE = "Csv Processing Service API";
	private static final String API_VERSION = "0.0.1";
	private static final String API_DESCRIPTION = "A basic API for parsing and processing input files to CSV files";

	public static void main(String[] args) {
		SpringApplication.run(CsvmakerApplication.class, args);
	}

	@Bean
	public OpenAPI setupOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title(API_TITLE)
						.version(API_VERSION)
						.description(API_DESCRIPTION));
	}

}
