package backend.msclasificator.doctor.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSummaryDTO {

    private Integer id;
    private String code;
    private String userId;

}
