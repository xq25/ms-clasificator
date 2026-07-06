package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.DiagnosticCategoryDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiagnosticCategoryDatasetRepository extends JpaRepository<DiagnosticCategoryDataset, Integer> {
    List<DiagnosticCategoryDataset> findByDatasetCategoryId(Integer datasetCategoryId);

    List<DiagnosticCategoryDataset> findByMedicalDiagnosticId(Integer medicalDiagnosticId);

    boolean existsByDatasetCategoryDatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId);

    boolean existsByMedicalDiagnosticId(Integer medicalDiagnosticId);

    @Query("SELECT COUNT(dcd) FROM DiagnosticCategoryDataset dcd")
    long countAll();
}
