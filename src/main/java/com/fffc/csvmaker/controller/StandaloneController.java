package com.fffc.csvmaker.controller;

import com.fffc.csvmaker.common.util.StringUtils;
import com.fffc.csvmaker.model.ValidResponseForm;
import com.fffc.csvmaker.service.CsvService;
import com.fffc.csvmaker.common.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;

@Controller
@RequestMapping("/api/v1/csv-maker/standalone")
@Tag(name = "Standalone")
public class StandaloneController {
    private final CsvService csvService;

    public StandaloneController(CsvService csvService) {
        this.csvService = csvService;
    }

    @Operation(summary = "Process a csv parsing on UTF-8 encoded files inside form (max 1MB size)", description = "Parses and returns the output csv file in response body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The output csv is in response Body"),
            @ApiResponse(responseCode = "400", description = "Error while parsing or processing input files."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidResponseForm> processStandaloneCsv(@RequestParam(value = "metadataFile") MultipartFile metadataFile,
                                                                  @RequestParam(value = "dataFile") MultipartFile dataFile) throws IOException, ParseException {
        BufferedReader metadataReader = new BufferedReader(
                new InputStreamReader(metadataFile.getInputStream(), FileUtils.INPUT_ENCODING)
        );
        BufferedReader dataReader = new BufferedReader(
                new InputStreamReader(dataFile.getInputStream(), FileUtils.INPUT_ENCODING)
        );

        String content = csvService.process(metadataReader, dataReader, new StringWriter());

        return ResponseEntity.ok(
                new ValidResponseForm(StringUtils.encodeString(content, FileUtils.OUTPUT_ENCODING))
        );
    }
}
