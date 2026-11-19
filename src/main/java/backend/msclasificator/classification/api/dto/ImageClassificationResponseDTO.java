package backend.msclasificator.classification.api.dto;

import backend.msclasificator.doctor.api.dto.DoctorSummaryDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageClassificationResponseDTO {
    private Integer id;
    private DoctorSummaryDTO doctor;
    private MedicalImageSummaryDTO medicalImg;
    private LocalDateTime diagnosticDate;
}

