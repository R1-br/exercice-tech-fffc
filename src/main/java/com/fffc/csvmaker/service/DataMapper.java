package com.fffc.csvmaker.service;

import com.fffc.csvmaker.enums.ColumnType;
import com.fffc.csvmaker.model.Column;
import com.fffc.csvmaker.util.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

@Service
public class DataMapper {
    private static final String SEPARATOR = ",";
    private static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";

    private record CellContentAndCharCountToRemove(String cellContent, int charCountToRemove) {}

    private String processDate(Column column, String inputLine, int lineNumber) {
        String dateSubstring = inputLine.substring(0, Math.min(column.size(), inputLine.length()));
        DateFormat inputDateFormat = new SimpleDateFormat(INPUT_DATE_FORMAT);
        DateFormat outputDateFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT);

        try {
            Date date = inputDateFormat.parse(dateSubstring);

            return outputDateFormat.format(date);
        } catch (ParseException _) {
            System.err.println("Invalid Date at line " + lineNumber + ", column " + column.name() + ": " + dateSubstring);
            System.exit(1);
        }

        return null;
    }
    private String processNumber(Column column, String inputLine, int lineNumber) {
        String numberSubstring = inputLine.substring(0, Math.min(column.size(), StringUtils.getFirstNonNumericalIndex(inputLine)));

        try {
            if (numberSubstring.contains(".")) {
                return Double.toString(parseDouble(numberSubstring));
            } else {
                return Long.toString(parseLong(numberSubstring));
            }
        } catch (NumberFormatException _) {
            System.err.println("Invalid Number at line " + lineNumber + "for column " + column.name() + ": " + numberSubstring);
            System.exit(1);
        }

        return null;
    }
    private String processString(Column column, String inputLine, boolean isQuoted, int nextQuoteIndex) {
        //Starting with double quotes -> quoted string, else normal string
        if (isQuoted) {
            inputLine = inputLine.substring(1);
            //return all chars until the next double quote or end of line or max column size
            if (nextQuoteIndex > 0) {
                return inputLine.substring(0, Math.min(inputLine.length() - 1, Math.min(nextQuoteIndex - 1, column.size())));
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
     * Map input to a csv Cell record.
     * @param column the current column to map the cell from
     * @param inputLine the input line string
     * @param lineNumber current line (for logging purposes)
     * @return the CSV string cell + the number of characters processed to remove from input
     */
    private CellContentAndCharCountToRemove mapCell(Column column, String inputLine, int lineNumber) {
        boolean isQuoted = false;
        int nextQuoteIndex = -1;

        String cellContent = switch (column.type()) {
            case DATE -> processDate(column, inputLine, lineNumber);
            case NUMBER -> processNumber(column, inputLine, lineNumber);
            case STRING -> {
                isQuoted = inputLine.startsWith("\"");
                nextQuoteIndex = inputLine.indexOf("\"", 1);
                yield processString(column, inputLine, isQuoted, nextQuoteIndex);
            }
        };

        if (cellContent == null || cellContent.isEmpty()) {
            return null;
        }

        if (ColumnType.STRING.equals(column.type())) {
            if (isQuoted) {
                //if quoted, inputLine must truncate cellContent + 2 (for quotes)
                // or until the nextQuote if the string was too long for the specified column length
                return new CellContentAndCharCountToRemove(
                        StringUtils.quoteString(cellContent),
                        Math.min(inputLine.length() - 1, (Math.max(cellContent.length() + 2, nextQuoteIndex + 1))));
            } else if (cellContent.contains(SEPARATOR)) {
                return new CellContentAndCharCountToRemove(StringUtils.quoteString(cellContent), cellContent.length());
            }
        }

        return new CellContentAndCharCountToRemove(cellContent, cellContent.length());
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

            CellContentAndCharCountToRemove cellContentAndCharCountToRemove = mapCell(column, inputLine, lineNumber);

            if (cellContentAndCharCountToRemove != null) {
                inputLine = inputLine.substring(Math.min(inputLine.length(), cellContentAndCharCountToRemove.charCountToRemove()));
                csvRecord.append(cellContentAndCharCountToRemove.cellContent());
            }

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
