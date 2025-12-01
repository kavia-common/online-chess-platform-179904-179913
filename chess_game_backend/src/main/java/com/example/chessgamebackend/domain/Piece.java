package com.example.chessgamebackend.domain;

import java.util.Objects;

/**
 * PUBLIC_INTERFACE
 * Immutable representation of a chess piece.
 */
public final class Piece {
    private final PieceType type;
    private final Color color;

    public Piece(PieceType type, Color color) {
        this.type = Objects.requireNonNull(type, "type");
        this.color = Objects.requireNonNull(color, "color");
    }

    public PieceType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public boolean is(Color c) {
        return color == c;
    }

    public boolean isType(PieceType t) {
        return type == t;
    }

    @Override
    public String toString() {
        char c;
        switch (type) {
            case KING -> c = 'k';
            case QUEEN -> c = 'q';
            case ROOK -> c = 'r';
            case BISHOP -> c = 'b';
            case KNIGHT -> c = 'n';
            case PAWN -> c = 'p';
            default -> c = '?';
        }
        return color == Color.WHITE ? ("" + Character.toUpperCase(c)) : ("" + c);
    }
}
