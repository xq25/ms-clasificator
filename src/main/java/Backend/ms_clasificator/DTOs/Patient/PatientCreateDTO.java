package Backend.ms_clasificator.DTOs.Patient;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientCreateDTO {

    @NotBlank(message = "El documento del paciente no puede estar vacío")
    @Pattern(regexp = "^(TI|CC)\\d{6,11}$", message = "El documento debe comenzar con TI o CC seguido de 6 a 11 dígitos (ejemplo: TI123456, CC12345678901)")
    private String document;

    @NotNull(message = "La edad del paciente no puede ser nula")
    @Positive(message = "La edad debe ser un número positivo")
    @Max(message = "La edad no puede ser mayor a 120 años", value = 120)
    private Integer years;

    @NotNull(message = "El userId no puede ser nulo")
    @NotBlank(message = "El userId no puede estar vacío")
    private String userId;

}

