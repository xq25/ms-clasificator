package backend.msclasificator.dataset.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetCategorySummaryDTO {

    private Integer id;
    private String name;
    private Integer numValue;

}
