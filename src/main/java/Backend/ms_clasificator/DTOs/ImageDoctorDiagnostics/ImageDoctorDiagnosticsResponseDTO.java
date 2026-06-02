package Backend.ms_clasificator.DTOs.ImageDoctorDiagnostics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDoctorDiagnosticsResponseDTO {
    private Integer id;

    private Integer imageDiagnosticId;

    private Integer medicalDiagnosticId;

    private String medicalDiagnosticName;

}
