package Backend.ms_clasificator.DTOs.MedicalDiagnostic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDiagnosticSummaryDTO {

    private Integer id;
    private String diagnosticCode;
    private String diagnosticName;
}
