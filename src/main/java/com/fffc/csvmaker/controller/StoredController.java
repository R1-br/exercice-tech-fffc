package com.fffc.csvmaker.controller;

import com.fffc.csvmaker.common.util.StringUtils;
import com.fffc.csvmaker.model.CsvStoredTransactionForm;
import com.fffc.csvmaker.service.CsvService;
import com.fffc.csvmaker.common.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;

@Controller
@RequestMapping("/api/v1/csv-maker/stored")
@Tag(name = "Stored")
public class StoredController {
    private final CsvService csvService;

    @Value("${file.metadata-basedir}")
    private String metaDataBaseDir;

    @Value("${file.data-basedir}")
    private String dataBaseDir;

    @Value("${file.output-dir}")
    private String outputDir;

    public StoredController(CsvService csvService) {
        this.csvService = csvService;
    }

    @Operation(summary = "Process a csv parsing on stored files", description = "Parses and save the output csv file. Returns output filepath in successful response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The output csv has been saved."),
            @ApiResponse(responseCode = "400", description = "Error while parsing or processing input files."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> processStoredCsv(@RequestBody CsvStoredTransactionForm transactionForm) throws IOException, ParseException {
        //if start with '/', consider as absolute else consider as relative to configured directories
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

        return ResponseEntity.ok(outputFilePath);
    }
}
