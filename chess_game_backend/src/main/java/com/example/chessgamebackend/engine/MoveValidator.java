package com.example.chessgamebackend.engine;

import com.example.chessgamebackend.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Validates moves on a Board. Provides generation of legal moves for check validation.
 * MVP: supports basic piece movement, captures, promotions to queen, basic castling. No en passant.
 */
public class MoveValidator {

    /**
     * PUBLIC_INTERFACE
     * Generate legal moves for side to move on the given board.
     * @param board board position
     * @param color moving color
     * @return list of moves (without promotion variants enumerated except to queen)
     */
    public List<Move> generateLegalMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();
        // For each piece, generate pseudo-legal then filter by king-safety
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = board.getPiece(r, f);
                if (p == null || p.getColor() != color) continue;
                switch (p.getType()) {
                    case PAWN -> genPawn(board, color, r, f, moves);
                    case KNIGHT -> genKnight(board, color, r, f, moves);
                    case BISHOP -> genSlider(board, color, r, f, moves, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
                    case ROOK -> genSlider(board, color, r, f, moves, new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
                    case QUEEN -> genSlider(board, color, r, f, moves, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}});
                    case KING -> genKing(board, color, r, f, moves);
                }
            }
        }
        // Filter by check safety
        List<Move> legal = new ArrayList<>();
        for (Move m : moves) {
            if (isLegalMove(board, color, m)) {
                legal.add(m);
            }
        }
        return legal;
    }

    private void genPawn(Board b, Color c, int r, int f, List<Move> out) {
        int dir = (c == Color.WHITE) ? 1 : -1;
        int startRank = (c == Color.WHITE) ? 1 : 6;
        int promotionRank = (c == Color.WHITE) ? 6 : 1;
        // Forward one
        int r1 = r + dir;
        if (inBoard(r1, f) && b.getPiece(r1, f) == null) {
            addPawnMove(out, r, f, r1, f, c, promotionRank);
            // Double
            int r2 = r + 2*dir;
            if (r == startRank && b.getPiece(r2, f) == null) {
                out.add(new Move(squareOf(r, f), squareOf(r2, f), null));
            }
        }
        // Captures
        for (int df : new int[]{-1, 1}) {
            int cf = f + df;
            int cr = r + dir;
            if (inBoard(cr, cf)) {
                Piece target = b.getPiece(cr, cf);
                if (target != null && target.getColor() != c) {
                    addPawnMove(out, r, f, cr, cf, c, promotionRank);
                }
            }
        }
        // No en passant in MVP
    }

    private void addPawnMove(List<Move> out, int r, int f, int r1, int f1, Color c, int promotionRank) {
        String from = squareOf(r, f);
        String to = squareOf(r1, f1);
        if (r == promotionRank) {
            out.add(new Move(from, to, PieceType.QUEEN)); // MVP: queen only
        } else {
            out.add(new Move(from, to, null));
        }
    }

    private void genKnight(Board b, Color c, int r, int f, List<Move> out) {
        int[][] deltas = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for (int[] d : deltas) {
            int nr = r + d[0], nf = f + d[1];
            if (!inBoard(nr, nf)) continue;
            Piece t = b.getPiece(nr, nf);
            if (t == null || t.getColor() != c) {
                out.add(new Move(squareOf(r,f), squareOf(nr,nf), null));
            }
        }
    }

    private void genSlider(Board b, Color c, int r, int f, List<Move> out, int[][] deltas) {
        for (int[] d : deltas) {
            int nr = r + d[0], nf = f + d[1];
            while (inBoard(nr, nf)) {
                Piece t = b.getPiece(nr, nf);
                if (t == null) {
                    out.add(new Move(squareOf(r,f), squareOf(nr,nf), null));
                } else {
                    if (t.getColor() != c) {
                        out.add(new Move(squareOf(r,f), squareOf(nr,nf), null));
                    }
                    break;
                }
                nr += d[0]; nf += d[1];
            }
        }
    }

    private void genKing(Board b, Color c, int r, int f, List<Move> out) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int df = -1; df <= 1; df++) {
                if (dr == 0 && df == 0) continue;
                int nr = r + dr, nf = f + df;
                if (!inBoard(nr, nf)) continue;
                Piece t = b.getPiece(nr, nf);
                if (t == null || t.getColor() != c) {
                    out.add(new Move(squareOf(r,f), squareOf(nr,nf), null));
                }
            }
        }
        // Basic castling if path empty and not in check over squares (check tested in isLegalMove)
        // White king at e1 (r=0,f=4)
        if (c == Color.WHITE && r == 0 && f == 4) {
            if (b.getPiece(0,5) == null && b.getPiece(0,6) == null) {
                out.add(new Move("e1","g1", null));
            }
            if (b.getPiece(0,3) == null && b.getPiece(0,2) == null && b.getPiece(0,1) == null) {
                out.add(new Move("e1","c1", null));
            }
        }
        // Black king at e8 (r=7,f=4)
        if (c == Color.BLACK && r == 7 && f == 4) {
            if (b.getPiece(7,5) == null && b.getPiece(7,6) == null) {
                out.add(new Move("e8","g8", null));
            }
            if (b.getPiece(7,3) == null && b.getPiece(7,2) == null && b.getPiece(7,1) == null) {
                out.add(new Move("e8","c8", null));
            }
        }
    }

    private boolean inBoard(int r, int f) {
        return r >= 0 && r < 8 && f >= 0 && f < 8;
    }

    private String squareOf(int r, int f) {
        return "" + (char)('a' + f) + (char)('1' + r);
    }

    /**
     * PUBLIC_INTERFACE
     * Check if move is legal, including king safety.
     */
    public boolean isLegalMove(Board board, Color mover, Move move) {
        // Pseudo-legal test by inclusion in generated list (optimization omitted)
        // Apply on a clone and verify king not in check
        Board clone = cloneBoard(board);
        if (!applyPseudoLegal(clone, mover, move)) return false;
        return !isKingInCheck(clone, mover);
    }

    /**
     * PUBLIC_INTERFACE
     * Returns true if color's king is in check.
     */
    public boolean isKingInCheck(Board board, Color color) {
        // Find king
        int kr = -1, kf = -1;
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = board.getPiece(r,f);
                if (p != null && p.getColor() == color && p.getType() == PieceType.KING) {
                    kr = r; kf = f;
                }
            }
        }
        if (kr == -1) return true; // no king found => illegal
        Color opp = color.opposite();
        List<Move> oppMoves = new ArrayList<>();
        // Generate opponent pseudo moves (king safety not needed)
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = board.getPiece(r, f);
                if (p == null || p.getColor() != opp) continue;
                switch (p.getType()) {
                    case PAWN -> genPawn(board, opp, r, f, oppMoves);
                    case KNIGHT -> genKnight(board, opp, r, f, oppMoves);
                    case BISHOP -> genSlider(board, opp, r, f, oppMoves, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
                    case ROOK -> genSlider(board, opp, r, f, oppMoves, new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
                    case QUEEN -> genSlider(board, opp, r, f, oppMoves, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}});
                    case KING -> genKing(board, opp, r, f, oppMoves);
                }
            }
        }
        String kingSquare = "" + (char)('a' + kf) + (char)('1' + kr);
        for (Move m : oppMoves) {
            if (m.getTo().equals(kingSquare)) return true;
        }
        return false;
    }

    private boolean applyPseudoLegal(Board board, Color mover, Move move) {
        int ff = Board.fileOf(move.getFrom());
        int fr = Board.rankOf(move.getFrom());
        int tf = Board.fileOf(move.getTo());
        int tr = Board.rankOf(move.getTo());
        Piece moved = board.getPiece(fr, ff);
        if (moved == null || moved.getColor() != mover) return false;

        // Basic movement rules check
        List<Move> pseudo = new ArrayList<>();
        switch (moved.getType()) {
            case PAWN -> genPawn(board, mover, fr, ff, pseudo);
            case KNIGHT -> genKnight(board, mover, fr, ff, pseudo);
            case BISHOP -> genSlider(board, mover, fr, ff, pseudo, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
            case ROOK -> genSlider(board, mover, fr, ff, pseudo, new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
            case QUEEN -> genSlider(board, mover, fr, ff, pseudo, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}});
            case KING -> genKing(board, mover, fr, ff, pseudo);
        }
        boolean found = false;
        for (Move m : pseudo) {
            if (m.getTo().equals(move.getTo())) {
                if ((m.getPromotion() == null && move.getPromotion() == null) ||
                        (m.getPromotion() != null && move.getPromotion() != null)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) return false;

        // Execute move (with basic castling rook move)
        boolean wasCaptureOrPawn = (board.getPiece(tr, tf) != null) || moved.getType() == PieceType.PAWN;
        board.setPiece(fr, ff, null);
        // Castling rook handling
        if (moved.getType() == PieceType.KING && Math.abs(tf - ff) == 2 && fr == tr) {
            // king-side or queen-side
            if (tf == 6) { // g-file
                // move rook h -> f
                Piece rook = board.getPiece(fr, 7);
                board.setPiece(fr, 7, null);
                board.setPiece(fr, 5, rook);
            } else if (tf == 2) {
                Piece rook = board.getPiece(fr, 0);
                board.setPiece(fr, 0, null);
                board.setPiece(fr, 3, rook);
            }
        }
        // Place moved (promotion to queen only in MVP)
        Piece placed = moved;
        if (moved.getType() == PieceType.PAWN && move.getPromotion() != null) {
            placed = new Piece(PieceType.QUEEN, mover);
        }
        board.setPiece(tr, tf, placed);

        board.noteKingOrRookMove(fr, ff, moved);
        board.setSideToMove(board.getSideToMove().opposite());
        board.incrementMove(mover, wasCaptureOrPawn);
        return true;
    }

    private Board cloneBoard(Board b) {
        Board c = new Board();
        c.loadFEN(b.toFEN());
        return c;
    }
}
