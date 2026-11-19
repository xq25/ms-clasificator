package backend.msclasificator.clinicalrecord.api.dto;

import backend.msclasificator.clinicalrecord.api.dto.ClinicalRecordSummaryDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import backend.msclasificator.diagnosiscatalog.domain.MedicalDiagnostic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisResponseDTO {
    private Integer id;
    private MedicalDiagnosticSummaryDTO medicalDiagnostic;
    private ClinicalRecordSummaryDTO clinicalrecord;
}

