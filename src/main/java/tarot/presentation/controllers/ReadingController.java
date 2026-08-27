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

@RestController
@RequestMapping("/api/v1/readings")
@Tag(name = "2. Reading & AI Consultation", description = "Endpoints for creating readings, getting AI synthesis, and chat interactions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReadingController {

    private final CreateReadingHandler createReadingHandler;
    private final GetReadingDetailHandler getReadingDetailHandler;
    private final GetReadingHistoryHandler getReadingHistoryHandler;
    private final SendChatMessageHandler sendChatMessageHandler;

    @PostMapping
    @Operation(summary = "Create reading & AI synthesis", description = "Draws cards, saves reading session, and generates initial AI interpretation")
    public ResponseEntity<?> createReading(@Valid @RequestBody CreateReadingCommand command) {
        return ActionResult.from(createReadingHandler.handle(command), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reading session by ID", description = "Retrieves reading details, drawn cards, and full chat history")
    public ResponseEntity<?> getReadingById(@PathVariable Long id) {
        return ActionResult.from(getReadingDetailHandler.handle(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reading history for a user", description = "Retrieves paginated reading history")
    public ResponseEntity<?> getReadingHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ActionResult.from(getReadingHistoryHandler.handle(userId, page, size));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Send chat message to AI Reader", description = "Sends a follow-up question grounded in the cards on the table")
    public ResponseEntity<?> sendChatMessage(
            @PathVariable Long id,
            @Valid @RequestBody SendChatMessageCommand command
    ) {
        return ActionResult.from(sendChatMessageHandler.handle(id, command));
    }
}