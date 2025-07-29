package com.fffc.csvmaker.service;

import com.fffc.csvmaker.common.config.FileWatcherConfiguration;
import com.fffc.csvmaker.common.util.FileUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;

/**
 * Service for watching a directory for file creation and automatically process them.
 */
@Service
public class FileWatcherService {
    private static final Logger logger = LoggerFactory.getLogger(FileWatcherService.class);

    @Getter
    private final boolean watchingEnabled;
    private final String dataWatchDirectory;
    private final String metadataWatchFile;
    private final String outputDir;

    private final CsvService csvService;

    public FileWatcherService(FileWatcherConfiguration fileWatcherConfiguration, CsvService csvService) {
        this.watchingEnabled = fileWatcherConfiguration.enabled();
        this.dataWatchDirectory = fileWatcherConfiguration.dataDir();
        this.metadataWatchFile = fileWatcherConfiguration.metadataFile();
        this.outputDir = fileWatcherConfiguration.outputDir();
        this.csvService = csvService;
    }

    private void processCreatedFile(File createdFile) throws IOException, ParseException {
        // Instantiate metadata reader (in loop if changes occured since app startup)
        BufferedReader metadataReader = FileUtils.getReader(metadataWatchFile);
        // Instantiate data reader for created file
        BufferedReader dataReader = FileUtils.getReader(createdFile.getAbsolutePath());
        // Generate output filepath & writer
        String outputFilePath = FileUtils.getOutputFilePath(
                createdFile.getAbsolutePath().substring(0, createdFile.getAbsolutePath().lastIndexOf("/") + 1)
                        + outputDir
        );
        BufferedWriter writer = FileUtils.getWriter(outputFilePath);

        logger.info("begin processing file: " + createdFile.getAbsolutePath() + " to output file: " + outputFilePath + "...");
        csvService.process(metadataReader, dataReader, writer);
    }

    /**
     * Watch for file creation events and automatically process them.
     * @throws IOException if an error occurs during file watching or I/O operation failing.
     * @throws InterruptedException if the thread is interrupted while waiting.
     * @throws ParseException if a parsing error occurs while processing the CSV data (date column)
     */
    public void watchFileForProcessing() throws IOException, InterruptedException, ParseException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(dataWatchDirectory);

        logger.info("Starting Watching creation of files in directory: " + dataWatchDirectory);

        //register for creation events inside directory.
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        // watch loop to wait for creation file events
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
               logger.info("Event kind:" + event.kind() + ". File created: " + event.context() + ".");
               File createdFile = new File(dataWatchDirectory + event.context());

               if (createdFile.isFile()) {
                   processCreatedFile(createdFile);
               }
            }
            key.reset();
        }
    }
}
