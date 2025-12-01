package com.example.chessgamebackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PUBLIC_INTERFACE
 * Request to make a move.
 */
public class MakeMoveRequest {

    @Schema(description = "From square", example = "e2", requiredMode = Schema.RequiredMode.REQUIRED)
    private String from;

    @Schema(description = "To square", example = "e4", requiredMode = Schema.RequiredMode.REQUIRED)
    private String to;

    @Schema(description = "Promotion piece type string (QUEEN only in MVP).", example = "QUEEN")
    private String promotion;

    @Schema(description = "Player identifier attempting the move", example = "alice123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String playerId;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPromotion() {
        return promotion;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
