package com.fffc.csvmaker.model;

import java.util.Map;

public record ErrorResponseForm(
        Map<String, String> errors
) {}
