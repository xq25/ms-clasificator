package Backend.ms_clasificator.DTOs.PatientDatum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDatumCreateDTO {

    @NotNull(message = "La descripcion dada por el medico no puede ser nula") // Pero si puede ser blank
    private String description;

    @NotNull(message = "El clinicalRecordId no puede ser nulo")
    private Integer clinicalRecordId;

    @NotNull(message = "El primitiveDatumId no puede ser nulo")
    private Integer primitiveDatumId;
}


