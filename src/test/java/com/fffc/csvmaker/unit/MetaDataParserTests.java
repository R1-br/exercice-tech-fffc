package com.fffc.csvmaker.unit;

import com.fffc.csvmaker.common.enums.ColumnType;
import com.fffc.csvmaker.common.exceptions.MetaDataParsingException;
import com.fffc.csvmaker.model.Column;
import com.fffc.csvmaker.service.MetaDataParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MetaDataParserTests {
    private final MetaDataParser metaDataParser = new MetaDataParser();

    @Test
    void testValid() throws IOException {
        String test = "Date de naissance,10,date\r\n" +
                "Prénom,15,chaîne\r\n" +
                "Poids,5,numérique";

        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);
        List<Column> columns = metaDataParser.parse(reader);

        assertEquals(3, columns.size());
        assertEquals("Date de naissance", columns.getFirst().name());
        assertEquals(10, columns.getFirst().size());
        assertEquals(ColumnType.DATE, columns.getFirst().type());
        assertEquals("Prénom", columns.get(1).name());
        assertEquals(15, columns.get(1).size());
        assertEquals(ColumnType.STRING, columns.get(1).type());
        assertEquals("Poids", columns.get(2).name());
        assertEquals(5, columns.get(2).size());
        assertEquals(ColumnType.NUMBER, columns.get(2).type());
    }

    @Test
    void testInvalidColumnCount() {
        String testInvalidColumnCount = "Date de naissance,10";
        Reader inputString = new StringReader(testInvalidColumnCount);
        BufferedReader reader = new BufferedReader(inputString);

        Exception invalidColumnCountException = assertThrows(MetaDataParsingException.class, () -> {
            metaDataParser.parse(reader);
        });

        assertEquals("MetaData file line 1. Expected 3 comma-separated fields, found 2.", invalidColumnCountException.getMessage());
    }

    @Test
    void testNonNumericSize() {
        String testInvalidNumber = "Date de naissance,aaa,date";
        Reader inputString = new StringReader(testInvalidNumber);
        BufferedReader reader = new BufferedReader(inputString);

        Exception invalidNumberException = assertThrows(NumberFormatException.class, () -> {
            metaDataParser.parse(reader);
        });

        assertEquals("Metadata line 1. Invalid numeric value for size: aaa.", invalidNumberException.getMessage());
    }

    @Test
    void testNegativeSize() {
        String testNegativeSize = "Date de naissance,-1,date";
        Reader inputString = new StringReader(testNegativeSize);
        BufferedReader reader = new BufferedReader(inputString);

        Exception negativeSizeException = assertThrows(MetaDataParsingException.class, () -> {
            metaDataParser.parse(reader);
        });

        assertEquals("MetaData file line 1. Invalid size: -1. Size must be a positive integer.", negativeSizeException.getMessage());
    }
    @Test
    void testFloatingPointSize() {
        String testFloatingSize = "Date de naissance,10.5,date";
        Reader inputString = new StringReader(testFloatingSize);
        BufferedReader reader = new BufferedReader(inputString);

        Exception floatingSizeException = assertThrows(NumberFormatException.class, () -> {
            metaDataParser.parse(reader);
        });

        assertEquals("Metadata line 1. Invalid numeric value for size: 10.5.", floatingSizeException.getMessage());
    }

    @Test
    void testInvalidColumnType() {
        String testInvalidColumn = "Date de naissance,10,aaa";
        Reader inputString = new StringReader(testInvalidColumn);
        BufferedReader reader = new BufferedReader(inputString);

        Exception invalidColumnException = assertThrows(IllegalArgumentException.class, () -> {
            metaDataParser.parse(reader);
        });

        assertEquals("Metadata line 1. Unsupported column type: aaa.", invalidColumnException.getMessage());
    }
}
