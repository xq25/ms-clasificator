package backend.msclasificator.doctor.api.dto;

import backend.msclasificator.doctor.api.dto.DoctorSummaryDTO;
import backend.msclasificator.doctor.api.dto.EvaluationAreaSummaryDTO;
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

