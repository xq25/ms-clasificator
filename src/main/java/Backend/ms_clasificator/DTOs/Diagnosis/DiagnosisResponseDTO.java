package Backend.ms_clasificator.DTOs.Diagnosis;

import Backend.ms_clasificator.DTOs.ClinicalRecord.ClinicalRecordSummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
import Backend.ms_clasificator.Models.MedicalDiagnostic;
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

