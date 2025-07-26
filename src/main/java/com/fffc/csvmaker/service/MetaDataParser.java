package com.fffc.csvmaker.service;

import com.fffc.csvmaker.common.enums.ColumnType;
import com.fffc.csvmaker.common.exceptions.MetaDataParsingException;
import com.fffc.csvmaker.controller.StoredController;
import com.fffc.csvmaker.model.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MetaDataParser {
    private final Logger logger = LoggerFactory.getLogger(MetaDataParser.class);

    /**
     * Parses a line from the metadata file and returns a Column object (name, size, type).
     * @param line the line from the metadata file.
     * @param lineNumber the line number in the metadata file (logging purposes).
     * @return the parsed Column object or null if parsing failed (currently programs exits on error).
     */
    private Column parseLine(String line, int lineNumber) {
        String[] parts = line.split(",");

        if (parts.length < 3) {
            throw (new MetaDataParsingException(
                    "MetaData file line " + lineNumber + ": Expected 3 comma-separated fields, found " + parts.length + "."
            ));
        }

        try {
           Column column = new Column(parts[0], Integer.parseInt(parts[1]), ColumnType.fromType(parts[2]));

           if (column.size() <= 0) {
               throw (new MetaDataParsingException(
                       "MetaData file line " + lineNumber + "Invalid size: " + parts[1] + ". Size must be a positive integer."
               ));
           }

           return column;
        } catch (NumberFormatException _) {
            throw new NumberFormatException("Metadata line " + lineNumber + ": Invalid numeric value for size: " + parts[1] + ".");
        } catch (IllegalArgumentException _) {
            throw new IllegalArgumentException("Metadata line " + lineNumber + "Unsupported column type: " + parts[2] + ".");
        }
    }

    /**
     * Parses the metadata file and returns a list of columns(name, size, type).
     * @param reader a BufferedReader from which metadata will be read.
     * @return the list of columns.
     */
    public List<Column> parse(BufferedReader reader) throws IOException {
        try {
            List<Column> columns = new ArrayList<>();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.trim().isEmpty()) {
                    continue;
                }

                columns.add(parseLine(line, lineNumber));
                lineNumber++;
            }

            return columns;
        } catch (IOException _) {
            throw new IOException("Unknown Error reading MetaData file");
        }
    }
}
