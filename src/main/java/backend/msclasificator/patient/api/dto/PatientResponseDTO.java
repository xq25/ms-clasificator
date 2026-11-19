package backend.msclasificator.patient.api.dto;

import backend.msclasificator.identity.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {
    private Integer id;
    private String document;
    private Date dob;
    private String sex;
    private String userId;
    private UserInfo userInfo;

}

