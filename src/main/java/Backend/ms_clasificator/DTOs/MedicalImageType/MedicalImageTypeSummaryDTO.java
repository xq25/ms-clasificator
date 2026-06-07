package Backend.ms_clasificator.DTOs.MedicalImageType;

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

}
