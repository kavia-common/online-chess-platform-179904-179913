package com.example.chessgamebackend.domain;

import java.util.Locale;
import java.util.Objects;

/**
 * PUBLIC_INTERFACE
 * A simple move representation: from square, to square, and optional promotion piece type.
 */
public final class Move {
    private final String from;
    private final String to;
    private final PieceType promotion; // null means no promotion

    public Move(String from, String to, PieceType promotion) {
        this.from = normalizeSquare(Objects.requireNonNull(from, "from"));
        this.to = normalizeSquare(Objects.requireNonNull(to, "to"));
        this.promotion = promotion;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public PieceType getPromotion() {
        return promotion;
    }

    private static String normalizeSquare(String s) {
        s = s.toLowerCase(Locale.ROOT);
        if (s.length() != 2) {
            throw new IllegalArgumentException("Square must be like e2");
        }
        char f = s.charAt(0);
        char r = s.charAt(1);
        if (f < 'a' || f > 'h' || r < '1' || r > '8') {
            throw new IllegalArgumentException("Invalid square: " + s);
        }
        return s;
    }

    @Override
    public String toString() {
        return from + "-" + to + (promotion != null ? "=" + promotion : "");
    }
}
