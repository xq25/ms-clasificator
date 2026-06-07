package Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset;

import Backend.ms_clasificator.DTOs.DatasetCategory.DatasetCategorySummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticCategoryDatasetResponseDTO {
    private Integer id;

    private DatasetCategorySummaryDTO datasetCategory;

    private MedicalDiagnosticSummaryDTO medicalDiagnostic;

}
