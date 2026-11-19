package backend.msclasificator.dataset.api.dto;

import backend.msclasificator.dataset.api.dto.DatasetCategorySummaryDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDiagnosticResponseDTO {
    private Integer id;

    private DatasetCategorySummaryDTO datasetCategory;

    private MedicalDiagnosticSummaryDTO medicalDiagnostic;

}
