package backend.msclasificator.dataset.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDiagnosticUpdateDTO {
    private Integer datasetCategoryId;
    private Integer medicalDiagnosticId;
}
