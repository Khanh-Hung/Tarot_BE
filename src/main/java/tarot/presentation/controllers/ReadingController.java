package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.features.chat.commands.sendchatmessage.SendChatMessageCommand;
import tarot.application.features.chat.commands.sendchatmessage.SendChatMessageHandler;
import tarot.application.features.reading.commands.createreading.CreateReadingCommand;
import tarot.application.features.reading.commands.createreading.CreateReadingHandler;
import tarot.application.features.reading.queries.getreadingdetail.GetReadingDetailHandler;
import tarot.application.features.reading.queries.getreadinghistory.GetReadingHistoryHandler;
import tarot.presentation.common.ActionResult;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/readings")
@Tag(name = "2. Reading & AI Consultation", description = "Endpoints for creating readings, getting AI synthesis, and chat interactions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReadingController {

    private final CreateReadingHandler createReadingHandler;
    private final GetReadingDetailHandler getReadingDetailHandler;
    private final GetReadingHistoryHandler getReadingHistoryHandler;
    private final tarot.application.features.reading.queries.getenergyinsights.GetEnergyInsightsHandler getEnergyInsightsHandler;
    private final SendChatMessageHandler sendChatMessageHandler;
    private final tarot.application.features.reading.queries.getsuggestedquestions.GetSuggestedQuestionsHandler getSuggestedQuestionsHandler;
    private final tarot.application.features.reading.commands.generateconclusion.GenerateConclusionHandler generateConclusionHandler;

    @GetMapping("/suggestions")
    @Operation(summary = "Generate AI question suggestions", description = "Generates 3 diverse, insightful Tarot inquiry suggestions using AI")
    public ResponseEntity<?> getSuggestedQuestions(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String zodiac
    ) {
        var result = getSuggestedQuestionsHandler.handle(
                new tarot.application.features.reading.queries.getsuggestedquestions.GetSuggestedQuestionsQuery(topic, zodiac)
        );
        return org.springframework.http.ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noCache().noStore().mustRevalidate())
                .body(result.getDataOrNull());
    }

    @PostMapping
    @Operation(summary = "Create reading & AI synthesis", description = "Draws cards, saves reading session, and generates initial AI interpretation")
    public ResponseEntity<?> createReading(@Valid @RequestBody CreateReadingCommand command) {
        return ActionResult.from(createReadingHandler.handle(command), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reading session by ID", description = "Retrieves reading details, drawn cards, and full chat history")
    public ResponseEntity<?> getReadingById(@PathVariable UUID id) {
        return ActionResult.from(getReadingDetailHandler.handle(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reading history for a user", description = "Retrieves paginated reading history")
    public ResponseEntity<?> getReadingHistory(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ActionResult.from(getReadingHistoryHandler.handle(userId, page, size));
    }

    @GetMapping("/user/{userId}/insights")
    @Operation(summary = "Get Tarot energy insights and statistics for a user", description = "Calculates elemental balance, most frequent cards, and spiritual energy trends")
    public ResponseEntity<?> getEnergyInsights(@PathVariable UUID userId) {
        return ActionResult.from(getEnergyInsightsHandler.handle(userId));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Send chat message to AI Reader", description = "Sends a follow-up question grounded in the cards on the table")
    public ResponseEntity<?> sendChatMessage(
            @PathVariable UUID id,
            @Valid @RequestBody SendChatMessageCommand command
    ) {
        return ActionResult.from(sendChatMessageHandler.handle(id, command));
    }

    @PostMapping("/{id}/conclude")
    @Operation(summary = "Generate 1-sentence AI conclusion for story sharing", description = "Synthesizes reading into 1 punchy conclusion quote")
    public ResponseEntity<?> generateConclusion(@PathVariable UUID id) {
        return ActionResult.from(generateConclusionHandler.handle(id));
    }
}