package Backend.ms_clasificator.DTOs.Doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorCreateDTO {

    @NotBlank(message = "El código del doctor no puede estar vacío")
    private String code;

    @NotNull(message = "El user_id no puede ser nulo")
    private UUID user_id;


}
