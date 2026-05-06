package Backend.ms_clasificator.DTOs.MedicalDiagnostic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDiagnosticUpdateDTO extends MedicalDiagnosticCreateDTO{

    @NotNull(message = "El ID del diagnóstico es requerido para actualizar")
    private Integer id;

}

