package com.csvmaker.core;

import com.csvmaker.model.Column;

import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.lang.Double.parseDouble;

public class DataMapper {
    private static final String SEPARATOR = ",";
    private static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";

    private String processDate(Column column, String inputLine, int lineNumber) {
        String dateSubstring = inputLine.substring(0, Math.min(column.size(), inputLine.length()));
        DateFormat dateFormat = new SimpleDateFormat(INPUT_DATE_FORMAT);
        DateFormat outputDateFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT);

        try {
            Date date = dateFormat.parse(dateSubstring);

            return outputDateFormat.format(date);
        } catch (ParseException _) {
            System.err.println("Invalid Date at line " + lineNumber + ", column " + column.name() + ": " + dateSubstring);
            System.exit(1);
        }

        return null;
    }
    private String processNumber(Column column, String inputLine, int lineNumber) {
        String numberSubstring = inputLine.substring(0, Math.min(column.size(), inputLine.length()));

        try {
            Double number = parseDouble(numberSubstring);

            return number.toString();
        } catch (NumberFormatException _) {
            System.err.println("Invalid Number at line " + lineNumber + "for column " + column.name() + ": " + numberSubstring);
            System.exit(1);
        }

        return null;
    }
    private String processString(Column column, String inputLine, boolean isQuoted) {
        //Starting with double quotes -> quoted string, else normal string
        if (isQuoted) {
            inputLine = inputLine.substring(1);
            int endIndex = inputLine.indexOf("\"", 0);
            //return all chars until the next double quote or end of line or max column size
            if (endIndex > 0) {
                return inputLine.substring(0, Math.min(inputLine.length() - 1, endIndex));
            } else {
                return inputLine.substring(2, Math.min(inputLine.length() - 1, column.size()));
            }
        } else {
            int nextSpaceIndex = inputLine.indexOf(" ", 1);
            int minBetweenColumnSizeAndLength = Math.min(column.size(), inputLine.length() - 1);
            //return all chars until the next space or end of line or max column size
            if (nextSpaceIndex > 0) {
                return inputLine.substring(0,  Math.min(nextSpaceIndex, minBetweenColumnSizeAndLength));
            } else {
                return inputLine.substring(0, minBetweenColumnSizeAndLength);
            }
        }
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
            inputLine = inputLine.trim();
            boolean isQuoted = false;
            String cellContent = switch (column.type()) {
                case DATE -> processDate(column, inputLine, lineNumber);
                case NUMBER -> processNumber(column, inputLine, lineNumber);
                case STRING -> {
                    isQuoted = inputLine.startsWith("\"");
                    yield processString(column, inputLine, isQuoted);
                }
            };

            if (!cellContent.isEmpty()) {
                csvRecord.append(cellContent);
            }

            inputLine = inputLine.substring(Math.min(inputLine.length(), cellContent.length() + (isQuoted ? 2 : 0)));

            if (inputLine.isEmpty()) {
                break;
            }

            if (columnIndex <= columns.size()) {
                csvRecord.append(SEPARATOR);
            }

            columnIndex++;
        }

        if (columnIndex != columns.size()) {
            System.out.println("No enough data found at line " + lineNumber + " to match the specified columns. Skipping this line.");

            return null;
        }

        return csvRecord.toString();
    }
}
