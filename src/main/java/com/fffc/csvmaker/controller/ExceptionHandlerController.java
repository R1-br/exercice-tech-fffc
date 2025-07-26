package com.fffc.csvmaker.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.text.ParseException;

@ControllerAdvice
public class ExceptionHandlerController {
    protected Logger logger;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(HttpServletRequest req, Exception ex) {
        String message = "Request %1$s raised %2$s .Error Message: %3$s".formatted(
                req.getRequestURL().toString(), ex, ex.getMessage()
        );

        logger.error(message);

        if (ex instanceof RuntimeException || ex instanceof ParseException || ex instanceof FileNotFoundException) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } else {
            return ResponseEntity.status(500).body("Internal Server Error: " + ex.getMessage());
        }
    }
}
