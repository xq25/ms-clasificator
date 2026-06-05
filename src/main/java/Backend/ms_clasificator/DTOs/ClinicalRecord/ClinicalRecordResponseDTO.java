package Backend.ms_clasificator.DTOs.ClinicalRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalRecordResponseDTO {
    private Integer id;
    private Date visitDate;
}

