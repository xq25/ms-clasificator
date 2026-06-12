package Backend.ms_clasificator.DTOs.Doctor;

import Backend.ms_clasificator.Models.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponseDTO {

    private Integer id;
    private String code;
    private String userId;
    private UserInfo userInfo;

}

