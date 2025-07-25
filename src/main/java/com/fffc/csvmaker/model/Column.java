package com.fffc.csvmaker.model;

import com.fffc.csvmaker.enums.ColumnType;

public record Column(
        String name,
        Integer size,
        ColumnType type
) {
}
