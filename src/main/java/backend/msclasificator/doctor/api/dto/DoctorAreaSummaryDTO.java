package backend.msclasificator.doctor.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAreaSummaryDTO {

    private Integer id;
    private Integer doctorId;
    private Integer evaluationAreaId;

}
