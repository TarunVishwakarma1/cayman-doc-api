package com.newgen.cig.cayman.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CryptoException.class)
    public ResponseEntity<Map<String, String>> handleCryptoException(CryptoException ex) {
        // You would typically log the ex.getCause() here
        Map<String, String> errorResponse = Map.of(
                "error", "A cryptographic error occurred.",
                "message", ex.getMessage()
        );
        // Use 400 for client-side errors (e.g., bad key) or 500 for server-side (e.g., config)
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}