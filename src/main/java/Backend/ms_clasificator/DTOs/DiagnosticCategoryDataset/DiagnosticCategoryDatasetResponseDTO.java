package Backend.ms_clasificator.DTOs.DiagnosticCategoryDataset;

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

    private Integer datasetCategoryId;

    private Integer medicalDiagnosticId;

    private String medicalDiagnosticCode;

}
