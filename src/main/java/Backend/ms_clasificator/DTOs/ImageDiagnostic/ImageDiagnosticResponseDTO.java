package Backend.ms_clasificator.DTOs.ImageDiagnostic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDiagnosticResponseDTO {
    private Integer id;
    private Integer doctorId;
    private Integer medicalImgId;
    private LocalDateTime diagnosticDate;
}

