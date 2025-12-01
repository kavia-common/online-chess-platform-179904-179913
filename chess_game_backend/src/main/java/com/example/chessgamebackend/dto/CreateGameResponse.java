package com.example.chessgamebackend.dto;

import com.example.chessgamebackend.domain.Color;
import com.example.chessgamebackend.domain.GameStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * Response after creating a game.
 */
public class CreateGameResponse {

    @Schema(description = "Game identifier")
    private UUID gameId;

    @Schema(description = "Current status")
    private GameStatus status;

    @Schema(description = "Side to move")
    private Color turn;

    @Schema(description = "Current position in FEN")
    private String fen;

    public CreateGameResponse() {}

    public CreateGameResponse(UUID gameId, GameStatus status, Color turn, String fen) {
        this.gameId = gameId;
        this.status = status;
        this.turn = turn;
        this.fen = fen;
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

    public String getFen() {
        return fen;
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

    public void setFen(String fen) {
        this.fen = fen;
    }
}
