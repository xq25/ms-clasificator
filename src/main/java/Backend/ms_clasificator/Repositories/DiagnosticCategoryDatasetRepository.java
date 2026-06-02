package Backend.ms_clasificator.Repositories;

import Backend.ms_clasificator.Models.DiagnosticCategoryDataset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticCategoryDatasetRepository extends JpaRepository<DiagnosticCategoryDataset, Integer> {
    List<DiagnosticCategoryDataset> findByDatasetCategoryId(Integer datasetCategoryId);
    boolean existsByDatasetCategory_DatasetIdAndMedicalDiagnosticId(Integer datasetId, Integer medicalDiagnosticId);
}
