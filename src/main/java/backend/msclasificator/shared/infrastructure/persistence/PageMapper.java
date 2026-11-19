package backend.msclasificator.shared.infrastructure.persistence;

import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageMapper {

    private PageMapper() {
    }

    public static Pageable toPageable(PageQuery query) {
        Sort sort = query.direction().equalsIgnoreCase("desc")
                ? Sort.by(query.sortBy()).descending()
                : Sort.by(query.sortBy()).ascending();
        return PageRequest.of(query.page(), query.size(), sort);
    }

    public static <T> PageResult<T> toResult(Page<T> page) {
        return new PageResult<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
