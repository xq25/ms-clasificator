package Backend.ms_clasificator.DTOs.DoctorArea;

import Backend.ms_clasificator.DTOs.Doctor.DoctorSummaryDTO;
import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAreaResponseDTO {
    private Integer id;
    private DoctorSummaryDTO doctor;
    private EvaluationAreaSummaryDTO evaluationArea;
}

