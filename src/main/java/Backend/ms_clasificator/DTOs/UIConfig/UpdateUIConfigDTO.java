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
public class UpdateUIConfigDTO {
    @NotNull(message = "El ID del diagnóstico médico para la que está diseñada la configuración no puede ser null")
    private Integer medicalDiagnosticId;
}
