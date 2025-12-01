package com.example.chessgamebackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PUBLIC_INTERFACE
 * Request to create a new game.
 */
public class CreateGameRequest {

    @Schema(description = "White player identifier", example = "alice123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String whitePlayer;

    @Schema(description = "Black player identifier (optional). If omitted another player may join later.", example = "bob456")
    private String blackPlayer;

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }
}
