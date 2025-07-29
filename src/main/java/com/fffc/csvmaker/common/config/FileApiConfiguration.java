package com.fffc.csvmaker.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.api")
public record FileApiConfiguration(String metadataBaseDir, String dataBaseDir, String outputDir) {

}
