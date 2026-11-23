package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
class CategoryDiagnosticPersistenceAdapter implements CategoryDiagnosticRepository {

    private final CategoryDiagnosticJpaRepository jpa;

    CategoryDiagnosticPersistenceAdapter(CategoryDiagnosticJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PageResult<CategoryDiagnostic> findAll(PageQuery query) {
        return PageMapper.toResult(jpa.findAllByActiveTrue(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<CategoryDiagnostic> findById(Integer id) {
        return jpa.findByIdAndActiveTrue(id);
    }

    @Override
    public List<CategoryDiagnostic> findByDatasetCategoryId(Integer datasetCategoryId) {
        return jpa.findByDatasetCategoryIdAndActiveTrue(datasetCategoryId);
    }

    @Override
    public boolean existsByDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId) {
        return jpa.existsByDatasetCategoryDatasetIdAndMedicalDiagnosticIdAndActiveTrueAndDatasetCategoryActiveTrue(datasetId, medicalDiagnosticId);
    }

    @Override
    public Optional<CategoryDiagnostic> findByDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId) {
        return jpa.findByDatasetCategoryDatasetIdAndMedicalDiagnosticIdAndActiveTrueAndDatasetCategoryActiveTrue(datasetId, medicalDiagnosticId);
    }

    @Override
    public boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.existsByMedicalDiagnosticId(medicalDiagnosticId);
    }

    @Override
    public List<CategoryDiagnostic> findDefinitionAt(Integer datasetCategoryId, LocalDateTime at) {
        return jpa.findDefinitionAt(datasetCategoryId, at);
    }

    @Override
    public long count() {
        return jpa.countByActiveTrue();
    }

    @Override
    public CategoryDiagnostic save(CategoryDiagnostic categoryDiagnostic) {
        return jpa.save(categoryDiagnostic);
    }
}
