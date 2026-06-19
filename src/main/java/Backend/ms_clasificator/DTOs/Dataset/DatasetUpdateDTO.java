package Backend.ms_clasificator.DTOs.Dataset;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasetUpdateDTO {
    @NotNull(message = "El nombre del dataset no puede ser null")
    @NotBlank(message = "El nombre del dataset no puede estar vacío")
    private String name;

    @Min(message = "El ID del diagnóstico médico para la que está diseñada la configuración debe ser un número positivo", value = 1)
    @NotNull(message = "El ID del diagnóstico médico para la que está diseñada la configuración no puede ser null")
    private Integer medicalDiagnosticId;

    @NotNull(message = "El ID del tipo de imagen a clasificar no puede ser null")
    private Integer medicalImageTypeId;
}
