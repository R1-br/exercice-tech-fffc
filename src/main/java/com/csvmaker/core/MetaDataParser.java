package com.csvmaker.core;

import com.csvmaker.enums.ColumnType;
import com.csvmaker.model.Column;
import com.csvmaker.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetaDataParser {

    /**
     * Parses a line from the metadata file and returns a Column object.
     * @param line the line from the metadata file.
     * @param lineNumber the line number in the metadata file (logging purposes).
     * @return the parsed Column object or null if parsing failed (currently programs exits on error).
     */
    private Column parseLine(String line, int lineNumber) {
        String[] parts = line.split(",");
        if (parts.length < 3) {
            System.err.println( "MetaData file line " + lineNumber + ": Expected 3 comma-separated fields, found " + parts.length + ".");
            System.exit(1);
        }

        try {
            return new Column(parts[0], Integer.parseInt(parts[1]), ColumnType.fromType(parts[2]));
        } catch (NumberFormatException _) {
            System.err.println("Metadata line " + lineNumber + ": Invalid numeric value for size: " + parts[1] + ".");
            System.exit(1);
        } catch (IllegalArgumentException _) {
            System.err.println("Metadata line " + lineNumber + ": Unsupported column type:" + parts[2] + ".");
            System.exit(1);
        }
        return null; // This should never happen
    }

    /**
     * Parses the metadata file and returns a list of columns(name, length, type).
     * @param metaDataFilePath the path to the metadata file.
     * @return the list of columns.
     */
    public List<Column> parse(String metaDataFilePath) {
        try {
            BufferedReader reader = FileUtils.getReader(metaDataFilePath);
            List<Column> columns = new ArrayList<>();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                columns.add(parseLine(line, lineNumber));
                lineNumber++;
            }

            return columns;
        } catch (IOException e) {
            System.err.println("Unknown Error reading MetaData file: " + metaDataFilePath + "\n\rDETAILS:\n\r" + e.getMessage());
            System.exit(1);
        }

        return Collections.emptyList();
    }
}
