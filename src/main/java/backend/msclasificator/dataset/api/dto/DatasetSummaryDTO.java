package backend.msclasificator.dataset.api.dto;

import backend.msclasificator.diagnosiscatalog.api.dto.MedicalDiagnosticSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetSummaryDTO {

    private Integer id;
    private String name;
    private MedicalDiagnosticSummaryDTO medicalDiagnostic;

}
