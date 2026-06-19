package Backend.ms_clasificator.DTOs.Dataset;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalImageType.MedicalImageTypeSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasetResponseDTO {
    private Integer id;
    private String name;
    private MedicalDiagnosticSummaryDTO medicalDiagnostic;
    private MedicalImageTypeSummaryDTO medicalImageType;

}

