package backend.msclasificator.dataset.application.port.out;

import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface CategoryDiagnosticRepository {

    PageResult<CategoryDiagnostic> findAll(PageQuery query);

    Optional<CategoryDiagnostic> findById(Integer id);

    List<CategoryDiagnostic> findByDatasetCategoryId(Integer datasetCategoryId);

    boolean existsByDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId);

    // Categoría del dataset a la que está asignado el diagnóstico (a lo sumo una por dataset)
    Optional<CategoryDiagnostic> findByDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    long count();

    CategoryDiagnostic save(CategoryDiagnostic categoryDiagnostic);

    void delete(CategoryDiagnostic categoryDiagnostic);
}
