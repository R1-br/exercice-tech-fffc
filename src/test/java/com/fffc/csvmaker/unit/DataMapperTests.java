package com.fffc.csvmaker.unit;

import com.fffc.csvmaker.common.enums.ColumnType;
import com.fffc.csvmaker.model.Column;
import com.fffc.csvmaker.service.DataMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DataMapperTests {
    private final DataMapper dataMapper = new DataMapper();

    @Test
    void TestValid() throws ParseException {
        List<Column> columnList = List.of(
                new Column("Date de naissance", 10, ColumnType.DATE),
                new Column("Prénom", 15, ColumnType.STRING),
                new Column("Nom", 30, ColumnType.STRING),
                new Column("Poids", 5, ColumnType.NUMBER),
                new Column("Age", 3, ColumnType.NUMBER)
        );
        String inputLine = "1980-01-01Pierre, \"Nom de FAmille\" 75.43 31";

        String result = dataMapper.mapLine(columnList, inputLine, 1);

        assertEquals("01/01/1980,\"Pierre,\",\"Nom de FAmille\",75.43,31", result);
    }

    @Test
    void TestInvalidDate() {
        List<Column> columnList = List.of(
                new Column("Date de naissance", 10, ColumnType.DATE),
                new Column("Prénom", 15, ColumnType.STRING),
                new Column("Poids", 5, ColumnType.NUMBER)
        );
        String invalidDate = "198toto0-01-01 Pierre 75";

        Exception invalidDateException = assertThrows(ParseException.class, () -> {
            dataMapper.mapLine(columnList, invalidDate, 1);
        });

        assertEquals("Invalid Date at line 1, column Date de naissance: 198toto0-0", invalidDateException.getMessage());
    }

    @Test
    void TestInvalidNumber() {
        List<Column> columnList = List.of(
                new Column("Date de naissance", 10, ColumnType.DATE),
                new Column("Prénom", 15, ColumnType.STRING),
                new Column("Poids", 5, ColumnType.NUMBER)
        );
        String invalidNumber = "1980-01-01 Pierre toto";

        Exception invalidNumberException = assertThrows(NumberFormatException.class, () -> {
            dataMapper.mapLine(columnList, invalidNumber, 1);
        });

        assertEquals("Invalid Number at line 1 for column Poids: toto", invalidNumberException.getMessage());
    }
}
