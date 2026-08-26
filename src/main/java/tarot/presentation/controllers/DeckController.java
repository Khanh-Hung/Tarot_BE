package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.features.card.queries.getcardsbydeck.CardDto;
import tarot.application.features.card.queries.getcardsbydeck.GetCardsByDeckHandler;
import tarot.application.features.deck.queries.getdecks.DeckDto;
import tarot.application.features.deck.queries.getdecks.GetDecksHandler;
import tarot.application.common.result.Result;
import tarot.domain.enums.DeckCode;

import java.util.List;

@RestController
@RequestMapping("/api/v1/decks")
@RequiredArgsConstructor
@Tag(name = "1. Deck & Card Catalog", description = "Endpoints for browsing decks and tarot cards")
@CrossOrigin(origins = "*")
public class DeckController {

    private final GetDecksHandler getDecksHandler;
    private final GetCardsByDeckHandler getCardsByDeckHandler;

    @GetMapping
    @Operation(summary = "Get all tarot decks", description = "Returns available deck themes")
    public ResponseEntity<List<DeckDto>> getAllDecks() {
        Result<List<DeckDto>> result = getDecksHandler.handle();
        return ResponseEntity.ok(result.getDataOrNull());
    }

    @GetMapping("/{deckCode}/cards")
    @Operation(summary = "Get cards by deck code", description = "Returns all cards belonging to a specific deck")
    public ResponseEntity<List<CardDto>> getCardsByDeck(@PathVariable DeckCode deckCode) {
        Result<List<CardDto>> result = getCardsByDeckHandler.handle(deckCode);
        return ResponseEntity.ok(result.getDataOrNull());
    }
}