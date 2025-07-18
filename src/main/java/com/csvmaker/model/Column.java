package com.csvmaker.model;

import com.csvmaker.enums.ColumnType;

public record Column(
        String name,
        Integer size,
        ColumnType type
) {
}
