package com.csvmaker.core;

import com.csvmaker.model.Column;

import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.lang.Double.parseDouble;

public class dataMapper {
    private static final String SEPARATOR = ",";
    private static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";

    private String processDate(String inputLine, int size, int lineNumber, int columnIndex) {
        String dateSubstring = inputLine.substring(0, Math.min(size, inputLine.length()));
        DateFormat dateFormat = new SimpleDateFormat(INPUT_DATE_FORMAT);
        DateFormat outputDateFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT);

        try {
            Date date = dateFormat.parse(dateSubstring);

            return outputDateFormat.format(date);
        } catch (ParseException _) {
            System.err.println("Invalid Date at line " + lineNumber + ", column " + columnIndex + ": " + dateSubstring);
            System.exit(1);
        }

        return null;
    }

    private String processNumber(String inputLine, int size, int lineNumber, int columnIndex) {
        String numberSubstring = inputLine.trim().substring(0, Math.min(size, inputLine.length() - 1));

        try {
            Double number = parseDouble(numberSubstring);

            return number.toString();
        } catch (NumberFormatException _) {
            System.err.println("Invalid Number at line " + lineNumber + ", column " + columnIndex + ": " + numberSubstring);
            System.exit(1);
        }

        return null;
    }

    private String processString(String inputLine, int size, int lineNumber, int columnIndex) {
        //TODO case double quotes handling
        //TODO case maxSize
        //TODO case Space
        return "toto";
    }
    /**
     * Map a line of data to a CSV record based on the given columns.
     * @param columns the columns
     * @param inputLine the data line
     * @param lineNumber the line number in the data file
     * @return the CSV string record
     */
    public String mapLine(List<Column> columns, String inputLine, int lineNumber) {
        StringBuilder csvRecord = new StringBuilder();
        int columnIndex = 1;

        for (Column column : columns) {
            switch (column.type()) {
                case DATE -> csvRecord.append(processDate(inputLine, column.size(), lineNumber, columnIndex));
                case NUMBER -> csvRecord.append(processNumber(inputLine, column.size(), lineNumber, columnIndex));
                case STRING -> csvRecord.append(processString(inputLine, column.size(), lineNumber, columnIndex));
            }

            inputLine = inputLine.substring(Math.min(column.size(), inputLine.length() - 1));
            columnIndex++;

            if (inputLine.isEmpty()) {
                break;
            }

            if (columnIndex <= columns.size()) {
                csvRecord.append(SEPARATOR);
            }
        }

        if (columnIndex != columns.size() + 1) {
            System.err.println("No enough data found at line " + lineNumber + " to match the specified columns. Skipping this line.");

            return null;
        }

        return csvRecord.toString();
    }
}
