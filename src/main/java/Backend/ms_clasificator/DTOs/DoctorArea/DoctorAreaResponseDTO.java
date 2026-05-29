package Backend.ms_clasificator.DTOs.DoctorArea;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAreaResponseDTO {
    private Integer id;
    private Integer doctorId;
    private Integer evaluationAreaId;
}

