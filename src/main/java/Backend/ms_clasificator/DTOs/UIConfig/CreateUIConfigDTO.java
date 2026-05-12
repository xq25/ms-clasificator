package Backend.ms_clasificator.DTOs.UIConfig;

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
    @NotNull(message = "El ID del diagnosstico | enfermedad para la que esta diseñada la configuracion no puede ser null")
    private Integer medicalDiagnosticId;
}
