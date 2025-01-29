package org.javaacademy.cinema.controller;

import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.dto.ErrorResponse;
import org.javaacademy.cinema.exception.DataMappingException;
import org.javaacademy.cinema.exception.EntitySaveException;
import org.javaacademy.cinema.exception.NotFoundException;
import org.javaacademy.cinema.exception.TicketAlreadyPurchasedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            DataMappingException.class,
    })
    public ResponseEntity<ErrorResponse> handleDataMappingException(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({
            EntitySaveException.class,
    })
    public ResponseEntity<ErrorResponse> handleEntitySaveException(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler({
            TicketAlreadyPurchasedException.class
    })
    public ResponseEntity<ErrorResponse> handleAlreadyPurchasedException(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler({
            NotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status.value(), status.name(), message));
    }
}
