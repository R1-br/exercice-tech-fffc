package com.fffc.csvmaker.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.watcher")
public record FileWatcherConfiguration(boolean enabled, String dataDir, String metadataFile, String outputDir) {}
