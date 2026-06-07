package Backend.ms_clasificator.DTOs.Patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSummaryDTO {

    private Integer id;
    private String document;
    private Integer years;
    private String userId;

}
