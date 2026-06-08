package Backend.ms_clasificator.DTOs.Patient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// Solo se pueden actualizar la edad y el documento.
public class PatientUpdateDTO {
    @NotNull
    private Integer years;

    @NotNull
    @Pattern(regexp = "^(TI|CC)\\d{6,11}$", message = "El documento debe comenzar con TI o CC seguido de 6 a 11 dígitos (ejemplo: TI123456, CC12345678901)")
    private String document;

}

