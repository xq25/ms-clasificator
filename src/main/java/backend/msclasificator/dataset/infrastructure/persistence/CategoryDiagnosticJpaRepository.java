package backend.msclasificator.dataset.infrastructure.persistence;

import backend.msclasificator.dataset.domain.CategoryDiagnostic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CategoryDiagnosticJpaRepository extends JpaRepository<CategoryDiagnostic, Integer> {

    Page<CategoryDiagnostic> findAllByActiveTrue(Pageable pageable);

    Optional<CategoryDiagnostic> findByIdAndActiveTrue(Integer id);

    List<CategoryDiagnostic> findByDatasetCategoryIdAndActiveTrue(Integer datasetCategoryId);

    boolean existsByDatasetCategoryDatasetIdAndMedicalDiagnosticIdAndActiveTrueAndDatasetCategoryActiveTrue(
            Integer datasetId, Integer medicalDiagnosticId);

    Optional<CategoryDiagnostic> findByDatasetCategoryDatasetIdAndMedicalDiagnosticIdAndActiveTrueAndDatasetCategoryActiveTrue(
            Integer datasetId, Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticIdAndActiveTrueAndDatasetCategoryActiveTrue(Integer medicalDiagnosticId);

    @Query("""
        SELECT cd
        FROM CategoryDiagnostic cd
        WHERE cd.datasetCategory.id = :datasetCategoryId
          AND cd.createdAt <= :at
          AND (cd.deletedAt IS NULL OR cd.deletedAt > :at)
    """)
    List<CategoryDiagnostic> findDefinitionAt(@Param("datasetCategoryId") Integer datasetCategoryId, @Param("at") LocalDateTime at);

    long countByActiveTrue();
}
