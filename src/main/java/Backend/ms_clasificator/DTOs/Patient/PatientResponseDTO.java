package Backend.ms_clasificator.DTOs.Patient;

import Backend.ms_clasificator.Models.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {
    private Integer id;
    private String document;
    private Integer years;
    private String userId;
    private UserInfo userInfo;

}

