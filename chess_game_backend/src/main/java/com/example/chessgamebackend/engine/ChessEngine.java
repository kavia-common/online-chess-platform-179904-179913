package com.example.chessgamebackend.engine;

import com.example.chessgamebackend.domain.*;

import java.util.List;

/**
 * PUBLIC_INTERFACE
 * High-level chess engine operations: initialize, validate/apply moves, and detect basic end states.
 */
public class ChessEngine {

    private final MoveValidator validator = new MoveValidator();

    /**
     * PUBLIC_INTERFACE
     * @return starting position FEN
     */
    public String startingFEN() {
        Board b = new Board();
        b.initStartingPosition();
        return b.toFEN();
    }

    /**
     * PUBLIC_INTERFACE
     * Applies a move to a FEN, enforcing legality and returning new FEN and game status.
     * @param fen current FEN
     * @param move move to apply
     * @return result containing new fen, next turn, and status
     */
    public Result applyMove(String fen, Move move) {
        Board board = new Board();
        board.loadFEN(fen);
        Color mover = board.getSideToMove();

        if (!validator.isLegalMove(board, mover, move)) {
            throw new IllegalArgumentException("Illegal move: " + move);
        }
        // Apply move on board using validator's internal pseudo apply (replay by generating and then apply)
        // We use a tiny trick: validator.isLegalMove clones and applies; we need to apply now on real board
        // Re-apply pseudo on board directly (duplicated logic maintained in validator):
        // We can call a small helper here: generate legal and find exact, then re-load new FEN by simulating.
        List<Move> legal = validator.generateLegalMoves(board, mover);
        boolean found = false;
        for (Move lm : legal) {
            if (lm.getFrom().equals(move.getFrom()) && lm.getTo().equals(move.getTo())) {
                move = lm; // use canonical (promotion if needed)
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Illegal move (not found in legal set): " + move);

        // Now re-create by executing on a fresh board by stepping: emulate by loading FEN from validator clone
        // Simple approach: find clone after move with isLegalMove check which clones and applies; capture from there.
        Board after = new Board();
        after.loadFEN(fen);
        // apply same pseudo/logic as validator by regenerating on a temporary validator clone
        // Use a small internal executor
        internalApply(after, mover, move);

        String newFen = after.toFEN();
        Color next = after.getSideToMove();

        // Determine status
        GameStatus status = GameStatus.ONGOING;
        boolean oppInCheck = validator.isKingInCheck(after, next);
        List<Move> oppMoves = validator.generateLegalMoves(after, next);
        if (oppMoves.isEmpty()) {
            status = oppInCheck ? GameStatus.CHECKMATE : GameStatus.STALEMATE;
        }

        return new Result(newFen, next, status);
    }

    private void internalApply(Board board, Color mover, Move move) {
        // Repeat a small version by asking validator for legal and then reproducing result squares
        int ff = Board.fileOf(move.getFrom());
        int fr = Board.rankOf(move.getFrom());
        int tf = Board.fileOf(move.getTo());
        int tr = Board.rankOf(move.getTo());
        Piece moved = board.getPiece(fr, ff);
        boolean wasCaptureOrPawn = (board.getPiece(tr, tf) != null) || (moved != null && moved.getType() == PieceType.PAWN);

        board.setPiece(fr, ff, null);

        if (moved != null && moved.getType() == PieceType.KING && Math.abs(tf - ff) == 2 && fr == tr) {
            if (tf == 6) {
                Piece rook = board.getPiece(fr, 7);
                board.setPiece(fr, 7, null);
                board.setPiece(fr, 5, rook);
            } else if (tf == 2) {
                Piece rook = board.getPiece(fr, 0);
                board.setPiece(fr, 0, null);
                board.setPiece(fr, 3, rook);
            }
        }
        Piece placed = moved;
        if (moved != null && moved.getType() == PieceType.PAWN && move.getPromotion() != null) {
            placed = new Piece(PieceType.QUEEN, mover);
        }
        board.setPiece(tr, tf, placed);
        if (moved != null) board.noteKingOrRookMove(fr, ff, moved);
        board.setSideToMove(board.getSideToMove().opposite());
        board.incrementMove(mover, wasCaptureOrPawn);
    }

    /**
     * PUBLIC_INTERFACE
     * Result record carrying new position and status.
     */
    public record Result(String fen, Color nextTurn, GameStatus status) {}
}
