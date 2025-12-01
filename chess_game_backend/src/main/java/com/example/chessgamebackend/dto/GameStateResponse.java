package com.example.chessgamebackend.dto;

import com.example.chessgamebackend.domain.Color;
import com.example.chessgamebackend.domain.GameStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * Response describing game state.
 */
public class GameStateResponse {

    @Schema(description = "Game identifier")
    private UUID gameId;

    @Schema(description = "Game status")
    private GameStatus status;

    @Schema(description = "Side to move")
    private Color turn;

    @Schema(description = "White player ID")
    private String whitePlayer;

    @Schema(description = "Black player ID")
    private String blackPlayer;

    @Schema(description = "Current FEN position")
    private String fen;

    @Schema(description = "Last move in simple form (if tracked, may be null)")
    private String lastMove;

    @Schema(description = "Moves count (fullmove number)")
    private int movesCount;

    @Schema(description = "Updated timestamp")
    private Instant updatedAt;

    public GameStateResponse() {}

    public GameStateResponse(UUID gameId, GameStatus status, Color turn, String whitePlayer, String blackPlayer, String fen, String lastMove, int movesCount, Instant updatedAt) {
        this.gameId = gameId;
        this.status = status;
        this.turn = turn;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.fen = fen;
        this.lastMove = lastMove;
        this.movesCount = movesCount;
        this.updatedAt = updatedAt;
    }

    public UUID getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Color getTurn() {
        return turn;
    }

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public String getFen() {
        return fen;
    }

    public String getLastMove() {
        return lastMove;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setTurn(Color turn) {
        this.turn = turn;
    }

    public void setWhitePlayer(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public void setMovesCount(int movesCount) {
        this.movesCount = movesCount;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
