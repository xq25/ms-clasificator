package Backend.ms_clasificator.DTOs.ImageDiagnostic;

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
public class ImageDiagnosticUpdateDTO {

    @NotNull
    @Min(message = "El ID del diagnóstico de imagen debe ser un número positivo", value = 1)
    private Integer medicalDiagnosticId;
}
