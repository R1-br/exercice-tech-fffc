package com.fffc.csvmaker.controller;

import com.fffc.csvmaker.model.ErrorResponseForm;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController {
    protected Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class)  ;

    /**
     * Form Validation Exception Handler
     * @param ex the exception
     * @return a 400 response  with the error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseForm> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(
                new ErrorResponseForm(errors)
        );
    }

    /**
     * Default Exception Handler
     * @param req the HTTP request where the exception raised
     * @param ex the exception
     * @return Json obect with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseForm> handleError(HttpServletRequest req, Exception ex) {
        if (ex instanceof NoResourceFoundException) {
            logger.info(req.getRequestURI() + " : 404");

            return ResponseEntity.notFound().build();
        }

        String message = "Request %1$s raised %2$s .Error Message: %3$s".formatted(
                req.getRequestURL().toString(), ex, ex.getMessage()
        );
        logger.error(message);

        if (ex instanceof RuntimeException || ex instanceof ParseException || ex instanceof FileNotFoundException) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponseForm(Map.of("message", ex.getMessage()))
            );
        } else {
            return ResponseEntity.status(500).body(
                    new ErrorResponseForm(Map.of("message", ex.getMessage()))
            );
        }
    }
}
