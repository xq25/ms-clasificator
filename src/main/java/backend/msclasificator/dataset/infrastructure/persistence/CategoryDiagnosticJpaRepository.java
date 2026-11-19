package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryDiagnosticJpaRepository extends JpaRepository<CategoryDiagnostic, Integer> {
    List<CategoryDiagnostic> findByDatasetCategoryId(Integer datasetCategoryId);

    List<CategoryDiagnostic> findByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByDatasetCategoryDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    @Query("SELECT COUNT(dcd) FROM CategoryDiagnostic dcd")
    long countAll();
}
