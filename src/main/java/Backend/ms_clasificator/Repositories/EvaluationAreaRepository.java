package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.EvaluationArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationAreaRepository extends JpaRepository<EvaluationArea, Integer> {
    EvaluationArea findByName(String name);
    EvaluationArea findByCodeArea(String codeArea);

    boolean existByName(String name);
    boolean existByCodeArea(String codeArea);
}
