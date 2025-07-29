package com.fffc.csvmaker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CsvStoredTransactionForm(
        @NotEmpty(message = "metadataFilePath required") @NotBlank(message = "metadataFilePath required")
        String metadataFilePath,
        @NotEmpty(message = "dataFilePath required") @NotBlank(message = "dataFilePath required")
        String dataFilePath
) {}
