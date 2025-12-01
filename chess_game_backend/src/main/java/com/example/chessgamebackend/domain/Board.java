package com.example.chessgamebackend.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * PUBLIC_INTERFACE
 * Board representation with minimal FEN support for piece placement and active color.
 * MVP caveats:
 * - En passant is not tracked (always '-').
 * - Castling availability is recomputed simply based on king/rook starting positions and move history approximation.
 * - Halfmove/fullmove counters are tracked minimally.
 */
public class Board {

    private final Piece[][] squares; // [rank][file] 0..7, rank 0 is 8th rank (top), file 0 is 'a'
    private Color sideToMove;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteKingsideRookMoved = false;
    private boolean whiteQueensideRookMoved = false;
    private boolean blackKingsideRookMoved = false;
    private boolean blackQueensideRookMoved = false;
    private int halfmoveClock = 0;
    private int fullmoveNumber = 1;

    public Board() {
        this.squares = new Piece[8][8];
    }

    /**
     * PUBLIC_INTERFACE
     * Initializes board from the given FEN string (only standard fields).
     * @param fen valid FEN
     */
    public void loadFEN(String fen) {
        String[] parts = fen.trim().split("\\s+");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid FEN: " + fen);
        }
        // clear
        for (Piece[] row : squares) Arrays.fill(row, null);

        String placement = parts[0];
        String active = parts[1];

        String[] ranks = placement.split("/");
        if (ranks.length != 8) {
            throw new IllegalArgumentException("Invalid FEN ranks: " + placement);
        }
        for (int r = 0; r < 8; r++) {
            int file = 0;
            String rank = ranks[r];
            for (int i = 0; i < rank.length(); i++) {
                char c = rank.charAt(i);
                if (Character.isDigit(c)) {
                    file += (c - '0');
                } else {
                    Color color = Character.isUpperCase(c) ? Color.WHITE : Color.BLACK;
                    PieceType type = switch (Character.toLowerCase(c)) {
                        case 'k' -> PieceType.KING;
                        case 'q' -> PieceType.QUEEN;
                        case 'r' -> PieceType.ROOK;
                        case 'b' -> PieceType.BISHOP;
                        case 'n' -> PieceType.KNIGHT;
                        case 'p' -> PieceType.PAWN;
                        default -> throw new IllegalArgumentException("Bad piece: " + c);
                    };
                    setPiece(7 - r, file, new Piece(type, color)); // convert rank: fen top is rank 8
                    file++;
                }
            }
            if (file != 8) throw new IllegalArgumentException("Bad rank width: " + rank);
        }

        this.sideToMove = "w".equalsIgnoreCase(active) ? Color.WHITE : Color.BLACK;

        // Optional fields
        if (parts.length > 4) {
            try {
                this.halfmoveClock = Integer.parseInt(parts[4]);
            } catch (Exception ignored) { this.halfmoveClock = 0; }
        }
        if (parts.length > 5) {
            try {
                this.fullmoveNumber = Integer.parseInt(parts[5]);
            } catch (Exception ignored) { this.fullmoveNumber = 1; }
        }

