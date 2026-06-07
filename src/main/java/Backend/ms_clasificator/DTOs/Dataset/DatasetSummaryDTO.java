package Backend.ms_clasificator.DTOs.Dataset;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
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
