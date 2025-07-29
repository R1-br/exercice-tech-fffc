package com.fffc.csvmaker;

import com.fffc.csvmaker.common.config.FileApiConfiguration;
import com.fffc.csvmaker.common.config.FileWatcherConfiguration;
import com.fffc.csvmaker.service.FileWatcherService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
@EnableConfigurationProperties({FileWatcherConfiguration.class, FileApiConfiguration.class})
public class CsvmakerApplication {
	private static final String API_TITLE = "Csv Processing Service API";
	private static final String API_VERSION = "0.0.1";
	private static final String API_DESCRIPTION = "A basic API for parsing and processing input files to CSV files";

	public static void main(String[] args) throws IOException, InterruptedException, ParseException {
		ApplicationContext applicationContext = SpringApplication.run(CsvmakerApplication.class, args);

		FileWatcherService fileWatcherService = applicationContext.getBean(FileWatcherService.class);

		if (fileWatcherService.isWatchingEnabled()) {
			fileWatcherService.watchFileForProcessing();
		}
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