        // Movement flags approximated by starting positions
        inferMovementFlagsFromStartingSquares();
    }

    /**
     * PUBLIC_INTERFACE
     * Returns a FEN string for the current position.
     * @return FEN
     */
    public String toFEN() {
        StringBuilder sb = new StringBuilder();
        for (int r = 7; r >= 0; r--) {
            int empty = 0;
            for (int f = 0; f < 8; f++) {
                Piece p = squares[r][f];
                if (p == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        sb.append(empty);
                        empty = 0;
                    }
                    String s = p.toString();
                    sb.append(s);
                }
            }
            if (empty > 0) sb.append(empty);
            if (r > 0) sb.append('/');
        }
        sb.append(' ');
        sb.append(sideToMove == Color.WHITE ? 'w' : 'b');
        sb.append(' ');
        String castling = computeCastlingRights();
        sb.append(castling.isEmpty() ? "-" : castling);
        sb.append(' ');
        sb.append('-'); // en passant square not tracked in MVP
        sb.append(' ');
        sb.append(halfmoveClock);
        sb.append(' ');
        sb.append(fullmoveNumber);
        return sb.toString();
    }

    private String computeCastlingRights() {
        StringBuilder sb = new StringBuilder();
        if (!whiteKingMoved) {
            if (!whiteKingsideRookMoved && getPiece(0, 7) != null && getPiece(0,7).isType(PieceType.ROOK) && getPiece(0,7).is(Color.WHITE))
                sb.append('K');
            if (!whiteQueensideRookMoved && getPiece(0, 0) != null && getPiece(0,0).isType(PieceType.ROOK) && getPiece(0,0).is(Color.WHITE))
                sb.append('Q');
        }
        if (!blackKingMoved) {
            if (!blackKingsideRookMoved && getPiece(7, 7) != null && getPiece(7,7).isType(PieceType.ROOK) && getPiece(7,7).is(Color.BLACK))
                sb.append('k');
            if (!blackQueensideRookMoved && getPiece(7, 0) != null && getPiece(7,0).isType(PieceType.ROOK) && getPiece(7,0).is(Color.BLACK))
                sb.append('q');
        }
        return sb.toString();
    }

    private void inferMovementFlagsFromStartingSquares() {
        // If kings are not on starting squares, mark moved
        Piece wk = getPiece(0, 4);
        if (wk == null || !wk.isType(PieceType.KING) || !wk.is(Color.WHITE)) whiteKingMoved = true;
        Piece bk = getPiece(7, 4);
        if (bk == null || !bk.isType(PieceType.KING) || !bk.is(Color.BLACK)) blackKingMoved = true;

        Piece wrr = getPiece(0, 7);
        if (wrr == null || !wrr.isType(PieceType.ROOK) || !wrr.is(Color.WHITE)) whiteKingsideRookMoved = true;
        Piece wrl = getPiece(0, 0);
        if (wrl == null || !wrl.isType(PieceType.ROOK) || !wrl.is(Color.WHITE)) whiteQueensideRookMoved = true;

        Piece brr = getPiece(7, 7);
        if (brr == null || !brr.isType(PieceType.ROOK) || !brr.is(Color.BLACK)) blackKingsideRookMoved = true;
        Piece brl = getPiece(7, 0);
        if (brl == null || !brl.isType(PieceType.ROOK) || !brl.is(Color.BLACK)) blackQueensideRookMoved = true;
    }

    /**
     * PUBLIC_INTERFACE
     * Initialize to the standard chess starting position.
     */
    public void initStartingPosition() {
        loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Piece getPiece(int rank, int file) {
        return squares[rank][file];
    }

    public void setPiece(int rank, int file, Piece piece) {
        squares[rank][file] = piece;
    }

    public static int fileOf(String square) {
        return square.toLowerCase(Locale.ROOT).charAt(0) - 'a';
    }

    public static int rankOf(String square) {
        return (square.charAt(1) - '1');
    }

    public Color getSideToMove() {
        return sideToMove;
    }

    public void setSideToMove(Color sideToMove) {
        this.sideToMove = sideToMove;
    }

    public void noteKingOrRookMove(int fromRank, int fromFile, Piece moved) {
        if (moved.isType(PieceType.KING)) {
            if (moved.is(Color.WHITE)) whiteKingMoved = true; else blackKingMoved = true;
        } else if (moved.isType(PieceType.ROOK)) {
            if (moved.is(Color.WHITE)) {
                if (fromRank == 0 && fromFile == 0) whiteQueensideRookMoved = true;
                if (fromRank == 0 && fromFile == 7) whiteKingsideRookMoved = true;
            } else {
                if (fromRank == 7 && fromFile == 0) blackQueensideRookMoved = true;
                if (fromRank == 7 && fromFile == 7) blackKingsideRookMoved = true;
            }
        }
    }

    public void incrementMove(Color mover, boolean wasCaptureOrPawn) {
        if (mover == Color.BLACK) {
            fullmoveNumber += 1;
        }
        halfmoveClock = wasCaptureOrPawn ? 0 : halfmoveClock + 1;
    }
}
