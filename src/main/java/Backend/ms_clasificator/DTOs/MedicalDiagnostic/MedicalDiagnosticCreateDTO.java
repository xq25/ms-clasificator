package Backend.ms_clasificator.DTOs.MedicalDiagnostic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalDiagnosticCreateDTO {

    @NotBlank(message = "El código del diagnóstico no puede estar vacío")
    @Size(min = 3, max = 8, message = "El código del diagnóstico debe tener mínimo 3 caracteres")
    @Pattern(regexp = "^[A-Za-z]\\d{2}.*$", message = "El código debe comenzar con una letra seguida de exactamente dos números (ejemplo: A12, B99XYZ)")
    private String diagnosticCode;

    //Importante para el manejo del sistema para personas no especializadas en areas medicas
    @NotBlank(message = "El nombre del diagnóstico no puede estar vacío")
    private String diagnosticName;

    // ID del diagnóstico padre (opcional - nullable si es diagnosis raíz)
    private Integer parentDiagnosticId;
}

