package tarot.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tarot.application.common.PagedResponse;
import tarot.application.common.result.Result;
import tarot.application.features.chat.commands.sendchatmessage.ChatMessageDto;
import tarot.application.features.chat.commands.sendchatmessage.SendChatMessageCommand;
import tarot.application.features.chat.commands.sendchatmessage.SendChatMessageHandler;
import tarot.application.features.reading.commands.createreading.CreateReadingCommand;
import tarot.application.features.reading.commands.createreading.CreateReadingHandler;
import tarot.application.features.reading.commands.createreading.CreateReadingResponse;
import tarot.application.features.reading.queries.getreadingdetail.GetReadingDetailHandler;
import tarot.application.features.reading.queries.getreadingdetail.ReadingDetailResponse;
import tarot.application.features.reading.queries.getreadinghistory.GetReadingHistoryHandler;
import tarot.application.features.reading.queries.getreadinghistory.ReadingSummaryResponse;

@RestController
@RequestMapping("/api/v1/readings")
@RequiredArgsConstructor
@Tag(name = "2. Reading & AI Consultation", description = "Endpoints for creating readings, getting AI synthesis, and chat interactions")
@CrossOrigin(origins = "*")
public class ReadingController {

    private final CreateReadingHandler createReadingHandler;
    private final SendChatMessageHandler sendChatMessageHandler;
    private final GetReadingDetailHandler getReadingDetailHandler;
    private final GetReadingHistoryHandler getReadingHistoryHandler;

    @PostMapping
    @Operation(summary = "Create reading & AI synthesis", description = "Draws cards, saves reading session, and generates initial AI interpretation")
    public ResponseEntity<?> createReading(@Valid @RequestBody CreateReadingCommand command) {
        Result<CreateReadingResponse> result = createReadingHandler.handle(command);
        if (result.isFailure()) {
            return ResponseEntity.badRequest().body(result.getErrorOrNone());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getDataOrNull());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reading session by ID", description = "Retrieves reading details, drawn cards, and full chat history")
    public ResponseEntity<?> getReadingById(@PathVariable Long id) {
        Result<ReadingDetailResponse> result = getReadingDetailHandler.handle(id);
        if (result.isFailure()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getErrorOrNone());
        }
        return ResponseEntity.ok(result.getDataOrNull());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reading history for a user", description = "Retrieves paginated reading history")
    public ResponseEntity<PagedResponse<ReadingSummaryResponse>> getReadingHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Result<PagedResponse<ReadingSummaryResponse>> result = getReadingHistoryHandler.handle(userId, page, size);
        return ResponseEntity.ok(result.getDataOrNull());
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Send chat message to AI Reader", description = "Sends a follow-up question grounded in the cards on the table")
    public ResponseEntity<?> sendChatMessage(
            @PathVariable Long id,
            @Valid @RequestBody SendChatMessageCommand command
    ) {
        Result<ChatMessageDto> result = sendChatMessageHandler.handle(id, command);
        if (result.isFailure()) {
            return ResponseEntity.badRequest().body(result.getErrorOrNone());
        }
        return ResponseEntity.ok(result.getDataOrNull());
    }
}