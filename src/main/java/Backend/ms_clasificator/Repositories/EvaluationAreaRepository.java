package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.EvaluationArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationAreaRepository extends JpaRepository<EvaluationArea, Integer> {
    // Spring Data JPA proporciona automáticamente:
    // - findById(Integer)
    // - findAll()
    // - save()
    // - delete()
    // - etc.
}
