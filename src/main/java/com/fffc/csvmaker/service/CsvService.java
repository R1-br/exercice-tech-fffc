package com.fffc.csvmaker.service;

import com.fffc.csvmaker.model.Column;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvService {
    private static final String LINE_SEPARATOR = "\r\n";

    private final MetaDataParser metaDataParser;
    private final DataMapper dataMapper;

    private CsvService(MetaDataParser metaDataParser, DataMapper dataMapper) {
        this.metaDataParser = metaDataParser;
        this.dataMapper = dataMapper;
    }

    private String generateHeaderLine(List<Column> columns) {
        return columns.stream()
                .map(Column::name)
                .collect(Collectors.joining(","));
    }

    public String process(BufferedReader metaDataReader, BufferedReader dataReader, Writer writer) throws IOException {
        //Parse and get columns
        List<Column> columns = metaDataParser.parse(metaDataReader);

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

        return writer.toString();
    }
}
