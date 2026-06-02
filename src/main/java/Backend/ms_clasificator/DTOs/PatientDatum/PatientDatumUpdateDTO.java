package Backend.ms_clasificator.DTOs.PatientDatum;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientDatumUpdateDTO {
    @NotNull(message = "La descripcion dada por el medico no puede ser nula") // Pero si puede ser blank
    private String description;
}


