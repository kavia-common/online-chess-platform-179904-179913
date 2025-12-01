package com.example.chessgamebackend.exception;

/**
 * PUBLIC_INTERFACE
 * Thrown when a move is illegal.
 */
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) { super(message); }
}
