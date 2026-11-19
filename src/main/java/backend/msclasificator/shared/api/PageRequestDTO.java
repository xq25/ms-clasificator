package backend.msclasificator.shared.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.data.domain.Pageable;

@Data
public class PageRequestDTO {
    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(value = 100, message = "El tamaño de página no puede superar 100 registros")
    private int size = 10;

    private String sortBy    = "id";
    private String direction = "asc";

    public PageQuery toPageQuery() {
        return new PageQuery(page, size, sortBy, direction);
    }

    // Solo para servicios aún no migrados a puertos (ADR-007); eliminar al terminar la fase 2
    public Pageable toPageable() {
        return PageMapper.toPageable(toPageQuery());
    }
}
