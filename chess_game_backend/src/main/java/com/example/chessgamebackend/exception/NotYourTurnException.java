package com.example.chessgamebackend.exception;

/**
 * PUBLIC_INTERFACE
 * Thrown when a player attempts to move out of turn.
 */
public class NotYourTurnException extends RuntimeException {
    public NotYourTurnException(String message) { super(message); }
}
