package backend.msclasificator.medicalimage.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalImageTypeSummaryDTO {

    private Integer id;
    private String name;

    // false si el tipo está retirado (soft delete, ADR-013)
    private Boolean active;

}
