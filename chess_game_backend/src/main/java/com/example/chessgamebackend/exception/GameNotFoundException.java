package com.example.chessgamebackend.exception;

/**
 * PUBLIC_INTERFACE
 * Thrown when a game id cannot be found.
 */
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) { super(message); }
}
