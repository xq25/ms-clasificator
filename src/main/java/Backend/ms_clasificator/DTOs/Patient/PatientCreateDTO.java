package Backend.ms_clasificator.DTOs.Patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

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
    private Integer years;

    @NotNull(message = "El userId no puede ser nulo")
    private String userId;

}

