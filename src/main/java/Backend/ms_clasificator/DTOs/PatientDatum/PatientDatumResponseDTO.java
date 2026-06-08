package Backend.ms_clasificator.DTOs.PatientDatum;

import Backend.ms_clasificator.DTOs.PrimitiveDatum.PrimitiveDatumSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDatumResponseDTO {
    private Integer id;
    private String description;
    private PrimitiveDatumSummaryDTO primitiveDatum;
    private Integer clinicalRecordId;
}

