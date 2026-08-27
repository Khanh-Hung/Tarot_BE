package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.features.card.queries.getcardsbydeck.GetCardsByDeckHandler;
import tarot.application.features.deck.queries.getdecks.GetDecksHandler;
import tarot.domain.enums.DeckCode;
import tarot.presentation.common.ActionResult;

@RestController
@RequestMapping("/api/v1/decks")
@Tag(name = "1. Deck & Card Catalog", description = "Endpoints for browsing decks and tarot cards")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DeckController {

    private final GetDecksHandler getDecksHandler;
    private final GetCardsByDeckHandler getCardsByDeckHandler;

    @GetMapping
    @Operation(summary = "Get all tarot decks", description = "Returns available deck themes")
    public ResponseEntity<?> getAllDecks() {
        return ActionResult.from(getDecksHandler.handle());
    }

    @GetMapping("/{deckCode}/cards")
    @Operation(summary = "Get cards by deck code", description = "Returns all cards belonging to a specific deck")
    public ResponseEntity<?> getCardsByDeck(@PathVariable DeckCode deckCode) {
        return ActionResult.from(getCardsByDeckHandler.handle(deckCode));
    }
}