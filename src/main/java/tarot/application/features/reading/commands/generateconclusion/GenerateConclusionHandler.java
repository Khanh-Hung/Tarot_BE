package tarot.application.features.reading.commands.generateconclusion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.core.Reading;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.persistence.repositories.core.ReadingRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerateConclusionHandler {

    private final ReadingRepository readingRepository;
    private final AiConsultationService aiConsultationService;

    public Result<ConclusionResponse> handle(UUID readingId) {
        Reading reading = readingRepository.findById(readingId).orElse(null);
        if (reading == null) {
            return Result.failure(new Error("READING_NOT_FOUND", "Reading session not found with ID: " + readingId));
        }

        String conclusion = aiConsultationService.generateConclusionQuote(
                reading.getUserQuestion(),
                reading.getDrawnCards(),
                reading.getInitialReading()
        );

        return Result.success(new ConclusionResponse(conclusion));
    }
}
