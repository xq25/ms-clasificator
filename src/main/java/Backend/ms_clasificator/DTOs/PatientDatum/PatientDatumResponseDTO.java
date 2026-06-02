package Backend.ms_clasificator.DTOs.PatientDatum;

import Backend.ms_clasificator.Models.PrimitiveDatum;

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
    private String name;
    private String description;
    private PrimitiveDatum primitiveDatum;
}

