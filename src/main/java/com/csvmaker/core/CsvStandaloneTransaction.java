package com.csvmaker.core;

import com.csvmaker.model.Column;
import com.csvmaker.util.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CsvStandaloneTransaction {
    private static final String LINE_SEPARATOR = "\r\n";
    private final String metaDataFilePath;
    private final String dataFilePath;
    private final String outputFilePath;

    private final MetaDataParser metaDataParser = new MetaDataParser();
    private final dataMapper dataMapper = new dataMapper();

    public CsvStandaloneTransaction(String metaDataFilePath, String dataFilePath, String outputFilePath) {
        this.metaDataFilePath = metaDataFilePath;
        this.dataFilePath = dataFilePath;
        this.outputFilePath = outputFilePath;
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
            if (line.isEmpty()) {
                continue;
            }
            String csvRecord = dataMapper.mapLine(columns, line, lineNumber);

            if (csvRecord != null) {
                writer.write(csvRecord);
                writer.write(LINE_SEPARATOR);
            }
        }

        //Close streams
        dataReader.close();
        writer.close();
    }
}
