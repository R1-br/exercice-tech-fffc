package com.fffc.csvmaker.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class MetaDataParsingException extends RuntimeException {
    public MetaDataParsingException(String message) {
        super(message);
    }
}
