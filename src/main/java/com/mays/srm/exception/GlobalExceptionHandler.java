package com.mays.srm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ResourceNotFoundException exc) {
        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exc) {
        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Specially handle Database Integrity issues (like Unique Key constraints)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exc) {
        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.CONFLICT.value()); // 409 Conflict is standard for duplicate data

        // Extract a more readable message if it's a duplicate entry
        String message = exc.getMostSpecificCause().getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            error.setMessage("A record with this unique value (like mobile number or email) already exists.");
        } else {
            error.setMessage("Database Error: " + message);
        }

        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException exc) {
        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Use getCause() if available, otherwise just use the main message
        String rootCause = (exc.getCause() != null) ? exc.getCause().getMessage() : "";

        if (!rootCause.isEmpty()) {
             error.setMessage(exc.getMessage() + " | Cause: " + rootCause);
        } else {
             error.setMessage(exc.getMessage());
        }

        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exc) {
        ErrorResponse error = new ErrorResponse();

        // If it's an completely unhandled exception
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage("An unexpected error occurred: " + exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
