package backend.msclasificator.clinicalrecord.api.dto;

import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
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
