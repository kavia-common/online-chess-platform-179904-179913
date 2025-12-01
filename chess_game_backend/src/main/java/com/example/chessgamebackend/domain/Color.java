package com.example.chessgamebackend.domain;

/**
 * PUBLIC_INTERFACE
 * Color of chess side.
 */
public enum Color {
    WHITE, BLACK;

    /**
     * PUBLIC_INTERFACE
     * Returns the opposite color.
     * @return other color
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
