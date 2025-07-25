package com.fffc.csvmaker.controller;

import com.fffc.csvmaker.model.CsvStoredTransactionForm;
import com.fffc.csvmaker.service.CsvService;
import com.fffc.csvmaker.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

@Controller
@RequestMapping("/api/v1/csv-maker/stored")
public class StoredController {
    private final Logger logger = LoggerFactory.getLogger(StoredController.class);

    private final CsvService csvService;

    public StoredController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processStoredCsv(@RequestBody CsvStoredTransactionForm transactionForm) throws IOException {
        //Init I/O streams
        BufferedReader dataReader = FileUtils.getReader(transactionForm.dataFilePath());
        BufferedReader metadataReader = FileUtils.getReader(transactionForm.metadataFilePath());
        String outputFilePath = FileUtils.getOutputFilePath();
        BufferedWriter writer = FileUtils.getWriter(outputFilePath);

        csvService.process(metadataReader, dataReader, writer);

        return ResponseEntity.ok(outputFilePath);
    }

}
