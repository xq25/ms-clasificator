package backend.msclasificator.doctor.application.port.out;

import backend.msclasificator.doctor.domain.EvaluationArea;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface EvaluationAreaRepository {

    PageResult<EvaluationArea> findAll(PageQuery query);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<EvaluationArea> searchByName(String query, PageQuery pageQuery);

    // Coincidencia parcial, sin distinguir mayúsculas/minúsculas
    PageResult<EvaluationArea> searchByCode(String query, PageQuery pageQuery);

    Optional<EvaluationArea> findById(Integer id);

    Optional<EvaluationArea> findByName(String name);

    Optional<EvaluationArea> findByCodeArea(String codeArea);

    // Áreas de evaluación que el doctor indicado aún no tiene asignadas
    List<EvaluationArea> findEvaluationAreasNotAssignedToDoctor(Integer doctorId);

    boolean existsById(Integer id);

    boolean existsByName(String name);

    boolean existsByCodeArea(String codeArea);

    long count();

    EvaluationArea save(EvaluationArea evaluationArea);

    void delete(EvaluationArea evaluationArea);
}
