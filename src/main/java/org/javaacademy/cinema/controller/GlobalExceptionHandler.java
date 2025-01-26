package org.javaacademy.cinema.controller;

import lombok.extern.slf4j.Slf4j;
import org.javaacademy.cinema.dto.ErrorResponse;
import org.javaacademy.cinema.exception.DataMappingException;
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
            TicketAlreadyPurchasedException.class
    })
    public ResponseEntity<ErrorResponse> handleBookingException(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
    }

    @ExceptionHandler({
            NotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status.value(), status.name(), message));
    }
}
