package Backend.ms_clasificator.DTOs.MedicalImageType;

import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImageTypeResponseDTO {
    private Integer id;
    private String name;
    private EvaluationAreaSummaryDTO evaluationArea;
}

