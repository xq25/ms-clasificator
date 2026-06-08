package Backend.ms_clasificator.DTOs.MedicalDiagnostic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalDiagnosticResponseDTO {
    private Integer id;
    private String diagnosticCode;
    private String diagnosticName;
    private MedicalDiagnosticSummaryDTO parentDiagnostic;
}

