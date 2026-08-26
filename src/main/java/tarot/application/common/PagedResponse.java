package tarot.application.common;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public record PagedResponse<T>(
    List<T> items,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize,
    boolean hasNext
) {
    public static <E, D> PagedResponse<D> fromPage(Page<E> page, Function<E, D> mapper) {
        List<D> list = page.getContent().stream().map(mapper).toList();
        return new PagedResponse<>(
            list,
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize(),
            page.hasNext()
        );
    }
}