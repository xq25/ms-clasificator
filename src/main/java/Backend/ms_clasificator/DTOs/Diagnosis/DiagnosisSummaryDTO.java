package Backend.ms_clasificator.DTOs.Diagnosis;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisSummaryDTO {

    private Integer id;
    private MedicalDiagnosticSummaryDTO medicalDiagnostic;

}
