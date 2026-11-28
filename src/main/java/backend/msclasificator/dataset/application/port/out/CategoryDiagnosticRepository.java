package backend.msclasificator.dataset.application.port.out;

import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Las lecturas consideran solo asignaciones activas de categorías activas, salvo las indicadas (ADR-011).
public interface CategoryDiagnosticRepository {

    PageResult<CategoryDiagnostic> findAll(PageQuery query);

    Optional<CategoryDiagnostic> findById(Integer id);

    List<CategoryDiagnostic> findByDatasetCategoryId(Integer datasetCategoryId);

    boolean existsByDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId);

    // Categoría activa del dataset a la que está asignado el diagnóstico (a lo sumo una)
    Optional<CategoryDiagnostic> findByDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId);

    // Diagnóstico que define alguna categoría ACTIVA (asignación activa en categoría activa): bloquea su retiro (ADR-013)
    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    // Incluye retiradas: un sub-diagnóstico que alguna vez definió una categoría no se separa de su padre
    boolean existsByMedicalDiagnosticIdIncludingRetired(Integer medicalDiagnosticId);

    // Definición de la categoría vigente en un instante: asignaciones creadas antes y no retiradas hasta entonces
    List<CategoryDiagnostic> findDefinitionAt(Integer datasetCategoryId, LocalDateTime at);

    long count();

    CategoryDiagnostic save(CategoryDiagnostic categoryDiagnostic);
}
