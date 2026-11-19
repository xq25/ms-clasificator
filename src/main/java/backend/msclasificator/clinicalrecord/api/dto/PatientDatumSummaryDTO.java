package backend.msclasificator.clinicalrecord.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDatumSummaryDTO {

    private Integer id;
    private String description;
    private Integer primitiveDatumId;
    private Integer clinicalRecordId;

}
