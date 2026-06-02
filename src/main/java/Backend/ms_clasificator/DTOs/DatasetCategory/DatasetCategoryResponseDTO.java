package Backend.ms_clasificator.DTOs.DatasetCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasetCategoryResponseDTO {
    private Integer id;
    private Integer datasetId;
    private Integer numValue;
}

