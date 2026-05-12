package Backend.ms_clasificator.DTOs.Doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DoctorUpdateDTO {
    @NotBlank(message = "El código del doctor no puede estar vacío")
    @NotNull(message = "El código del doctor no puede ser nulo")
    private String code;
}
