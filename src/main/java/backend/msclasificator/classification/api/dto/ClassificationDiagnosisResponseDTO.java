package backend.msclasificator.classification.api.dto;

import backend.msclasificator.classification.api.dto.ImageClassificationSummaryDTO;
import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationDiagnosisResponseDTO {
    private Integer id;
    private ImageClassificationSummaryDTO imageDiagnostic;
    private MedicalDiagnosticSummaryDTO medicalDiagnostic;

}
