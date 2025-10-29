package com.newgen.cig.cayman.document.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CryptoException.class)
    public ResponseEntity<Map<String, String>> handleCryptoException(CryptoException ex) {
        logger.error("CryptoException caught by global exception handler", ex);
        logger.error("Exception message: {}", ex.getMessage());
        if (ex.getCause() != null) {
            logger.error("Root cause: {}", ex.getCause().getMessage(), ex.getCause());
        }
        logger.debug("Creating error response for CryptoException");
        Map<String, String> errorResponse = Map.of(
                "error", "A cryptographic error occurred.",
                "message", ex.getMessage()
        );
        logger.info("Returning BAD_REQUEST response for CryptoException");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        logger.error("Unhandled exception caught by global exception handler", ex);
        logger.error("Exception type: {}, Message: {}", ex.getClass().getName(), ex.getMessage());
        if (ex.getCause() != null) {
            logger.error("Root cause: {}", ex.getCause().getMessage(), ex.getCause());
        }
        logger.debug("Creating error response for generic exception");
        Map<String, String> errorResponse = Map.of(
                "error", "An unexpected error occurred.",
                "message", ex.getMessage() != null ? ex.getMessage() : "Unknown error"
        );
        logger.info("Returning INTERNAL_SERVER_ERROR response for unhandled exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}