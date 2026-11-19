package backend.msclasificator.classification.api.dto;

import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationDiagnosisSummaryDTO {

    private Integer id;
    private Integer imageDiagnosticId;
    private MedicalDiagnosticSummaryDTO medicalDiagnostic;

}
