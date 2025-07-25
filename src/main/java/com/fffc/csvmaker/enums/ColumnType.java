package com.fffc.csvmaker.enums;

import java.util.Arrays;

public enum ColumnType {
    STRING("chaîne"),
    DATE("date"),
    NUMBER("numérique");


    final String type;
    ColumnType(String type) {
        this.type = type;
    }

    public static ColumnType fromType(String type) {
        return Arrays.stream(ColumnType.values())
               .filter(t -> t.type.equals(type))
               .findFirst()
               .orElseThrow(() -> new IllegalArgumentException("Invalid column type: " + type));
    }
}
