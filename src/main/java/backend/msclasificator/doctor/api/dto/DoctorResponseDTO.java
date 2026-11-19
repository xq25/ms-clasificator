package backend.msclasificator.doctor.api.dto;

import backend.msclasificator.identity.domain.UserInfo;
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

