package com.fffc.csvmaker.model;

import lombok.NonNull;

public record CsvStoredTransactionForm(
        @NonNull
        String metadataFilePath,
        @NonNull
        String dataFilePath
) {}
