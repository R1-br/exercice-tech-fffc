package com.fffc.csvmaker.controller;

import com.fffc.csvmaker.service.CsvService;
import com.fffc.csvmaker.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

@Controller
@RequestMapping("/api/v1/csv-maker/standalone")
public class StandaloneController {
    private final Logger logger = LoggerFactory.getLogger(StandaloneController.class);

    private final CsvService csvService;

    public StandaloneController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> processStandaloneCsv(@RequestParam(value = "metadataFile") MultipartFile metadataFile,
                                                      @RequestParam(value = "dataFile") MultipartFile dataFile) throws IOException {
        BufferedReader metadataReader = new BufferedReader(
                new InputStreamReader(metadataFile.getInputStream(), FileUtils.INPUT_ENCODING)
        );
        BufferedReader dataReader = new BufferedReader(
                new InputStreamReader(dataFile.getInputStream(), FileUtils.INPUT_ENCODING)
        );

        String content = csvService.process(metadataReader, dataReader, new StringWriter());

        return ResponseEntity.ok(content);
    }
}
