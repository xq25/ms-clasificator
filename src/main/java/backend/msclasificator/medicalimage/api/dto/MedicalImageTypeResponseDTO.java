package backend.msclasificator.medicalimage.api.dto;

import backend.msclasificator.doctor.api.dto.EvaluationAreaSummaryDTO;
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

    // false si el tipo está retirado (soft delete, ADR-013)
    private Boolean active;
    private EvaluationAreaSummaryDTO evaluationArea;
}

