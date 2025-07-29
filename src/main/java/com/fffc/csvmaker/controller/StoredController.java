package com.fffc.csvmaker.controller;

import com.fffc.csvmaker.common.config.FileApiConfiguration;
import com.fffc.csvmaker.model.CsvStoredTransactionForm;
import com.fffc.csvmaker.model.ValidResponseForm;
import com.fffc.csvmaker.service.CsvService;
import com.fffc.csvmaker.common.util.FileUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.text.ParseException;

@Controller
@RequestMapping("/api/v1/csv-maker/stored")
@Tag(name = "Stored")
public class StoredController {
    private final CsvService csvService;

    private final String metaDataBaseDir;
    private final String dataBaseDir;
    private final String outputDir;

    public StoredController(FileApiConfiguration fileApiConfiguration, CsvService csvService) {
        this.metaDataBaseDir = fileApiConfiguration.metadataBaseDir();
        this.dataBaseDir = fileApiConfiguration.dataBaseDir();
        this.outputDir = fileApiConfiguration.outputDir();
        this.csvService = csvService;
    }

    @Operation(summary = "Process a csv parsing on stored files", description = "Parses and save the output csv file. Returns output filepath in successful response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The output csv has been saved."),
            @ApiResponse(responseCode = "400", description = "Error while parsing or processing input files."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE,  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidResponseForm> processStoredCsv(@RequestBody @Valid CsvStoredTransactionForm transactionForm) throws IOException, ParseException {
        //if start with '/', consider as absolute path else consider as relative to configured directories
        String dataFilePath = transactionForm.dataFilePath().startsWith("/") ?
                transactionForm.dataFilePath() : metaDataBaseDir + transactionForm.dataFilePath() ;
        String metadataFilePath = transactionForm.metadataFilePath().startsWith("/") ?
                transactionForm.metadataFilePath() : dataBaseDir + transactionForm.metadataFilePath()  ;

        //Init I/O streams
        BufferedReader dataReader = FileUtils.getReader(dataFilePath);
        BufferedReader metadataReader = FileUtils.getReader(metadataFilePath);

        String outputFilePath = FileUtils.getOutputFilePath(
                dataFilePath.substring(0, dataFilePath.lastIndexOf("/") + 1) + outputDir
        );
        BufferedWriter writer = FileUtils.getWriter(outputFilePath);

        //Process
        csvService.process(metadataReader, dataReader, writer);

        return ResponseEntity.ok(new ValidResponseForm(outputFilePath));
    }
}
