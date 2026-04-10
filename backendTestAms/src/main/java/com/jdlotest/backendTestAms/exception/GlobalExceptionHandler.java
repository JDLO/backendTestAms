package com.jdlotest.backendTestAms.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFound(ResourceNotFoundException ex) {
        Map<String, String> body = Map.of(
            "error", "Not Found",
            "message", ex.getMessage()
        );
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        return new ResponseEntity<>(Map.of("message", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
