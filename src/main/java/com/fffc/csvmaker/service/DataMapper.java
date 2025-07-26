package com.fffc.csvmaker.service;

import com.fffc.csvmaker.common.enums.ColumnType;
import com.fffc.csvmaker.model.Column;
import com.fffc.csvmaker.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(DataMapper.class);

    private static final String SEPARATOR = ",";
    private static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";

    private record CellContentAndCharCountToRemove(String cellContent, int charCountToRemove) {}

    private String processDate(Column column, String inputLine, int lineNumber) throws ParseException {
        String dateSubstring = inputLine.substring(0, Math.min(column.size(), inputLine.length()));
        DateFormat inputDateFormat = new SimpleDateFormat(INPUT_DATE_FORMAT);
        DateFormat outputDateFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT);

        try {
            Date date = inputDateFormat.parse(dateSubstring);

            return outputDateFormat.format(date);
        } catch (ParseException e) {
            throw new ParseException("Invalid Date at line " + lineNumber + ", column " + column.name() + ": " + dateSubstring, e.getErrorOffset());
        }
    }
    private String processNumber(Column column, String inputLine, int lineNumber) {
        String numberSubstring = inputLine.substring(0, Math.min(column.size(), StringUtils.getFirstNonNumericalIndex(inputLine)));

        try {
            return numberSubstring.contains(".") ?
                    Double.toString(parseDouble(numberSubstring)) : Long.toString(parseLong(numberSubstring));
        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid Number at line " + lineNumber + "for column " + column.name() + ": " + numberSubstring);
        }
    }
    private String processString(Column column, String inputLine, int nextSpaceIndex) {
        int minBetweenColumnSizeAndLength = Math.min(column.size(), inputLine.length() - 1);

        //return all chars until the next space or end of line or max column size
        if (nextSpaceIndex > 0) {
            return inputLine.substring(0,  Math.min(nextSpaceIndex, minBetweenColumnSizeAndLength));
        } else {
            return inputLine.substring(0, minBetweenColumnSizeAndLength);
        }
    }

    private String processQuotedString(Column column, String inputLine, int nextQuoteIndex) {
        inputLine = inputLine.substring(1);

        //return all chars until the next double quote or end of line or max column size
        if (nextQuoteIndex > 0) {
            return inputLine.substring(0, Math.min(inputLine.length() - 1, Math.min(nextQuoteIndex - 1, column.size())));
        } else {
            return inputLine.substring(2, Math.min(inputLine.length() - 1, column.size()));
        }
    }

    /**
     * Map input to a csv cell.
     * @param column the current column to map the cell from
     * @param inputLine the input line string
     * @param lineNumber current line (for logging purposes)
     * @return the CSV string cell + the number of characters processed to remove from input
     */
    private CellContentAndCharCountToRemove mapCell(Column column, String inputLine, int lineNumber) throws ParseException {
        boolean isQuoted = false;
        int nextInputSeparatorIndex = -1;

        String cellContent = switch (column.type()) {
            case DATE -> processDate(column, inputLine, lineNumber);
            case NUMBER -> processNumber(column, inputLine, lineNumber);
            case STRING -> {
                isQuoted = inputLine.startsWith("\"");
                nextInputSeparatorIndex = isQuoted ? inputLine.indexOf("\"", 1) : inputLine.indexOf(" ", 1);
                yield isQuoted ?
                        processQuotedString(column, inputLine, nextInputSeparatorIndex)
                        : processString(column, inputLine, nextInputSeparatorIndex);
            }
        };

        if (cellContent == null || cellContent.isEmpty()) {
            return null;
        }

        if (ColumnType.STRING.equals(column.type())) {
            return new CellContentAndCharCountToRemove(
                    (isQuoted || cellContent.contains(SEPARATOR)) ? StringUtils.quoteString(cellContent) : cellContent,
                    Math.min(
                            inputLine.length() - 1,
                            (Math.max(cellContent.length() + (isQuoted ? 2 : 0), nextInputSeparatorIndex + 1))
                    )
            );
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
    public String mapLine(List<Column> columns, String inputLine, int lineNumber) throws ParseException {
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
           logger.warn("No enough data found at line " + lineNumber + " to match the specified columns. Skipping this line.");

            return null;
        }

        return csvRecord.toString();
    }
}
