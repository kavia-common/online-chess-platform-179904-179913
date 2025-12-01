package com.example.chessgamebackend.api;

import com.example.chessgamebackend.domain.*;
import com.example.chessgamebackend.dto.CreateGameRequest;
import com.example.chessgamebackend.dto.CreateGameResponse;
import com.example.chessgamebackend.dto.GameStateResponse;
import com.example.chessgamebackend.dto.MakeMoveRequest;
import com.example.chessgamebackend.engine.MoveValidator;
import com.example.chessgamebackend.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PUBLIC_INTERFACE
 * REST API for chess games under /api/chess.
 */
@RestController
@RequestMapping(value = "/api/chess", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Chess", description = "Endpoints to create games, join, query state, make moves, and resign.")
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    /**
     * PUBLIC_INTERFACE
     * Creates a new game.
     */
    @PostMapping(value = "/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create game", description = "Creates a new chess game with white and optional black player.")
    @ApiResponse(responseCode = "200", description = "Game created")
    public CreateGameResponse createGame(@RequestBody CreateGameRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.getWhitePlayer() == null || request.getWhitePlayer().isBlank()) {
            throw new IllegalArgumentException("whitePlayer is required");
        }
        Game game = service.createGame(request.getWhitePlayer(), request.getBlackPlayer());
        return new CreateGameResponse(game.getId(), game.getStatus(), game.getTurn(), game.getCurrentFEN());
    }

    /**
     * PUBLIC_INTERFACE
     * Join a game as black if available.
     */
    @PostMapping("/games/{id}/join")
    @Operation(summary = "Join game", description = "Joins an existing game as black if the slot is empty.")
    public GameStateResponse joinGame(
            @PathVariable("id") UUID id,
            @RequestParam("playerId") String playerId
    ) {
        Game g = service.joinGame(id, playerId);
        return toState(g, null);
    }

    /**
     * PUBLIC_INTERFACE
     * Get current game state.
     */
    @GetMapping("/games/{id}")
    @Operation(summary = "Get game state", description = "Returns game state for the given game id.")
    @ApiResponse(responseCode = "200", description = "Game state", content = @Content(schema = @Schema(implementation = GameStateResponse.class)))
    public GameStateResponse getGame(@PathVariable("id") UUID id) {
        Game g = service.getGame(id);
        return toState(g, null);
    }

    /**
     * PUBLIC_INTERFACE
     * Make a move.
     */
    @PostMapping(value = "/games/{id}/moves", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Make move", description = "Makes a move if legal and updates game position.")
    public GameStateResponse makeMove(
            @PathVariable("id") UUID id,
            @RequestBody MakeMoveRequest req
    ) {
        Objects.requireNonNull(req, "request");
        if (req.getFrom() == null || req.getTo() == null || req.getPlayerId() == null) {
            throw new IllegalArgumentException("from, to, and playerId are required");
        }
        PieceType promo = null;
        if (req.getPromotion() != null && !req.getPromotion().isBlank()) {
            promo = PieceType.valueOf(req.getPromotion().toUpperCase(Locale.ROOT));
            if (promo != PieceType.QUEEN) {
                throw new IllegalArgumentException("Only queen promotion supported in MVP");
            }
        }
        Move move = new Move(req.getFrom(), req.getTo(), promo);
        Game g = service.makeMove(id, req.getPlayerId(), move);
        return toState(g, move);
    }

    /**
     * PUBLIC_INTERFACE
     * Resign a game.
     */
    @PostMapping("/games/{id}/resign")
    @Operation(summary = "Resign", description = "Resign the game by the calling player.")
    public GameStateResponse resign(
            @PathVariable("id") UUID id,
            @RequestParam("playerId") String playerId
    ) {
        Game g = service.resign(id, playerId);
        return toState(g, null);
    }

    /**
     * PUBLIC_INTERFACE
     * Optional helper: get legal moves from a square.
     */
    @GetMapping("/games/{id}/legal-moves")
    @Operation(summary = "Legal moves", description = "Returns legal move targets from a given square.", responses = {
            @ApiResponse(responseCode = "200", description = "List of target squares")
    })
    public List<String> legalMoves(
            @PathVariable("id") UUID id,
            @RequestParam("from") @Parameter(description = "Source square like e2") String from
    ) {
        Game g = service.getGame(id);
        Board b = new Board();
        b.loadFEN(g.getCurrentFEN());
        MoveValidator mv = new MoveValidator();
        List<Move> legals = mv.generateLegalMoves(b, b.getSideToMove());
        return legals.stream()
                .filter(m -> m.getFrom().equalsIgnoreCase(from))
                .map(Move::getTo)
                .sorted()
                .collect(Collectors.toList());
    }

    private GameStateResponse toState(Game g, Move last) {
        // Extract fullmove number from FEN (last number)
        String[] fenParts = g.getCurrentFEN().split("\\s+");
        int moves = 1;
        if (fenParts.length >= 6) {
            try {
                moves = Integer.parseInt(fenParts[5]);
            } catch (Exception ignored) {}
        }
        String lastStr = last != null ? last.toString() : null;
        return new GameStateResponse(
                g.getId(),
                g.getStatus(),
                g.getTurn(),
                g.getWhitePlayerId(),
                g.getBlackPlayerId(),
                g.getCurrentFEN(),
                lastStr,
                moves,
                g.getUpdatedAt()
        );
    }
}
