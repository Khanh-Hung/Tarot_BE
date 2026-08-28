package tarot.application.features.reading.queries.getreadingdetail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.Reading;
import tarot.infrastructure.persistence.repositories.ReadingRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReadingDetailHandler {

    private final ReadingRepository readingRepository;

    public Result<ReadingDetailResponse> handle(UUID readingId) {
        Reading reading = readingRepository.findById(readingId).orElse(null);
        if (reading == null) {
            return Result.failure(new Error("READING_NOT_FOUND", "Reading session not found with ID: " + readingId));
        }

        return Result.success(ReadingDetailResponse.fromEntity(reading));
    }
}