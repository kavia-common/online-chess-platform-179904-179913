package com.example.chessgamebackend.service;

import com.example.chessgamebackend.domain.*;
import com.example.chessgamebackend.engine.ChessEngine;
import com.example.chessgamebackend.exception.GameNotFoundException;
import com.example.chessgamebackend.exception.InvalidMoveException;
import com.example.chessgamebackend.exception.NotYourTurnException;
import com.example.chessgamebackend.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * Service implementing core chess use-cases.
 */
@Service
@Transactional
public class GameService {

    private final GameRepository repository;
    private final ChessEngine engine = new ChessEngine();

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    /**
     * PUBLIC_INTERFACE
     * Creates a new game with the given white and optional black player IDs.
     */
    public Game createGame(String whitePlayer, String blackPlayer) {
        Game g = new Game();
        g.setCurrentFEN(engine.startingFEN());
        g.setStatus(GameStatus.ONGOING);
        g.setTurn(Color.WHITE);
        g.setWhitePlayerId(whitePlayer);
        g.setBlackPlayerId(blackPlayer);
        g.setMoveHistory("[]");
        return repository.save(g);
    }

    /**
     * PUBLIC_INTERFACE
     * Allows a player to join as black if slot empty.
     */
    public Game joinGame(UUID id, String playerId) {
        Game g = getGame(id);
        if (g.getBlackPlayerId() == null || g.getBlackPlayerId().isBlank()) {
            g.setBlackPlayerId(playerId);
            return repository.save(g);
        }
        return g;
    }

    /**
     * PUBLIC_INTERFACE
     * Retrieves a game by id.
     */
    @Transactional(readOnly = true)
    public Game getGame(UUID id) {
        Optional<Game> opt = repository.findById(id);
        return opt.orElseThrow(() -> new GameNotFoundException("Game not found: " + id));
    }

    /**
     * PUBLIC_INTERFACE
     * Applies a move for the given player.
     */
    public Game makeMove(UUID id, String playerId, Move move) {
        Game g = getGame(id);
        if (g.getStatus() != GameStatus.ONGOING) {
            throw new InvalidMoveException("Game is not ongoing.");
        }
        // Turn enforcement
        boolean isWhite = playerId != null && playerId.equals(g.getWhitePlayerId());
        boolean isBlack = playerId != null && playerId.equals(g.getBlackPlayerId());
        if (g.getTurn() == Color.WHITE && !isWhite) throw new NotYourTurnException("It's White's turn.");
        if (g.getTurn() == Color.BLACK && !isBlack) throw new NotYourTurnException("It's Black's turn.");

        ChessEngine.Result res;
        try {
            res = engine.applyMove(g.getCurrentFEN(), move);
        } catch (IllegalArgumentException ex) {
            throw new InvalidMoveException(ex.getMessage());
        }

        g.setCurrentFEN(res.fen());
        g.setTurn(res.nextTurn());
        g.setStatus(res.status());
        // append to history (very basic)
        String entry = String.format("{\"from\":\"%s\",\"to\":\"%s\",\"promotion\":\"%s\"}", move.getFrom(), move.getTo(), move.getPromotion());
        String hist = g.getMoveHistory();
        if (hist == null || hist.isBlank()) hist = "[]";
        if (hist.endsWith("]")) {
            if (hist.length() == 2) {
                hist = "[" + entry + "]";
            } else {
                hist = hist.substring(0, hist.length() - 1) + "," + entry + "]";
            }
        }
        g.setMoveHistory(hist);
        return repository.save(g);
    }

    /**
     * PUBLIC_INTERFACE
     * Resigns the game by the given player.
     */
    public Game resign(UUID id, String playerId) {
        Game g = getGame(id);
        if (g.getStatus() != GameStatus.ONGOING) {
            return g;
        }
        g.setStatus(GameStatus.RESIGNED);
        return repository.save(g);
    }
}
