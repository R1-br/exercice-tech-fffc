package com.csvmaker.enums;

public enum ColumnType {
    STRING("chaîne"),
    DATE("date"),
    NUMBER("numérique");


    final String name;
    ColumnType(String name) {
        this.name = name;
    }
}
