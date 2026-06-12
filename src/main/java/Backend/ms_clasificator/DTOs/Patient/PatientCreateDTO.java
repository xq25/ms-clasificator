package Backend.ms_clasificator.DTOs.Patient;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientCreateDTO {

    @NotBlank(message = "El documento del paciente no puede estar vacío")
    @Pattern(regexp = "^(TI|CC)\\d{6,11}$", message = "El documento debe comenzar con TI o CC seguido de 6 a 11 dígitos (ejemplo: TI123456, CC12345678901)")
    private String document;

    @NotNull(message = "La edad del paciente no puede ser nula")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @NotNull(message = "La información de sexo del paciente no puede ser nula")
    @NotBlank(message = "La información de sexo del paciente no puede estar vacía")
    private String sex;

    @NotNull(message = "El userId no puede ser nulo")
    @NotBlank(message = "El userId no puede estar vacío")
    private String userId;

}

