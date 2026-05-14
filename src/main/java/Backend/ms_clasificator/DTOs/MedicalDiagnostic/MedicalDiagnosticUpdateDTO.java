package Backend.ms_clasificator.DTOs.MedicalDiagnostic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDiagnosticUpdateDTO extends MedicalDiagnosticCreateDTO{

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

