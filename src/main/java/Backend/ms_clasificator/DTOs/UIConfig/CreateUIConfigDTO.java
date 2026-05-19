package Backend.ms_clasificator.DTOs.UIConfig;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUIConfigDTO {
    @Min(message = "El ID del diagnosstico | enfermedad para la que esta diseñada la configuracion debe ser un numero positivo", value = 1)
    @NotNull(message = "El ID del diagnosstico | enfermedad para la que esta diseñada la configuracion no puede ser null")
    private Integer medicalDiagnosticId;

}
