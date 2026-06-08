package Backend.ms_clasificator.DTOs.ImageDiagnostic;

import Backend.ms_clasificator.DTOs.Doctor.DoctorSummaryDTO;
import Backend.ms_clasificator.DTOs.MedicalImg.MedicalImgSummaryDTO;
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
    private DoctorSummaryDTO doctor;
    private MedicalImgSummaryDTO medicalImg;
    private LocalDateTime diagnosticDate;
}

