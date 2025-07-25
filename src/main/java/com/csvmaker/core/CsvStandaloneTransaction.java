package com.csvmaker.core;

import com.csvmaker.model.Column;
import com.csvmaker.util.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CsvStandaloneTransaction {
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String OUTPUT_FILE_DATE_FORMAT = "yyyyMMdd-HH";
    private static final String OUTPUT_FILE_TIME_FORMAT = "mm:ss";
    private static final String OUTPUT_FILE_PREFIX = "-fffc.csv";
    
    private final String metaDataFilePath;
    private final String dataFilePath;
    private final String outputFilePath;

    private final MetaDataParser metaDataParser = new MetaDataParser();
    private final DataMapper dataMapper = new DataMapper();

    public CsvStandaloneTransaction(String metaDataFilePath, String dataFilePath) {
        this.metaDataFilePath = metaDataFilePath;
        this.dataFilePath = dataFilePath;
        Date current = new Date();
        this.outputFilePath = new SimpleDateFormat(OUTPUT_FILE_DATE_FORMAT).format(current) + "h" +
                new SimpleDateFormat(OUTPUT_FILE_TIME_FORMAT).format(current) +
                OUTPUT_FILE_PREFIX;
    }

    private String generateHeaderLine(List<Column> columns) {
        return columns.stream()
                .map(Column::name)
                .collect(Collectors.joining(","));
    }

    public void process() throws IOException {
        //Parse and get columns
        List<Column> columns = metaDataParser.parse(metaDataFilePath);

        //Init I/O streams
        BufferedReader dataReader = FileUtils.getReader(dataFilePath);
        BufferedWriter writer = FileUtils.getWriter(outputFilePath);

        //Write header line
        writer.write(generateHeaderLine(columns));
        writer.write(LINE_SEPARATOR);

        //read, parse and write data
        String line;
        int lineNumber = 1;

        while ((line = dataReader.readLine()) != null) {
            if (line.isEmpty() || line.trim().isEmpty()) {
                continue;
            }

            String csvRecord = dataMapper.mapLine(columns, line, lineNumber);

            if (csvRecord != null) {
                writer.write(csvRecord);
                writer.write(LINE_SEPARATOR);
            }

            lineNumber++;
        }

        //Close streams
        dataReader.close();
        writer.close();
    }
}
