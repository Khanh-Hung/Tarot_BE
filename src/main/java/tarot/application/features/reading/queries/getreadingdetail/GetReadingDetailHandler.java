package tarot.application.features.reading.queries.getreadingdetail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.core.Reading;
import tarot.domain.entities.identity.User;
import tarot.infrastructure.ai.sanitizer.ReadingSanitizer;
import tarot.infrastructure.persistence.repositories.core.ReadingRepository;
import tarot.infrastructure.persistence.repositories.identity.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReadingDetailHandler {

    private final ReadingRepository readingRepository;
    private final UserRepository userRepository;
    private final ReadingSanitizer readingSanitizer;

    public Result<ReadingDetailResponse> handle(UUID readingId) {
        Reading reading = readingRepository.findById(readingId).orElse(null);
        if (reading == null) {
            return Result.failure(new Error("READING_NOT_FOUND", "Reading session not found with ID: " + readingId));
        }

        User user = null;
        if (reading.getUserId() != null) {
            user = userRepository.findById(reading.getUserId()).orElse(null);
        }

        String sanitizedReading = readingSanitizer.sanitize(reading.getInitialReading(), user);
        return Result.success(ReadingDetailResponse.fromEntity(reading, sanitizedReading));
    }
}