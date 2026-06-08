package Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics;

import Backend.ms_clasificator.DTOs.MedicalDiagnostic.MedicalDiagnosticSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDoctorDiagnosticsSummaryDTO {

    private Integer id;
    private Integer imageDiagnosticId;
    private MedicalDiagnosticSummaryDTO medicalDiagnostic;

}
