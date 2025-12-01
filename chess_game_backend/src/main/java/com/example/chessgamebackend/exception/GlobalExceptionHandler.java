package com.example.chessgamebackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PUBLIC_INTERFACE
 * Maps exceptions to consistent error responses.
 *
 * This advice is scoped only to the application's base package to avoid
 * intercepting framework and springdoc endpoints such as /api-docs and /swagger-ui.
 */
@RestControllerAdvice(basePackages = "com.example.chessgamebackend")
public class GlobalExceptionHandler {

    private ResponseEntity<Object> body(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * PUBLIC_INTERFACE
     * Handle not found for game resources.
     */
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(GameNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * PUBLIC_INTERFACE
     * Handle invalid moves, turn violations, and bad input.
     */
    @ExceptionHandler({InvalidMoveException.class, NotYourTurnException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
