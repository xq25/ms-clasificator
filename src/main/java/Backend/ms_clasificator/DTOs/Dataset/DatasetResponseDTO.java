package Backend.ms_clasificator.DTOs.Dataset;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticResponseDTO;
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
    private MedicalDiagnosticResponseDTO medicalDiagnostic;
    private Integer evaluationAreaId;
    private String evaluationAreaName;
}

