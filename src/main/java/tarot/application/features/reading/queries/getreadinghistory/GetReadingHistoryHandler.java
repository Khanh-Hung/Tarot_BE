package tarot.application.features.reading.queries.getreadinghistory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.PagedResponse;
import tarot.application.common.result.Result;
import tarot.domain.entities.Reading;
import tarot.infrastructure.persistence.repositories.ReadingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReadingHistoryHandler {

    private final ReadingRepository readingRepository;

    public Result<PagedResponse<ReadingSummaryResponse>> handle(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reading> paged = readingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        PagedResponse<ReadingSummaryResponse> response = PagedResponse.fromPage(paged, ReadingSummaryResponse::fromEntity);
        return Result.success(response);
    }
}