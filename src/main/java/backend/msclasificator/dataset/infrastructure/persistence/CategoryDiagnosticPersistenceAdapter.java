package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.application.port.out.CategoryDiagnosticRepository;
import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import backend.msclasificator.shared.infrastructure.persistence.PageMapper;
import org.springframework.stereotype.Component;

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
        return PageMapper.toResult(jpa.findAll(PageMapper.toPageable(query)));
    }

    @Override
    public Optional<CategoryDiagnostic> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<CategoryDiagnostic> findByDatasetCategoryId(Integer datasetCategoryId) {
        return jpa.findByDatasetCategoryId(datasetCategoryId);
    }

    @Override
    public boolean existsByDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId) {
        return jpa.existsByDatasetCategoryDatasetIdAndMedicalDiagnosticId(datasetId, medicalDiagnosticId);
    }

    @Override
    public boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId) {
        return jpa.existsByMedicalDiagnosticId(medicalDiagnosticId);
    }

    @Override
    public long count() {
        return jpa.countAll();
    }

    @Override
    public CategoryDiagnostic save(CategoryDiagnostic categoryDiagnostic) {
        return jpa.save(categoryDiagnostic);
    }

    @Override
    public void delete(CategoryDiagnostic categoryDiagnostic) {
        jpa.delete(categoryDiagnostic);
    }
}
